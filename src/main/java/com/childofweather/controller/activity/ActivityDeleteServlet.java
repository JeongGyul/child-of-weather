package com.childofweather.controller.activity;

import com.childofweather.dto.MemberDTO;
import com.childofweather.service.ActivityService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/activity/delete.do")
public class ActivityDeleteServlet extends HttpServlet {

    private ActivityService activityService = new ActivityService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        MemberDTO.InfoResponse loginUser = (session != null)
                ? (MemberDTO.InfoResponse) session.getAttribute("loginUser")
                : null;

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }
        
        long memberActivityId = Long.parseLong(request.getParameter("memberActivityId"));
        long memberId = loginUser.getMemberId();
        
        activityService.deleteMemberActivity(memberActivityId, memberId);
       
        response.sendRedirect(request.getContextPath() + "/activity.do");
    }
}
