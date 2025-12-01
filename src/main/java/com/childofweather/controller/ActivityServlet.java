package com.childofweather.controller;

import com.childofweather.dao.MemberDAO;
import com.childofweather.dto.MemberDTO;

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

    private final MemberDAO memberDAO = new MemberDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>> [ActivityServlet] doGet() 진입");

        HttpSession session = request.getSession(false);
        MemberDTO loginUser = (session != null)
                ? (MemberDTO) session.getAttribute("loginUser")
                : null;

        if (loginUser == null) {
            System.out.println(">>> [ActivityServlet] loginUser == null (GET) -> /login.do 리다이렉트");
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        Long memberId = loginUser.getMemberId();
        System.out.println(">>> [ActivityServlet] doGet() memberId = " + memberId);

        // 이 회원 활동 목록
        List<MemberDTO.MemberActivity> activityList =
                memberDAO.getActivitiesByMemberId(memberId);
        request.setAttribute("activityList", activityList);

        // 모달 기본값
        DefaultConditions defaultConditions = new DefaultConditions(15, 30, 60, 20);
        request.setAttribute("defaultConditions", defaultConditions);

        request.getRequestDispatcher("/WEB-INF/views/activity/activity.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>> [ActivityServlet] doPost() 진입");

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        MemberDTO loginUser = (session != null)
                ? (MemberDTO) session.getAttribute("loginUser")
                : null;

        if (loginUser == null) {
            System.out.println(">>> [ActivityServlet] loginUser == null (POST) -> /login.do 리다이렉트");
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        Long memberId = loginUser.getMemberId();
        System.out.println(">>> [ActivityServlet] doPost() memberId = " + memberId);

        // 어떤 동작인지 구분 (create / delete)
        String op = request.getParameter("op");
        if ("delete".equals(op)) {
            // ===== 삭제 처리 =====
            String idStr = request.getParameter("memberActivityId");
            System.out.println(">>> [ActivityServlet] delete 요청, memberActivityId = " + idStr);

            if (idStr != null && !idStr.isBlank()) {
                long memberActivityId = Long.parseLong(idStr);
                int rows = memberDAO.deleteMemberActivity(memberActivityId, memberId);
                System.out.println(">>> [ActivityServlet] delete rows = " + rows);
            } else {
                System.out.println(">>> [ActivityServlet] delete 요청이지만 memberActivityId 파라미터 없음");
            }

            response.sendRedirect(request.getContextPath() + "/activity.do");
            return;
        }

        // ===== 여기부터는 "새 활동 추가" 처리 =====
        String activityTypeIdStr = request.getParameter("activityTypeId");
        String minTempStr       = request.getParameter("minTemp");
        String maxTempStr       = request.getParameter("maxTemp");
        String maxHumidityStr   = request.getParameter("maxHumidity");
        String maxPopStr        = request.getParameter("maxPop");

        // activityTypeId는 NOT NULL이므로, 없으면 바로 리다이렉트
        if (activityTypeIdStr == null || activityTypeIdStr.isBlank()) {
            System.out.println(">>> [ActivityServlet] activityTypeId가 비어 있음. insert 중단");
            response.sendRedirect(request.getContextPath() + "/activity.do");
            return;
        }

        System.out.println(">>> [ActivityServlet] raw params:");
        System.out.println("    activityTypeId = " + activityTypeIdStr);
        System.out.println("    minTemp        = " + minTempStr);
        System.out.println("    maxTemp        = " + maxTempStr);
        System.out.println("    maxHumidity    = " + maxHumidityStr);
        System.out.println("    maxPop         = " + maxPopStr);

        MemberDTO.MemberActivity activity = new MemberDTO.MemberActivity();
        activity.setMemberId(memberId);
        activity.setActivityTypeId(Long.parseLong(activityTypeIdStr));
        activity.setMinTemperature(parseNullableInt(minTempStr));
        activity.setMaxTemperature(parseNullableInt(maxTempStr));
        activity.setMaxHumidity(parseNullableInt(maxHumidityStr));
        activity.setMaxPrecipitation(parseNullableInt(maxPopStr));

        int rows = memberDAO.insertMemberActivity(activity);
        System.out.println(">>> [ActivityServlet] insert rows = " + rows);

        response.sendRedirect(request.getContextPath() + "/activity.do");
    }

    private Integer parseNullableInt(String value) {
        if (value == null || value.isBlank()) return null;
        return Integer.parseInt(value);
    }

    // JSP에서 사용 중인 기본 조건용 클래스
    public static class DefaultConditions {
        private final int minTemp;
        private final int maxTemp;
        private final int maxHumidity;
        private final int maxPop;

        public DefaultConditions(int minTemp, int maxTemp, int maxHumidity, int maxPop) {
            this.minTemp = minTemp;
            this.maxTemp = maxTemp;
            this.maxHumidity = maxHumidity;
            this.maxPop = maxPop;
        }

        public int getMinTemp()     { return minTemp; }
        public int getMaxTemp()     { return maxTemp; }
        public int getMaxHumidity() { return maxHumidity; }
        public int getMaxPop()      { return maxPop; }
    }
}
