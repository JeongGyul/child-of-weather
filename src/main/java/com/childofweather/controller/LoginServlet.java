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

@WebServlet("/login.do")
public class LoginServlet extends HttpServlet {
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/member/login_register.jsp")
               .forward(request, response);
    }
	
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        MemberDTO dto = new MemberDTO();
        dto.setEmail(email);
        dto.setPassword(password);

        MemberDAO dao = new MemberDAO();
        boolean ok = dao.login(dto);

        if (ok) {
        	MemberDTO loginUser = dao.getMember(dto);
            HttpSession session = request.getSession();
            session.setAttribute("loginUser", loginUser);

            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } else {
        	request.setAttribute("error", "이메일 또는 패스워드가 일치하지 않습니다.");
            request.getRequestDispatcher("/WEB-INF/views/member/login_register.jsp")
                   .forward(request, response);
        }
    }
}
