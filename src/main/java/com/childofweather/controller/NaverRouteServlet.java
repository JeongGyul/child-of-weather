package com.childofweather.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.childofweather.util.ApiConfig;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@WebServlet("/naver/route")
public class NaverRouteServlet extends HttpServlet {

    // ✅ 네이버 길찾기(Directions 5) REST API 엔드포인트
    private static final String API_URL =
            "https://maps.apigw.ntruss.com/map-direction/v1/driving";

    private static final String NAVER_DIRECTIONS_CLIENT_ID = ApiConfig.get("naver.map.client.id");
    private static final String NAVER_DIRECTIONS_CLIENT_SECRET = ApiConfig.get("naver.map.client.secret");

    /**
     * 🧪 [TEST] 서버 환경의 HTTPS 연결 상태를 검증하는 메서드 (401 오류 진단용)
     * 이 코드가 200 OK를 반환해야 서버 환경은 정상입니다.
     */
    private void checkHttpsConnection() {
        // 테스트용 공용 HTTPS 엔드포인트 (구글 서버)
        String TEST_URL = "https://www.google.com"; 
        
        try {
            URL url = new URL(TEST_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000); 
            conn.setReadTimeout(5000); 

            int responseCode = conn.getResponseCode();
            
            System.out.println("==================================================");
            System.out.println("[HTTPS TEST] 응답 코드: " + responseCode);
            System.out.println("[HTTPS TEST] 응답 메시지: " + conn.getResponseMessage());
            System.out.println("==================================================");

        } catch (Exception e) {
            // 이 예외가 발생하면 네트워크/JVM 환경에 문제가 있다는 뜻입니다.
            System.err.println("[HTTPS TEST] 연결 실패 오류: " + e.getMessage());
        }
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        
        // 🧪 [TEST 호출] 서버 환경 검증 테스트 실행
        checkHttpsConnection(); 

        // 프론트(js/route.js)에서 쿼리스트링으로 넘어오는 값: "경도,위도"
        String start = req.getParameter("start"); // 예: "126.9780,37.5665"
        String goal  = req.getParameter("goal");  // 예: "127.0276,37.4979"

        if (start == null || goal == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"error\":\"start, goal 파라미터가 필요합니다.\"}");
            return;
        }

        try {
            // 쿼리 파라미터 인코딩
            String params = "start=" + URLEncoder.encode(start, "UTF-8") +
                            "&goal=" + URLEncoder.encode(goal, "UTF-8");

            URL url = new URL(API_URL + "?" + params);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            // ✅ Directions REST 인증 헤더 설정
            conn.setRequestProperty("X-NCP-APIGW-API-KEY-ID", NAVER_DIRECTIONS_CLIENT_ID);
            conn.setRequestProperty("X-NCP-APIGW-API-KEY", NAVER_DIRECTIONS_CLIENT_SECRET);
            
            // 📝 [로깅] 요청 헤더 값 확인 (401 오류 진단용)
            System.out.println("[RouteLog] >> Naver API Request URL: " + url.toString());
            System.out.println("[RouteLog] >> X-NCP-APIGW-API-KEY-ID: " + NAVER_DIRECTIONS_CLIENT_ID);
            System.out.println("[RouteLog] >> X-NCP-APIGW-API-KEY: " + NAVER_DIRECTIONS_CLIENT_SECRET); // Secret 값 로깅

            int code = conn.getResponseCode();
            InputStream is = (code == HttpURLConnection.HTTP_OK)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

            // 📝 [로깅] 응답 코드 확인
            System.out.println("[NaverRouteServlet] HTTP 응답 코드 = " + code);

            resp.setContentType("application/json; charset=UTF-8");
            try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                 PrintWriter out = resp.getWriter()) {
                
                // 📝 [로깅] 응답 본문 확인
                StringBuilder responseJson = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    out.println(line); // 클라이언트에게 전송
                    responseJson.append(line); // 로깅용으로 저장
                }
                System.out.println("[RouteLog] << 응답 JSON 본문: " + responseJson.toString()); 
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"error\":\"네이버 길찾기 호출 중 서버 오류가 발생했습니다.\"}");
        }
    }
}