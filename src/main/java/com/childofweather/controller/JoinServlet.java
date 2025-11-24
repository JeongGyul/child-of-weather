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
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        MemberDTO dto = new MemberDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setPassword(password); 

        MemberDAO dao = new MemberDAO();
        int result = dao.insert(dto);

        if (result == 1) {
        	request.setAttribute("successMessage", "회원가입이 완료되었습니다!");
            request.getRequestDispatcher("/WEB-INF/views/member/login_register.jsp")
                   .forward(request, response);
        } else {
        	request.setAttribute("error", "회원가입에 실패했습니다.");
        	request.getRequestDispatcher("/WEB-INF/views/member/login_register.jsp")
            		.forward(request, response);
        }
    }
}
