package com.childofweather.controller.route;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.childofweather.util.ApiConfig;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 네이버 검색 > 지역(Local) API 프록시
 * 예) GET /ChildOfWeather/naver/placeSearch?query=서울역
 */
@WebServlet("/naver/placeSearch")
public class NaverPlaceSearchServlet extends HttpServlet {

    // 네이버 Local 검색 API 엔드포인트
    private static final String API_URL =
            "https://openapi.naver.com/v1/search/local.json";

    private static final String CLIENT_ID = ApiConfig.get("naver.search.client.id");
    private static final String CLIENT_SECRET = ApiConfig.get("naver.search.client.secret");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String query = req.getParameter("query");

        if (query == null || query.isBlank()) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.setContentType("application/json; charset=UTF-8");
            resp.getWriter().write("{\"error\":\"query 파라미터가 필요합니다.\"}");
            return;
        }

        String params = "query=" + URLEncoder.encode(query, "UTF-8")
                      + "&display=3"; // 최대 3개만

        URL url = new URL(API_URL + "?" + params);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // ✅ 검색 API 인증 헤더
        conn.setRequestProperty("X-Naver-Client-Id", CLIENT_ID);
        conn.setRequestProperty("X-Naver-Client-Secret", CLIENT_SECRET);

        int code = conn.getResponseCode();
        InputStream is = (code == HttpURLConnection.HTTP_OK)
                ? conn.getInputStream()
                : conn.getErrorStream();

        resp.setStatus(code);
        resp.setContentType("application/json; charset=UTF-8");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             PrintWriter out = resp.getWriter()) {
            String line;
            while ((line = br.readLine()) != null) {
                out.println(line);
            }
        }
    }
}
