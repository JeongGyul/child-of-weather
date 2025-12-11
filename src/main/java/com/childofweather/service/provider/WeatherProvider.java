package com.childofweather.service.provider;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class WeatherProvider {

    // 기상청 초단기실황 조회 URL
    private static final String API_URL_ULTRA =
            "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getUltraSrtNcst";

    // 기상청 단기예보 조회 URL
    private static final String API_URL_VILAGE =
            "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

    // 공공데이터포털 서비스 키
    private static final String SERVICE_KEY = loadServiceKey();

    private static String loadServiceKey() {
        Properties props = new Properties();

        try (InputStream input = WeatherProvider.class
                .getClassLoader()
                .getResourceAsStream("serviceKey.properties")) {

            if (input == null) {
                System.out.println("Sorry, unable to find serviceKey.properties");
                throw new IllegalStateException("[WeatherProvider] serviceKey.properties 를 찾을 수 없습니다.");
            }

            props.load(input);
            String key = props.getProperty("kma.serviceKey");

            if (key == null || key.isBlank()) {
                throw new IllegalStateException("[WeatherProvider] kma.serviceKey 가 설정되어 있지 않습니다.");
            }

            return key.trim();
        } catch (IOException e) {
            throw new RuntimeException("[WeatherProvider] serviceKey.properties 로딩 중 오류 발생", e);
        }
    }

    public String requestUltraSrtNcst(String baseDate, String baseTime, int nx, int ny) throws Exception {
        return requestApi(API_URL_ULTRA, baseDate, baseTime, nx, ny);
    }

    public String requestVilageForecast(String baseDate, String baseTime, int nx, int ny) throws Exception {
        return requestApi(API_URL_VILAGE, baseDate, baseTime, nx, ny);
    }

    private String requestApi(String baseUrl, String baseDate, String baseTime, int nx, int ny) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(baseUrl);
        urlBuilder.append("?")
                .append("ServiceKey=").append(URLEncoder.encode(SERVICE_KEY, "UTF-8"))
                .append("&pageNo=1")
                .append("&numOfRows=1000")
                .append("&dataType=XML")
                .append("&base_date=").append(baseDate)
                .append("&base_time=").append(baseTime)
                .append("&nx=").append(nx)
                .append("&ny=").append(ny);

        String reqUrl = urlBuilder.toString();
        System.out.println("[WeatherProvider] Request URL = " + reqUrl);

        URL url = new URL(reqUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/xml");

        int code = conn.getResponseCode();
        System.out.println("[WeatherProvider] Response Code = " + code);

        InputStream is = (code == 200) ? conn.getInputStream() : conn.getErrorStream();

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            conn.disconnect();
        }

        String xml = sb.toString();
        // 디버깅용 로그 (XML 내용만 출력, 앞뒤에 다른 문자열 붙이지 않기)
        System.out.println("[WeatherProvider] Raw XML length = " + xml.length());

        return xml;
    }

    /** XML 문자열을 DOM Document로 변환 */
    public Document loadXml(String xml) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        return db.parse(new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    }
}