<%@ page contentType="text/html; charset=UTF-8"
         import="jakarta.servlet.RequestDispatcher" %>
<%
    String loginId = (String) session.getAttribute("loginId");

    if (loginId == null) {
        // 로그인 안 된 경우 → 로그인 폼으로 forward
        RequestDispatcher rd =
            request.getRequestDispatcher("/WEB-INF/views/auth/loginForm.jsp");
        rd.forward(request, response);
        return;
    } else {
        // 로그인 된 경우 → 메인 페이지로 이동
        response.sendRedirect("main.jsp");
        return;
    }
%>
