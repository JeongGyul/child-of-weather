package com.childofweather.controller.admin;

import com.childofweather.dao.MemberDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/admin/deleteUser.do")
public class DeleteUserServlet extends HttpServlet {

    private MemberDAO memberDAO = new MemberDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 파라미터 받기
        String memberId = request.getParameter("id");

        // 2. 회원 삭제 처리
        memberDAO.deleteMember(memberId);

        // 3. 관리자 페이지로 리다이렉트
        response.sendRedirect(request.getContextPath() + "/admin.do");
    }
}
