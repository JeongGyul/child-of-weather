package com.childofweather.controller.activity;

import com.childofweather.dto.ActivityDTO;
import com.childofweather.dto.MemberDTO;
import com.childofweather.dto.WeatherDTO;
import com.childofweather.service.ActivityService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/activity.do")
public class ActivityServlet extends HttpServlet {

    private ActivityService activityService = new ActivityService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        MemberDTO.InfoResponse loginUser = (session != null)
                ? (MemberDTO.InfoResponse) session.getAttribute("loginUser")
                : null;

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        List<WeatherDTO.HourlyForecast> hourly = (session != null)
                ? (List<WeatherDTO.HourlyForecast>) session.getAttribute("hourly")
                : null;

        if (hourly == null) {
            response.sendRedirect(request.getContextPath() + "/dashboard.do");
            return;
        }

        Long memberId = loginUser.getMemberId();
        List<ActivityDTO.Response> activityList = activityService.getActivities(memberId, hourly);

        request.setAttribute("activityList", activityList);
        request.getRequestDispatcher("/WEB-INF/views/activity/activity.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        MemberDTO.InfoResponse loginUser = (session != null)
                ? (MemberDTO.InfoResponse) session.getAttribute("loginUser")
                : null;

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        Long memberId = loginUser.getMemberId();
        String activityTypeIdStr = request.getParameter("activityTypeId");
        String minTempStr = request.getParameter("minTemp");
        String maxTempStr = request.getParameter("maxTemp");
        String maxHumidityStr = request.getParameter("maxHumidity");
        String maxPopStr = request.getParameter("maxPop");

        // activityTypeId는 NOT NULL이므로, 없으면 바로 리다이렉트
        if (activityTypeIdStr == null || activityTypeIdStr.isBlank()) {
            response.sendRedirect(request.getContextPath() + "/activity.do");
            return;
        }

        ActivityDTO.Request dto = new ActivityDTO.Request();
        dto.setMemberId(memberId);
        dto.setActivityTypeId(Long.parseLong(activityTypeIdStr));
        dto.setMinTemperature(parseNullableInt(minTempStr));
        dto.setMaxTemperature(parseNullableInt(maxTempStr));
        dto.setMaxHumidity(parseNullableInt(maxHumidityStr));
        dto.setMaxPrecipitation(parseNullableInt(maxPopStr));

        activityService.insertMemberActivity(dto);
        response.sendRedirect(request.getContextPath() + "/activity.do");
    }

    private Integer parseNullableInt(String value) {
        if (value == null || value.isBlank()) return null;
        return Integer.parseInt(value);
    }
}
