package com.childofweather.controller.admin;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.childofweather.service.AdminService;

@WebServlet("/admin/deleteUser.do")
public class DeleteUserServlet extends HttpServlet {
	
	AdminService adminService = new AdminService();
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String memberId = request.getParameter("id");
		Boolean result = adminService.deleteMember(memberId);
		
		response.sendRedirect(request.getContextPath() + "/admin.do");
	}

}
