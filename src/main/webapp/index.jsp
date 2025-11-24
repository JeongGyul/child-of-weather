<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:choose>
    <c:when test="${not empty sessionScope.loginUser}">
        <c:redirect url="dashboard.do"/>
    </c:when>
    
    <c:otherwise>
        <c:redirect url="login.do"/>
    </c:otherwise>
</c:choose>