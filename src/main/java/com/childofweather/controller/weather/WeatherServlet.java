package com.childofweather.controller.weather;

import com.childofweather.dto.ActivityDTO;
import com.childofweather.dto.WeatherDTO;
import com.childofweather.service.RecommendActivityService;
import com.childofweather.service.WeatherService;
import com.childofweather.util.WeatherJsonMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/weather/short")
public class WeatherServlet extends HttpServlet {

    private final WeatherService weatherService = new WeatherService();
    private final WeatherJsonMapper weatherJsonMapper = new WeatherJsonMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

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


        try {
            WeatherDTO.Response dto = weatherService.getWeather(lat, lon);
            List<ActivityDTO.RecommendActivityResponse> activities = RecommendActivityService.getRecommendActivities(dto);

            HttpSession session = request.getSession();
            session.setAttribute("hourly", new ArrayList<>(dto.getHourly()));

            response.setContentType("application/json; charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                out.write(weatherJsonMapper.toJson(dto, activities));
            }

        } catch (Exception e) {
            e.printStackTrace();
            writeError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "기상청 API 호출 중 오류가 발생했습니다.");
        }
    }

    private void writeError(HttpServletResponse response, int status, String message)
            throws IOException {
        response.setStatus(status);
        response.setContentType("application/json; charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            String safeMsg = message.replace("\"", "\\\"");
            out.write("{\"error\":\"" + safeMsg + "\"}");
        }
    }
}
