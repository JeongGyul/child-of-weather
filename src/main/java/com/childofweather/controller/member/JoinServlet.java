package com.childofweather.controller.member;

import com.childofweather.dto.MemberDTO;
import com.childofweather.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/join.do")
public class JoinServlet extends HttpServlet {
	
	private MemberService memberService = new MemberService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        MemberDTO.JoinRequest joinDTO = new MemberDTO.JoinRequest();
        joinDTO.setName(name);
        joinDTO.setEmail(email);
        joinDTO.setPassword(password); 

        Boolean isSuccess = memberService.join(joinDTO);

        if (isSuccess) {
        	request.setAttribute("successMessage", "회원가입이 완료되었습니다!");
        } else {
        	request.setAttribute("error", "회원가입에 실패했습니다.");
        }
        
        request.getRequestDispatcher("/WEB-INF/views/member/login_register.jsp")
        .forward(request, response);
    }
}
