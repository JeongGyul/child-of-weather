<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>


<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>대시보드 - 날씨의 아이</title>

    <!-- 외부 CSS 연결 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/dashboard.css">
</head>
<body>
<div class="app-root">

    <jsp:include page="/WEB-INF/views/common/header.jsp">
        <jsp:param name="activeMenu" value="dashboard" />
    </jsp:include>

    <!-- 메인 내용 -->
    <main class="main-wrapper">

        <!-- 현재 날씨 카드 -->
        <section class="current-weather-card">
            <div class="cw-left">
                <div class="cw-left-location">현재 날씨 · 경북</div>
                <div class="cw-left-temp">
                    <div class="cw-temp-value">18°</div>
                </div>
                <div class="cw-condition">흐림</div>
                <div class="cw-left-sub">오늘도 맑은 하루가 예상됩니다.</div>
            </div>
            <div class="cw-right">
                <div class="cw-stat-row">
                    <div class="cw-stat-pill">습도 45%</div>
                    <div class="cw-stat-pill">바람 2.5m/s</div>
                </div>
                <div class="cw-stat-row">
                    <div class="cw-stat-pill">강수 10%</div>
                </div>
            </div>
        </section>

        <!-- 오늘의 추천 -->
        <section>
            <h2 class="section-title">오늘의 추천</h2>

            <!-- JS가 이 안을 다 채움 -->
            <div class="recommend-row" id="recommend-row">
                <!-- <div class="rec-placeholder">날씨 정보를 불러오는 중...</div> -->
            </div>
        </section>

        <!-- 시간별 예보 -->
        <section>
            <div class="hourly-card">
                <div class="hourly-header">
                    <h2 class="section-title" style="margin-top:0;">시간별 예보</h2>
                    <p>오늘의 날씨 변화</p>
                </div>
                <div class="hourly-row" id="hourly-row">
                    <!-- JS에서 시간별 예보 아이템을 동적으로 채움 -->
                </div>

            </div>
        </section>

    </main>

    <footer class="footer">
        © 2025 날씨의 아이. All rights reserved.
    </footer>
    <script>
        // 컨텍스트 경로를 JS에서 쓸 수 있게 전역으로 노출
        window.appContextPath = '${pageContext.request.contextPath}';
    </script>
    <script src="${pageContext.request.contextPath}/js/dashboard.js"></script>

</div>
</body>
</html>
