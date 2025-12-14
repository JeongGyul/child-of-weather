package com.childofweather.controller.member;

import com.childofweather.dto.MemberDTO;
import com.childofweather.service.MemberService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/mypage.do")
public class MyPageServlet extends HttpServlet {
	
	private MemberService memberService = new MemberService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	HttpSession session = request.getSession(false);
        MemberDTO.InfoResponse loginUser = (MemberDTO.InfoResponse) session.getAttribute("loginUser");

        // 2) 마이페이지에 뿌릴 데이터 준비
        MemberDTO.MyPageInfoResponse myPageInfo = memberService.getMyPageInfo(loginUser);
        request.setAttribute("myPageInfo", myPageInfo);

        // 3) JSP로 forward
        request.getRequestDispatcher("/WEB-INF/views/member/mypage.jsp")
               .forward(request, response);
    }
}
