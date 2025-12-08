package com.childofweather.controller.admin;

import java.io.IOException;
import java.util.List;

import com.childofweather.dao.MemberDAO;
import com.childofweather.dto.AdminDTO;
import com.childofweather.dto.MemberDTO;
import com.childofweather.service.AdminService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin.do")
public class AdminServlet extends HttpServlet {
	
	private AdminService adminService = new AdminService();
	
	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
    		throws ServletException, IOException {

    	request.setCharacterEncoding("UTF-8");
    	
        AdminDTO.AdminPageResponse adminPageInfo = adminService.getAdminPageInfo();

        request.setAttribute("adminPageInfo", adminPageInfo);
        request.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp").forward(request, response);
    }
}