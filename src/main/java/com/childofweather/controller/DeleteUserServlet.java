package com.childofweather.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.childofweather.dao.MemberDAO;


@WebServlet("/deleteUser.do")
public class DeleteUserServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String memberId = request.getParameter("id");
		
		MemberDAO dao = new MemberDAO();
        int result = dao.deleteMember(memberId);
        
        if (result == 1) {
            response.sendRedirect("admin.do");
        }
	}

}
