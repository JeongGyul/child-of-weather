package com.childofweather.controller.admin;

import java.io.IOException;
import java.util.List;

import com.childofweather.dao.MemberDAO;
import com.childofweather.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/admin.do")
public class AdminServlet extends HttpServlet {
	
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	System.out.println(">>> ActivityServlet.doPost() 진입");

    	request.setCharacterEncoding("UTF-8");
    	
        MemberDAO dao = new MemberDAO();
        List<MemberDTO.InfoResponse> userList = dao.getAllMembers();
        int totalUsers = userList.size();
        int adminCount = dao.getAdminCount();
        int userCount = dao.getUserCount();
        int newJoinCount = dao.getNewUserCount();
        int activeUserCount = dao.getActiveUserCount();

        // 4. 데이터 세팅
        request.setAttribute("userList", userList);
        request.setAttribute("totalUsers", totalUsers);
        request.setAttribute("adminCount", adminCount);
        request.setAttribute("userCount", userCount);
        request.setAttribute("newJoinCount", newJoinCount);
        request.setAttribute("activeUserCount", activeUserCount);

        request.getRequestDispatcher("/WEB-INF/views/admin/admin.jsp").forward(request, response);
    }
}