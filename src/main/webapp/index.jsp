<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    // 세션에서 로그인 여부 확인
    String loginId = (String) session.getAttribute("loginId");

    if (loginId == null) {
        // 로그인 안 되어 있으면 로그인 폼으로 forward
        request.getRequestDispatcher("/WEB-INF/views/auth/loginForm.jsp")
               .forward(request, response);
    } else {
        // 로그인 되어 있으면 메인 페이지로 이동
        response.sendRedirect("main.jsp");
    }
%>
