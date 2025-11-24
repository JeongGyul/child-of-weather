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

@WebServlet("/mypage/edit.do")
public class MyPageEditServlet extends HttpServlet {

    // 수정 폼 띄우기 (GET)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        MemberDTO loginUser = (session == null)
                ? null
                : (MemberDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            // 로그인 안 되어 있으면 로그인 화면으로
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        // 폼 초기값으로 세팅
        request.setAttribute("userName", loginUser.getName());
        request.setAttribute("userEmail", loginUser.getEmail());

        request.getRequestDispatcher("/WEB-INF/views/member/mypage_edit.jsp")
               .forward(request, response);
    }

    // 수정 처리 (POST)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        MemberDTO loginUser = (session == null)
                ? null
                : (MemberDTO) session.getAttribute("loginUser");

        if (loginUser == null) {
            response.sendRedirect(request.getContextPath() + "/login.do");
            return;
        }

        // 1) 기존 이메일 (WHERE 조건용)
        String originalEmail = loginUser.getEmail();

        // 2) 폼에서 넘어온 새 값
        String newName = request.getParameter("name");
        String newEmail = request.getParameter("email");

        // 3) DTO에 반영
        loginUser.setName(newName);
        loginUser.setEmail(newEmail);

        // 4) DB 업데이트
        MemberDAO dao = new MemberDAO();
        int result = dao.updateProfile(originalEmail, loginUser);

        if (result > 0) {
            // 세션도 최신 정보로 유지
            session.setAttribute("loginUser", loginUser);
            // 수정 후 마이페이지로 이동
            response.sendRedirect(request.getContextPath() + "/mypage.do");
        } else {
            // 실패 시: 에러 메시지 + 다시 수정 폼
            request.setAttribute("errorMsg", "회원 정보 수정에 실패했습니다.");
            request.setAttribute("userName", newName);
            request.setAttribute("userEmail", newEmail);

            request.getRequestDispatcher("/WEB-INF/views/member/mypage_edit.jsp")
                   .forward(request, response);
        }
    }
}
