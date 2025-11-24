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
                <div class="cw-condition">맑음</div>
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
            <div class="recommend-row">

                <!-- 빨래 건조 -->
                <article class="rec-card">
                    <div class="rec-header">
                        <div class="rec-left">
                            <div class="rec-icon sun">☀</div>
                            <div class="rec-title">빨래 건조</div>
                        </div>
                        <div class="rec-badge optimal">최적</div>
                    </div>
                    <div class="rec-body">
                        <div class="rec-time-row">
                            <span class="rec-time-icon">🕒</span>
                            <span>오전 10시 ~ 오후 4시</span>
                        </div>
                        <span>습도 낮고 바람 적당, 건조에 최적</span>
                    </div>
                </article>

                <!-- 세차 -->
                <article class="rec-card">
                    <div class="rec-header">
                        <div class="rec-left">
                            <div class="rec-icon carwash">🧽</div>
                            <div class="rec-title">세차</div>
                        </div>
                        <div class="rec-badge good">좋음</div>
                    </div>
                    <div class="rec-body">
                        <div class="rec-time-row">
                            <span class="rec-time-icon">🕒</span>
                            <span>오늘 오후 2시 이후</span>
                        </div>
                        <span>강수 확률 낮음, 72시간 동안 비 예보 없음</span>
                    </div>
                </article>

                <!-- 출퇴근길 -->
                <article class="rec-card">
                    <div class="rec-header">
                        <div class="rec-left">
                            <div class="rec-icon route">⚠</div>
                            <div class="rec-title">출퇴근길</div>
                        </div>
                        <div class="rec-badge warn">주의</div>
                    </div>
                    <div class="rec-body">
                        <div class="rec-time-row">
                            <span class="rec-time-icon">🕒</span>
                            <span>오전 8시</span>
                        </div>
                        <span>미세먼지 보통, 마스크 착용 권장</span>
                    </div>
                </article>

            </div>
        </section>

        <!-- 시간별 예보 -->
        <section>
            <div class="hourly-card">
                <div class="hourly-header">
                    <h2 class="section-title" style="margin-top:0;">시간별 예보</h2>
                    <p>오늘의 날씨 변화</p>
                </div>
                <div class="hourly-row">
                    <div class="hour-item">
                        <div class="hour-time">09:00</div>
                        <div class="hour-icon">☀</div>
                        <div class="hour-temp">16°</div>
                        <div class="hour-pop">0%</div>
                    </div>
                    <div class="hour-item">
                        <div class="hour-time">12:00</div>
                        <div class="hour-icon">☀</div>
                        <div class="hour-temp">20°</div>
                        <div class="hour-pop">0%</div>
                    </div>
                    <div class="hour-item">
                        <div class="hour-time">15:00</div>
                        <div class="hour-icon">☀</div>
                        <div class="hour-temp">22°</div>
                        <div class="hour-pop">5%</div>
                    </div>
                    <div class="hour-item">
                        <div class="hour-time">18:00</div>
                        <div class="hour-icon">🌧</div>
                        <div class="hour-temp">18°</div>
                        <div class="hour-pop">10%</div>
                    </div>
                    <div class="hour-item">
                        <div class="hour-time">21:00</div>
                        <div class="hour-icon">🌧</div>
                        <div class="hour-temp">15°</div>
                        <div class="hour-pop">20%</div>
                    </div>
                </div>
            </div>
        </section>

    </main>

    <footer class="footer">
        © 2025 날씨의 아이. All rights reserved.
    </footer>

</div>
</body>
</html>
