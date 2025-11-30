package com.childofweather.controller;

import com.childofweather.util.GridAddressLoader;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@WebServlet("/weather/short")
public class WeatherServlet extends HttpServlet {

    // 기상청 초단기실황 조회 URL
    private static final String API_URL =
            "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

    private static final String API_URL_VILAGE =
            "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    // 공공데이터포털 서비스 키 (디코딩 전 원본값)
    private static final String SERVICE_KEY = "14240190568d9ff24788ff0a3b608e01a5b605c51d10dda828d764a6cb655c2f";

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. 파라미터 읽기
        String latParam = request.getParameter("lat");
        String lonParam = request.getParameter("lon");

        if (latParam == null || lonParam == null) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "lat, lon 파라미터가 필요합니다.");
            return;
        }

        double lat;
        double lon;
        try {
            lat = Double.parseDouble(latParam);
            lon = Double.parseDouble(lonParam);
        } catch (NumberFormatException e) {
            writeError(response, HttpServletResponse.SC_BAD_REQUEST,
                    "lat, lon 파라미터 형식이 잘못되었습니다.");
            return;
        }

        // 2. 위경도 → 기상청 격자
        GridPoint grid = convertToGrid(lat, lon);

        // 3. 기준 시각 계산
        BaseDateTime baseNow = calcBaseDateTime();              // 초단기실황용
        BaseDateTime baseVilage = calcBaseDateTimeForVilage();  // 단기예보용

        try {
            // 4-1. 현재 실황(getUltraSrtNcst)
            String xmlNow = callKmaApi(baseNow.baseDate, baseNow.baseTime, grid.nx, grid.ny);
            WeatherData weather = parseUltraSrtNcstXml(xmlNow);

            // 4-2. 단기예보(getVilageFcst)
            String today = java.time.LocalDate
                    .now(java.time.ZoneId.of("Asia/Seoul"))
                    .format(DATE_FMT);

            String xmlVilage = callKmaVilageApi(baseVilage.baseDate, baseVilage.baseTime, grid.nx, grid.ny);
            java.util.List<HourlyForecast> hourly = parseVilageFcstXml(xmlVilage, today);

            // ============================
            // ★★★ 3시간 간격 시간 필터링 추가 ★★★
            // ============================

            // 현재 시간
            int nowHour = java.time.LocalDateTime
                    .now(java.time.ZoneId.of("Asia/Seoul"))
                    .getHour();

            // 3시간 간격 후보 시간 생성
            java.util.Set<String> targetTimes = new java.util.LinkedHashSet<>();
            for (int h = nowHour; h <= nowHour + 12; h += 3) {
                int hour = h % 24;  // 24 넘어가면 0~ 처리
                String key = String.format("%02d00", hour);  // "0900"
                targetTimes.add(key);
            }

            // hourly 리스트 필터링
            java.util.List<HourlyForecast> filtered = new java.util.ArrayList<>();
            for (HourlyForecast hf : hourly) {
                // "09:00" → "0900"
                if (hf.time != null && hf.time.length() >= 2) {
                    String timeKey = hf.time.substring(0, 2) + "00";
                    if (targetTimes.contains(timeKey)) {
                        filtered.add(hf);
                    }
                }
            }

            hourly = filtered; // filtered 리스트로 교체

            // ============================

            // 5. 위치 이름은 위도/경도로 표시
            String key = grid.nx + "-" + grid.ny;
            String locationName = GridAddressLoader.getMap()
                    .getOrDefault(key, String.format("위도 %.3f, 경도 %.3f", lat, lon));


            // 6. JSON 응답
            response.setContentType("application/json; charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                String json = buildJson(locationName, weather, hourly);
                out.write(json);
            }

        } catch (Exception e) {
            e.printStackTrace();
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "기상청 API 호출 중 오류가 발생했습니다.");
        }
    }



    // ========== 공통 유틸 ==========

    private void writeError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String safeMsg = message.replace("\"", "\\\"");
            out.write("{\"error\":\"" + safeMsg + "\"}");
        }
    }

    private BaseDateTime calcBaseDateTime() {
        // 기준: 현재 시간 기준, 한 시간 전 정시
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime baseTime = now.minusHours(1)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        BaseDateTime result = new BaseDateTime();
        result.baseDate = baseTime.format(DATE_FMT);
        result.baseTime = baseTime.format(TIME_FMT);
        return result;
    }

    private BaseDateTime calcBaseDateTimeForVilage() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        int[] baseHours = {2, 5, 8, 11, 14, 17, 20, 23};
        int hour = now.getHour();

        LocalDateTime base = now;
        if (hour < 2) {
            // 새벽 0~1시는 전날 23시 기준
            base = now.minusDays(1).withHour(23);
        } else {
            int chosen = 2;
            for (int h : baseHours) {
                if (hour >= h) {
                    chosen = h;
                }
            }
            base = now.withHour(chosen);
        }

        base = base.withMinute(0).withSecond(0).withNano(0);

        BaseDateTime result = new BaseDateTime();
        result.baseDate = base.format(DATE_FMT);
        result.baseTime = base.format(TIME_FMT);
        return result;
    }


    private String callKmaApi(String baseDate, String baseTime, int nx, int ny) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(API_URL);
        urlBuilder.append("?")
                .append("ServiceKey=").append(URLEncoder.encode(SERVICE_KEY, "UTF-8"))
                .append("&pageNo=1")
                .append("&numOfRows=1000")
                .append("&dataType=XML")
                .append("&base_date=").append(baseDate)
                .append("&base_time=").append(baseTime)
                .append("&nx=").append(nx)
                .append("&ny=").append(ny);

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/xml");

        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }

    private String callKmaVilageApi(String baseDate, String baseTime, int nx, int ny) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(API_URL_VILAGE);
        urlBuilder.append("?")
                .append("ServiceKey=").append(URLEncoder.encode(SERVICE_KEY, "UTF-8"))
                .append("&pageNo=1")
                .append("&numOfRows=1000")
                .append("&dataType=XML")
                .append("&base_date=").append(baseDate)
                .append("&base_time=").append(baseTime)
                .append("&nx=").append(nx)
                .append("&ny=").append(ny);

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/xml");

        int responseCode = conn.getResponseCode();
        InputStream is = (responseCode == 200) ? conn.getInputStream() : conn.getErrorStream();

        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        } finally {
            conn.disconnect();
        }
    }

    private WeatherData parseUltraSrtNcstXml(String xml) throws Exception {
        WeatherData data = new WeatherData();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        try (InputStream is = new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String category = getText(item, "category");
                String valueStr = getText(item, "obsrValue");
                if (valueStr == null || valueStr.isEmpty()) {
                    continue;
                }

                double value;
                try {
                    value = Double.parseDouble(valueStr);
                } catch (NumberFormatException e) {
                    continue;
                }

                if ("T1H".equals(category)) {          // 기온(℃)
                    data.temperature = value;
                } else if ("REH".equals(category)) {   // 습도(%)
                    data.humidity = value;
                } else if ("WSD".equals(category)) {   // 풍속(m/s)
                    data.windSpeed = value;
                } else if ("RN1".equals(category)) {   // 1시간 강수량(mm)
                    data.rain1h = value;
                } else if ("PTY".equals(category)) {   // 강수형태 코드
                    data.pty = (int) value;
                }
            }

            data.conditionText = toConditionText(data.pty, data.rain1h);
            data.precipitationProb = (data.rain1h > 0.0) ? 100 : 0; // 임시 로직
            data.statusMessage = buildStatusMessage(data);
        }

        return data;
    }

    private java.util.List<HourlyForecast> parseVilageFcstXml(String xml, String targetDate) throws Exception {
        java.util.Map<String, HourlyForecast> map = new java.util.LinkedHashMap<>();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        try (InputStream is = new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
            Document doc = db.parse(is);
            doc.getDocumentElement().normalize();

            NodeList items = doc.getElementsByTagName("item");
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);

                String fcstDate = getText(item, "fcstDate");
                String fcstTime = getText(item, "fcstTime"); // "0900" 형태
                String category = getText(item, "category");
                String valueStr = getText(item, "fcstValue");

                if (fcstDate == null || fcstTime == null || category == null || valueStr == null) {
                    continue;
                }
                if (!fcstDate.equals(targetDate)) {
                    continue; // 오늘 데이터만 사용
                }

                HourlyForecast hf = map.get(fcstTime);
                if (hf == null) {
                    hf = new HourlyForecast();
                    // "0900" -> "09:00"
                    hf.time = fcstTime.substring(0, 2) + ":" + fcstTime.substring(2, 4);
                    map.put(fcstTime, hf);
                }

                if ("TMP".equals(category) || "T1H".equals(category)) {
                    try {
                        hf.temperature = Double.parseDouble(valueStr);
                    } catch (NumberFormatException ignored) {}
                } else if ("PTY".equals(category)) {
                    try {
                        hf.pty = (int) Double.parseDouble(valueStr);
                    } catch (NumberFormatException ignored) {}
                } else if ("POP".equals(category)) {
                    try {
                        hf.pop = (int) Double.parseDouble(valueStr);
                    } catch (NumberFormatException ignored) {}
                }
            }
        }

        java.util.List<HourlyForecast> list = new java.util.ArrayList<>(map.values());

        // fcstTime 기준으로 정렬
        java.util.Collections.sort(list, new java.util.Comparator<HourlyForecast>() {
            @Override
            public int compare(HourlyForecast o1, HourlyForecast o2) {
                return o1.time.compareTo(o2.time);
            }
        });

        // 필요하면 여기서 상위 N개만 잘라서 반환해도 됨 (지금은 전체 반환)
        return list;
    }

    private String getText(Element parent, String tagName) {
        NodeList list = parent.getElementsByTagName(tagName);
        if (list.getLength() == 0) return null;
        if (list.item(0).getFirstChild() == null) return null;
        return list.item(0).getFirstChild().getNodeValue();
    }

    private String toConditionText(int pty, double rain1h) {
        switch (pty) {
            case 0:
                if (rain1h > 0.0) {
                    return "약한 비";
                }
                return "맑음";
            case 1:
                return "비";
            case 2:
                return "비/눈";
            case 3:
                return "눈";
            case 4:
                return "소나기";
            default:
                return "알 수 없음";
        }
    }

    private String buildStatusMessage(WeatherData data) {
        if (data.pty == 0) { // 비·눈 없음
            if (!Double.isNaN(data.temperature) && data.temperature >= 28) {
                return "덥고 습한 날씨입니다. 수분 보충과 실내 휴식을 추천합니다.";
            }
            if (!Double.isNaN(data.temperature) && data.temperature <= 3) {
                return "기온이 낮습니다. 두꺼운 겉옷을 챙기세요.";
            }
            if (!Double.isNaN(data.humidity) && data.humidity >= 80) {
                return "비는 없지만 습도가 높습니다. 실내 공기 환기에 신경 써주세요.";
            }
            return "오늘도 맑고 쾌적한 하루가 예상됩니다.";
        } else {
            switch (data.pty) {
                case 1:
                    return "비가 내리고 있습니다. 우산을 챙기고 이동 시간에 여유를 두세요.";
                case 2:
                    return "비와 눈이 섞여 내리고 있습니다. 도로가 미끄러울 수 있습니다.";
                case 3:
                    return "눈이 내리고 있습니다. 미끄럼 사고에 주의하세요.";
                case 4:
                    return "소나기가 지나는 중입니다. 짧은 시간 강한 비에 대비하세요.";
                default:
                    return "강수 형태가 불안정합니다. 최신 기상 정보를 확인하세요.";
            }
        }
    }


    private String buildJson(String locationName, WeatherData data, java.util.List<HourlyForecast> hourly) {
        String safeLocation = locationName.replace("\"", "\\\"");
        String safeCondition = data.conditionText.replace("\"", "\\\"");
        String safeStatus = data.statusMessage.replace("\"", "\\\"");

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"locationName\":\"").append(safeLocation).append("\"");
        sb.append(",\"temperature\":").append(data.temperature);
        sb.append(",\"conditionText\":\"").append(safeCondition).append("\"");
        sb.append(",\"statusMessage\":\"").append(safeStatus).append("\"");
        sb.append(",\"humidity\":").append(data.humidity);
        sb.append(",\"windSpeed\":").append(data.windSpeed);
        sb.append(",\"precipitationProb\":").append((int) data.precipitationProb);

        // ★ hourly 배열
        sb.append(",\"hourly\":[");
        if (hourly != null) {
            for (int i = 0; i < hourly.size(); i++) {
                HourlyForecast h = hourly.get(i);
                if (i > 0) sb.append(",");
                sb.append("{");
                sb.append("\"time\":\"").append(h.time).append("\"");
                sb.append(",\"temperature\":").append(h.temperature);
                sb.append(",\"pty\":").append(h.pty);
                sb.append(",\"pop\":").append(h.pop);
                sb.append("}");
            }
        }
        sb.append("]");

        sb.append("}");
        return sb.toString();
    }


    // ========== 위경도 → 기상청 격자 변환 ==========

    private GridPoint convertToGrid(double lat, double lon) {
        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0;      // 격자 간격(km)
        double SLAT1 = 30.0;    // 표준위도1
        double SLAT2 = 60.0;    // 표준위도2
        double OLON = 126.0;    // 기준점 경도
        double OLAT = 38.0;     // 기준점 위도
        double XO = 43.0;       // 기준점 X좌표
        double YO = 136.0;      // 기준점 Y좌표

        double DEGRAD = Math.PI / 180.0;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) /
                Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);

        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;

        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);

        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int nx = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
        int ny = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        GridPoint gp = new GridPoint();
        gp.nx = nx;
        gp.ny = ny;
        return gp;
    }

    // ========== 내부 데이터 클래스 ==========

    private static class GridPoint {
        int nx;
        int ny;
    }

    private static class BaseDateTime {
        String baseDate;
        String baseTime;
    }

    private static class WeatherData {
        double temperature = Double.NaN;
        double humidity = Double.NaN;
        double windSpeed = Double.NaN;
        double rain1h = 0.0;
        int pty = 0;
        double precipitationProb = 0.0;
        String conditionText = "정보 없음";
        String statusMessage = "현재 기상 정보를 불러왔습니다.";
    }

    private static class HourlyForecast {
        String time;          // "09:00"
        double temperature = Double.NaN; // TMP
        int pty = 0;          // 강수형태
        int pop = -1;         // 강수확률 (%), 없으면 -1
    }

}
