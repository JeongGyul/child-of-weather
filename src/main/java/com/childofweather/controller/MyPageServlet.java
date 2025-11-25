package com.childofweather.controller;

import com.childofweather.dto.MemberDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/mypage.do")
public class MyPageServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1) 로그인 체크
        HttpSession session = request.getSession(false);
        MemberDTO loginUser = (session == null)
                ? null
                : (MemberDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            // 로그인 안 되어 있으면 로그인 화면으로
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        // 2) 마이페이지에 뿌릴 데이터 준비 (지금은 더미)
        request.setAttribute("userName", loginUser.getName());
        request.setAttribute("userEmail", loginUser.getEmail());
        
        String roleStr = "USER".equals(loginUser.getRole()) ? "일반 회원" : "관리자";
        request.setAttribute("userRole", roleStr);

        request.setAttribute("currentRegion", "울산");
        request.setAttribute("joinDate", "2025년 11월 9일");
        request.setAttribute("lastLoginDate", "2025년 11월 25일");

        request.setAttribute("activityCount", 3);
        request.setAttribute("routeCount", 3);
        request.setAttribute("alertCount", 12);

        // 3) JSP로 forward
        request.getRequestDispatcher("/WEB-INF/views/member/mypage.jsp")
               .forward(request, response);
    }
}
