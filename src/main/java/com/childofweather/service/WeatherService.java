package com.childofweather.service;

import com.childofweather.dto.WeatherDTO;
import com.childofweather.service.provider.WeatherProvider;
import com.childofweather.util.GridAddressLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class WeatherService {

    private final WeatherProvider provider = new WeatherProvider();

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HHmm");

    /** 메인 엔트리: lat, lon → WeatherDTO.Response */
    public WeatherDTO.Response getWeather(double lat, double lon) throws Exception {

        // 1. 위/경도 → 격자
        WeatherDTO.GridPoint grid = convertToGrid(lat, lon);

        // 2. 기준 시각 계산
        WeatherDTO.BaseDateTime baseNow = calcBaseDateTime();
        WeatherDTO.BaseDateTime baseVilage = calcBaseDateTimeForVilage();

        // 3. 실황 + 단기예보 호출
        WeatherDTO.WeatherData current = loadCurrentWeather(grid, baseNow);
        List<WeatherDTO.HourlyForecast> hourlyAll = loadHourlyForecast(grid, baseVilage);

        // 4. 시간별 예보는 "현재 시각 이후" 기준으로 최대 4개만 사용
        List<WeatherDTO.HourlyForecast> hourly = selectUpcomingForecasts(hourlyAll, 4);

        // 5. 행정구역 이름 매핑 (기존 GridAddressLoader 사용)
        String key = grid.getNx() + "-" + grid.getNy();
        String defaultName = String.format("위도 %.3f, 경도 %.3f", lat, lon);
        String locationName = GridAddressLoader.getMap()
                .getOrDefault(key, defaultName);

        return new WeatherDTO.Response(locationName, current, hourly);
    }

    // -------------------------
    //   1) 좌표 변환
    // -------------------------
    public WeatherDTO.GridPoint convertToGrid(double lat, double lon) {

        double RE = 6371.00877; // 지구 반경(km)
        double GRID = 5.0;      // 격자 간격(km)
        double SLAT1 = 30.0;
        double SLAT2 = 60.0;
        double OLON = 126.0;
        double OLAT = 38.0;
        double XO = 43.0;
        double YO = 136.0;

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

        return new WeatherDTO.GridPoint(nx, ny);
    }

    // -------------------------
    //   2) 기준 시각 계산 (실황)
    // -------------------------
    public WeatherDTO.BaseDateTime calcBaseDateTime() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        LocalDateTime baseTime = now.minusHours(1)
                .withMinute(0)
                .withSecond(0)
                .withNano(0);

        return new WeatherDTO.BaseDateTime(
                baseTime.format(DATE_FMT),
                baseTime.format(TIME_FMT)
        );
    }

    // -------------------------
    //   3) 기준 시각 계산 (단기예보)
    // -------------------------
    public WeatherDTO.BaseDateTime calcBaseDateTimeForVilage() {
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
        int hour = now.getHour();
        int[] baseHours = {2, 5, 8, 11, 14, 17, 20, 23};

        LocalDateTime base;

        if (hour < 2) {
            base = now.minusDays(1).withHour(23);
        } else {
            int chosen = 2;
            for (int h : baseHours) {
                if (hour >= h) chosen = h;
            }
            base = now.withHour(chosen);
        }

        base = base.withMinute(0).withSecond(0).withNano(0);

        return new WeatherDTO.BaseDateTime(
                base.format(DATE_FMT),
                base.format(TIME_FMT)
        );
    }

    // -------------------------
    //   4) 실황 API 호출 + 파싱
    // -------------------------
    private WeatherDTO.WeatherData loadCurrentWeather(WeatherDTO.GridPoint grid,
                                                      WeatherDTO.BaseDateTime base) throws Exception {

        String xml = provider.requestUltraSrtNcst(
                base.getBaseDate(),
                base.getBaseTime(),
                grid.getNx(),
                grid.getNy()
        );
        return parseUltraSrtNcstXml(xml);
    }

    private WeatherDTO.WeatherData parseUltraSrtNcstXml(String xml) throws Exception {

        WeatherDTO.WeatherData data = new WeatherDTO.WeatherData();

        Document doc = provider.loadXml(xml);
        NodeList items = doc.getElementsByTagName("item");

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);

            String category = getText(item, "category");
            String valueStr = getText(item, "obsrValue");
            if (valueStr == null || valueStr.isEmpty()) continue;

            double value;
            try {
                value = Double.parseDouble(valueStr);
            } catch (Exception e) {
                continue;
            }

            switch (category) {
                case "T1H":
                    data.setTemperature(value);
                    break;
                case "REH":
                    data.setHumidity(value);
                    break;
                case "WSD":
                    data.setWindSpeed(value);
                    break;
                case "RN1":
                    data.setRain1h(value);
                    break;
                case "PTY":
                    data.setPty((int) value);
                    break;
            }
        }

        data.buildCondition();
        return data;
    }

    // -------------------------
    //   5) 단기예보 API 호출 + 파싱
    // -------------------------
    private List<WeatherDTO.HourlyForecast> loadHourlyForecast(WeatherDTO.GridPoint grid,
                                                               WeatherDTO.BaseDateTime base) throws Exception {

        String xml = provider.requestVilageForecast(
                base.getBaseDate(),
                base.getBaseTime(),
                grid.getNx(),
                grid.getNy()
        );

        String today = LocalDateTime.now(ZoneId.of("Asia/Seoul")).format(DATE_FMT);
        return parseVilageFcstXml(xml, today);
    }

    private List<WeatherDTO.HourlyForecast> parseVilageFcstXml(String xml, String targetDate) throws Exception {

        Map<String, WeatherDTO.HourlyForecast> map = new LinkedHashMap<>();

        Document doc = provider.loadXml(xml);
        NodeList items = doc.getElementsByTagName("item");

        for (int i = 0; i < items.getLength(); i++) {
            Element item = (Element) items.item(i);

            String fcstDate = getText(item, "fcstDate");
            String fcstTime = getText(item, "fcstTime");
            String category = getText(item, "category");
            String valueStr = getText(item, "fcstValue");

            if (fcstDate == null || fcstTime == null || category == null || valueStr == null) continue;
            if (!targetDate.equals(fcstDate)) continue;

            map.putIfAbsent(fcstTime,
                    new WeatherDTO.HourlyForecast(fcstTime.substring(0, 2) + ":" + fcstTime.substring(2)));

            WeatherDTO.HourlyForecast hf = map.get(fcstTime);

            try {
                switch (category) {
                    case "TMP":
                    case "T1H":
                        hf.setTemperature(Double.parseDouble(valueStr));
                        break;
                    case "PTY":
                        hf.setPty((int) Double.parseDouble(valueStr));
                        break;
                    case "POP":
                        hf.setPop((int) Double.parseDouble(valueStr));
                        break;
                }
            } catch (NumberFormatException ignored) {}
        }

        List<WeatherDTO.HourlyForecast> list = new ArrayList<>(map.values());

        // 시간 오름차순 정렬 ("HH:mm" 문자열 기준)
        list.sort(Comparator.comparing(WeatherDTO.HourlyForecast::getTime));

        return list;
    }

    // -------------------------
    //   6) 시간 필터링: 현재 시각 이후 4개
    // -------------------------
    private List<WeatherDTO.HourlyForecast> selectUpcomingForecasts(List<WeatherDTO.HourlyForecast> all, int limit) {
        if (all == null || all.isEmpty() || limit <= 0) {
            return Collections.emptyList();
        }

        int nowHour = LocalDateTime.now(ZoneId.of("Asia/Seoul")).getHour();
        List<WeatherDTO.HourlyForecast> result = new ArrayList<>(limit);

        // 1차: 현재 시각보다 크거나 같은 시간
        for (WeatherDTO.HourlyForecast hf : all) {
            int h = parseHour(hf.getTime());
            if (h >= nowHour) {
                result.add(hf);
                if (result.size() == limit) return result;
            }
        }

        // 2차: 부족하면 하루 처음부터 채우기
        for (WeatherDTO.HourlyForecast hf : all) {
            int h = parseHour(hf.getTime());
            if (h < nowHour) {
                result.add(hf);
                if (result.size() == limit) break;
            }
        }

        return result;
    }

    private int parseHour(String time) {
        if (time == null || time.length() < 2) return 0;
        try {
            return Integer.parseInt(time.substring(0, 2));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // -------------------------
    //   공통 XML 유틸
    // -------------------------
    private String getText(Element parent, String tag) {
        NodeList nl = parent.getElementsByTagName(tag);
        if (nl.getLength() == 0) return null;
        if (nl.item(0).getFirstChild() == null) return null;
        return nl.item(0).getFirstChild().getNodeValue();
    }
}