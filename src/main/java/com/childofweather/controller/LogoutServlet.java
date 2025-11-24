package com.childofweather.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/logout.do")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 기존 세션이 있으면 가져오고, 없으면 null
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();   // 세션 제거 = 로그아웃
        }

        // 🔁 로그아웃 후 로그인 화면으로 이동
        
        response.sendRedirect(request.getContextPath() + "/login.do");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
