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

@WebServlet("/activity/delete.do")
public class ActivityDeleteServlet extends HttpServlet {

    private final MemberDAO memberDAO = new MemberDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>> [ActivityDeleteServlet] doPost() 진입");

        HttpSession session = request.getSession(false);
        MemberDTO loginUser = (session != null)
                ? (MemberDTO) session.getAttribute("loginUser")
                : null;

        if (loginUser == null) {
            System.out.println(">>> [ActivityDeleteServlet] loginUser == null -> /login.do 리다이렉트");
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        String idStr = request.getParameter("memberActivityId");
        if (idStr != null && !idStr.isBlank()) {
            long memberActivityId = Long.parseLong(idStr);
            long memberId = loginUser.getMemberId();

            int rows = memberDAO.deleteMemberActivity(memberActivityId, memberId);
            System.out.println(">>> [ActivityDeleteServlet] delete rows = " + rows);
        } else {
            System.out.println(">>> [ActivityDeleteServlet] memberActivityId 파라미터 없음");
        }

        // 다시 목록으로
        response.sendRedirect(request.getContextPath() + "/activity.do");
    }
}
