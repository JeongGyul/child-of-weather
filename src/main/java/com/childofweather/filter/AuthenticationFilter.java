package com.childofweather.filter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
		boolean isLoggedIn = (session != null && session.getAttribute("loginUser") != null);
		
		if(isLoggedIn) {
			chain.doFilter(request, response);
		} else {
			System.out.println(">>> [AuthFilter] 미인증 사용자 접근 차단: " + path);
            response.sendRedirect(contextPath + "/login.do");
		}
	}
	
	private boolean isWhiteList(String path) {
		return WHITE_LIST.stream().anyMatch(white -> 
        path.equals(white) || path.startsWith(white + "/")
				);
	}
}
