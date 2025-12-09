<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관리자 - 날씨의 아이</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<div class="app-root">
    
    <jsp:include page="/WEB-INF/views/common/header.jsp">
        <jsp:param name="activeMenu" value="admin" />
    </jsp:include>

    <main class="main-wrapper">
        
        <div class="page-header">
            <h2 class="page-title">관리자 페이지</h2>
            <p class="page-desc">전체 회원 관리 및 통계 현황</p>
        </div>

        <div class="stats-grid">
            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-title">전체 회원</span>
                    <span class="stat-icon">👥</span>
                </div>
                <div class="stat-value">${adminPageInfo.totalUsers}</div>
                <div class="stat-desc">일반 ${adminPageInfo.userCount}명 / 관리자 ${adminPageInfo.adminCount}명</div>
            </div>

            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-title">신규 가입</span>
                    <span class="stat-icon">📈</span>
                </div>
                <div class="stat-value">${adminPageInfo.newJoinCount}</div>
                <div class="stat-desc">최근 가입자</div>
            </div>

            <div class="stat-card">
                <div class="stat-header">
                    <span class="stat-title">활성 사용자</span>
                    <span class="stat-icon">⚡</span>
                </div>
                <div class="stat-value">${adminPageInfo.activeUserCount}</div> 
                <div class="stat-desc">7일 이내 접속</div>
            </div>
        </div>

        <div class="list-card">
            <div class="list-header">
                <div class="list-title">회원 목록</div>
            </div>

            <table class="data-table">
                <thead>
                    <tr>
                        <th width="15%">이름</th>
                        <th width="25%">이메일</th>
                        <th width="15%">역할</th>
                        <th width="20%">가입일</th>
                        <th width="15%">마지막 로그인</th>
                        <th width="10%" class="text-right">관리</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="user" items="${adminPageInfo.userList}">
                        <tr>
                            <td>
                                <span style="font-weight: 600;">${user.name}</span>
                            </td>
                            <td>${user.email}</td>
                            <td>${user.role == 'USER' ? '일반' : '관리자'}</td>
                            <td>${user.createdAt}</td>
                            <td>${user.lastLoginAt}</td>
                            <td class="text-right">
                                <button class="btn-delete" onclick="confirmDelete('${user.memberId}', '${user.name}')">
                                    강제 탈퇴
                                </button>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <c:if test="${empty adminPageInfo.userList}">
                        <tr>
                            <td colspan="6" style="text-align: center; padding: 60px; color: #9ca3af;">
                                회원이 없습니다.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>

    </main>

    <footer class="footer" style="text-align: center; padding: 40px 0; color: #9ca3af; font-size: 12px;">
        © 2025 날씨의 아이. All rights reserved.
    </footer>
</div>

<script src="${pageContext.request.contextPath}/js/admin.js"></script>
</body>
</html>