<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">

<header class="app-header">
        <div class="app-header-left">
            <div class="app-logo">
			    <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="#ffffff" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-cloud">
			        <path d="M17.5 19c0-1.7-1.3-3-3-3c-1.1 0-2.1.6-2.6 1.5c-.5-.9-1.5-1.5-2.6-1.5c-1.7 0-3 1.3-3 3"/>
			        <path d="M3 16.6A6 6 0 0 1 12 7a6 6 0 0 1 5.6 4.4a3.5 3.5 0 0 1 2.3 6.6"/>
			    </svg>
			</div>
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
        
        <a href="${pageContext.request.contextPath}/activity.do" 
           class="tab-item ${param.activeMenu == 'activity' ? 'active' : ''}">
           활동
        </a>
        
<!-- 기존 탭들 사이에 하나만 추가 -->
		<a href="${pageContext.request.contextPath}/route.do"
  		   class="tab-item ${param.activeMenu == 'route' ? 'active' : ''}">
    		경로
		</a>
        
        
        <div class="tab-item ${param.activeMenu == 'notification' ? 'active' : ''}">알림</div>
        
        <a href="${pageContext.request.contextPath}/mypage.do" 
        	class="tab-item ${param.activeMenu == 'my' ? 'active' : ''}">마이
        </a>
        
        <c:if test="${sessionScope.loginUser.role == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/admin.do" 
               class="tab-item ${param.activeMenu == 'admin' ? 'active' : ''}">
               관리자
            </a>
        </c:if>
    </div>
</nav>