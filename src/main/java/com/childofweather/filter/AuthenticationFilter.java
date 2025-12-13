package com.childofweather.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.childofweather.dto.MemberDTO;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/*")
public class AuthenticationFilter extends HttpFilter {

	private static final List<String> WHITE_LIST = Arrays.asList(
			"/login.do", "/join.do", "/logout.do", "/index.jsp", "/", "/css", "/js"
	);
	
	@Override
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
			throws IOException, ServletException 
	{
		String requestURI = request.getRequestURI();
		String contextPath = request.getContextPath();
		String path = requestURI.substring(contextPath.length());
		
		if(isWhiteList(path)) {
			chain.doFilter(request, response);
			return;
		}
		
		HttpSession session = request.getSession(false);
		MemberDTO.InfoResponse loginUser = (session == null) ? 
                null : (MemberDTO.InfoResponse) session.getAttribute("loginUser");
		
		if(loginUser == null) {
			System.out.println(">>> [AuthFilter] 미인증 사용자 접근 차단: " + path);
            response.sendRedirect(contextPath + "/login.do");
            return;
		}
		
		if (path.startsWith("/admin") && !"ADMIN".equals(loginUser.getRole())) {
			System.out.println(">>> [AuthFilter] 일반 사용자 관리자 페이지 접근 차단: " + loginUser.getName());
			response.sendRedirect(contextPath + "/dashboard.do");
			return;
	    }
		
		chain.doFilter(request, response);
	}
	
	private boolean isWhiteList(String path) {
		return WHITE_LIST.stream().anyMatch(white -> 
        path.equals(white) || path.startsWith(white + "/")
				);
	}
}
