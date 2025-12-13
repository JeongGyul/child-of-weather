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

@WebServlet("/mypage/edit.do")
public class MyPageEditServlet extends HttpServlet {
	
	private MemberService memberService = new MemberService();

    // 수정 폼 띄우기 (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/WEB-INF/views/member/mypage_edit.jsp")
               .forward(request, response);
    }

    // 수정 처리 (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        MemberDTO.InfoResponse loginUser = (MemberDTO.InfoResponse) session.getAttribute("loginUser");
        
        String originalEmail = loginUser.getEmail();
        String newName = request.getParameter("name");
        String newEmail = request.getParameter("email");

        Boolean isSuccess = memberService.updateProfile(originalEmail, newName, newEmail);

        if (isSuccess) {
            loginUser.setName(newName);
            loginUser.setEmail(newEmail);
            session.setAttribute("loginUser", loginUser);
            
            response.sendRedirect(request.getContextPath() + "/mypage.do");
        } else {
            // 실패 시: 에러 메시지 + 다시 수정 폼
            request.setAttribute("errorMsg", "회원 정보 수정에 실패했습니다.");
            request.setAttribute("newName", newName);
            request.setAttribute("newEmail", newEmail);

            request.getRequestDispatcher("/WEB-INF/views/member/mypage_edit.jsp")
                   .forward(request, response);
        }
    }
}
