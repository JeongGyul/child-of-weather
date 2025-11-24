package com.childofweather.controller;

import com.childofweather.dao.MemberDAO;
import com.childofweather.dto.MemberDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/join.do")
public class JoinServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String id    = request.getParameter("id");
        String pw    = request.getParameter("pw");
        String name  = request.getParameter("name");
        String email = request.getParameter("email");

        MemberDTO dto = new MemberDTO();
        dto.setMemberid(id);
        dto.setPassword(pw);
        dto.setName(name);
        dto.setEmail(email);

        MemberDAO dao = new MemberDAO();
        int result = dao.insert(dto);

        if (result == 1) {
            request.getRequestDispatcher("/WEB-INF/views/auth/joinOk.jsp")
                   .forward(request, response);
        } else {
            request.getRequestDispatcher("/WEB-INF/views/auth/joinFail.jsp")
                   .forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        doPost(req, resp);
    }
}
