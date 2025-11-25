<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">

<header class="app-header">
        <div class="app-header-left">
            <div class="app-logo">☁</div>
            <div class="app-title-group">
                <h1>날씨의 아이</h1>
                <p>똑똑한 개인 맞춤형 날씨 알림</p>
            </div>
        </div>
        <div class="app-header-right">
            <div class="user-badge-circle">
   		 		${fn:substring(sessionScope.loginUser.name, 0, 1)}
			</div>
            <div class="user-info">
    			<span class="name">${sessionScope.loginUser.name}</span>
    			<span class="region">경북</span>
			</div>
        </div>
    </header>

<nav class="main-tabs">
    <div class="main-tabs-inner">
        <a href="${pageContext.request.contextPath}/dashboard.do" 
           class="tab-item ${param.activeMenu == 'dashboard' ? 'active' : ''}">
           대시보드
        </a>
        
        <a href="${pageContext.request.contextPath}/activity/list.do" 
           class="tab-item ${param.activeMenu == 'activity' ? 'active' : ''}">
           활동
        </a>
        
        <a href="${pageContext.request.contextPath}/route/list.do" 
           class="tab-item ${param.activeMenu == 'route' ? 'active' : ''}">
           경로
        </a>
        
        <div class="tab-item ${param.activeMenu == 'notification' ? 'active' : ''}">알림</div>
        
        <a href="${pageContext.request.contextPath}/mypage.do" 
        	class="tab-item ${param.activeMenu == 'my' ? 'active' : ''}">마이
        </a>
        
        <c:if test="${not empty sessionScope.loginUser 
             and sessionScope.loginUser.role eq 'ADMIN'}">
   			 <a href="${pageContext.request.contextPath}/admin.do" 
      			 class="tab-item ${param.activeMenu == 'admin' ? 'active' : ''}">
       			관리자
   			 </a>
		</c:if>

    </div>
</nav>