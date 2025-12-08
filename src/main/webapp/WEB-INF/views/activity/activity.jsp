<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>활동 - Child Of Weather</title>

    <!-- 활동 페이지 전용 CSS -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/activity.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">
<div class="app-root">

    <!-- 공통 헤더 (네비바) -->
    <jsp:include page="/WEB-INF/views/common/header.jsp">
        <jsp:param name="activeMenu" value="activity" />
    </jsp:include>

    <!-- 메인 컨텐츠 -->
    <main class="main-wrapper">

        <!-- 상단 제목 영역 -->
        <header class="page-header">
            <div class="page-title-wrap">
                <h1 class="page-title">정적 활동 관리</h1>
                <p class="page-desc">날씨에 맞는 최적의 타이밍을 추천받으세요</p>
            </div>
            <button type="button" class="btn-primary" id="btn-add-activity">
                + 활동 추가
            </button>
        </header>

        <!-- 활동 카드 그리드 -->
        <section class="activity-grid" id="activity-grid">
            <c:forEach var="a" items="${activityList}">
                <article class="activity-card">
                    <!-- 카드 상단 헤더 -->
                    <div class="card-header">
                        <div class="card-title-wrap">
                            <!-- 아이콘 (이모지: HTML 코드로 표기) -->
                            <div class="activity-icon">
                                <c:choose>
                                    <c:when test="${a.activityTypeId == 1}">&#128085;</c:when>
                                    <c:when test="${a.activityTypeId == 2}">&#128663;</c:when>
                                    <c:when test="${a.activityTypeId == 3}">&#127939;</c:when>
                                    <c:when test="${a.activityTypeId == 4}">&#127793;</c:when>
                                    <c:when test="${a.activityTypeId == 5}">&#127912;</c:when>
                                    <c:otherwise>&#128221;</c:otherwise>
                                </c:choose>
                            </div>

                            <!-- 제목 + 소요시간 -->
                            <div>
                                <div class="card-title-main">
                                    <c:choose>
                                        <c:when test="${a.activityTypeId == 1}">빨래 건조</c:when>
                                        <c:when test="${a.activityTypeId == 2}">세차</c:when>
                                        <c:when test="${a.activityTypeId == 3}">실외 운동</c:when>
                                        <c:when test="${a.activityTypeId == 4}">정원 가꾸기</c:when>
                                        <c:when test="${a.activityTypeId == 5}">외벽 페인트</c:when>
                                        <c:otherwise>기타</c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="card-title-sub">
                                    소요 시간: -
                                </div>
                            </div>
                        </div>

                        <!-- 삭제 버튼: 폼으로 감싸서 POST 요청 -->
                        <form method="post" action="${pageContext.request.contextPath}/activity/delete.do">
                            <input type="hidden" name="op" value="delete" />
                            <input type="hidden" name="memberActivityId" value="${a.memberActivityId}" />
                            <button class="icon-button" type="submit" title="삭제하기">&#128465;</button>
                        </form>
                    </div>

                    <!-- 최적 타이밍 섹션 (지금은 고정 텍스트) -->
                    <div class="card-section">
                        <div class="section-label">
                            <span>&#9881; 최적 타이밍</span>
                            <span class="status-badge status-soon">
                                예정
                            </span>
                        </div>

                        <div class="time-row">
                            <span class="time-label">예정</span>
                            <span class="time-value">
                                날씨 데이터 분석 후 추천
                            </span>
                        </div>
                    </div>

                    <!-- 조건 칩 섹션 -->
                    <div class="card-section">
                        <div class="conditions-title">필요 조건:</div>
                        <div class="chip-row">
                            <c:set var="hasCondition" value="false" />

                            <c:if test="${not empty a.minTemperature}">
                                <span class="chip">최저 온도 ${a.minTemperature}℃</span>
                                <c:set var="hasCondition" value="true" />
                            </c:if>

                            <c:if test="${not empty a.maxTemperature}">
                                <span class="chip">최고 온도 ${a.maxTemperature}℃</span>
                                <c:set var="hasCondition" value="true" />
                            </c:if>

                            <c:if test="${not empty a.maxHumidity}">
                                <span class="chip">최대 습도 ${a.maxHumidity}%</span>
                                <c:set var="hasCondition" value="true" />
                            </c:if>

                            <c:if test="${not empty a.maxPrecipitation}">
                                <span class="chip">최대 강수확률 ${a.maxPrecipitation}%</span>
                                <c:set var="hasCondition" value="true" />
                            </c:if>

                            <c:if test="${hasCondition == false}">
                                <span class="chip">조건 미설정</span>
                            </c:if>
                        </div>
                    </div>
                </article>
            </c:forEach>
        </section>

    </main>
</div>

<!-- 🔹 새 활동 추가 모달 -->
<div class="modal-overlay" id="activity-modal-overlay">
    <div class="modal">
        <header class="modal-header">
            <div>
                <h2 class="modal-title">새 활동 추가</h2>
                <p class="modal-desc">
                    날씨 조건을 설정하면 최적의 타이밍을 자동으로 추천해드립니다.
                </p>
            </div>
            <button type="button" class="modal-close-btn" id="btn-modal-close">&#10005;</button>
        </header>

        <!-- 서버로 바로 POST -->
        <form
            class="modal-body"
            id="activity-form"
            method="post"
            action="${pageContext.request.contextPath}/activity.do"
        >
            <!-- 활동 유형 -->
            <div class="form-group full">
                <label class="form-label" for="activity-type">활동 유형</label>
                <div class="select-wrapper">
                    <select id="activity-type" name="activityTypeId" class="form-select" required>
                        <option value="" disabled selected>활동 유형을 선택하세요</option>
                        <option value="1" data-icon="&#128085;">빨래 건조</option>
                        <option value="2" data-icon="&#128663;">세차</option>
                        <option value="3" data-icon="&#127939;">실외 운동</option>
                        <option value="4" data-icon="&#127793;">정원 가꾸기</option>
                        <option value="5" data-icon="&#127912;">외벽 페인트</option>
                        <option value="6" data-icon="&#128221;">기타</option>
                    </select>
                </div>
            </div>

            <!-- 최저/최고 온도, 습도, 강수 조건 -->
            <div class="form-group">
                <label class="form-label" for="min-temp">최저 온도 (℃)</label>
                <input
                    type="number"
                    id="min-temp"
                    name="minTemp"
                    class="form-input"
                    placeholder="예: 15"
                    value="15"
                >
            </div>

            <div class="form-group">
                <label class="form-label" for="max-temp">최고 온도 (℃)</label>
                <input
                    type="number"
                    id="max-temp"
                    name="maxTemp"
                    class="form-input"
                    placeholder="예: 30"
                    value="30"
                >
            </div>

            <div class="form-group">
                <label class="form-label" for="max-humidity">최대 습도 (%)</label>
                <input
                    type="number"
                    id="max-humidity"
                    name="maxHumidity"
                    class="form-input"
                    placeholder="예: 60"
                    value="60"
                >
            </div>

            <div class="form-group">
                <label class="form-label" for="max-pop">최대 강수확률 (%)</label>
                <input
                    type="number"
                    id="max-pop"
                    name="maxPop"
                    class="form-input"
                    placeholder="예: 20"
                    value="20"
                >
            </div>

            <!-- 모달 버튼 -->
            <div class="modal-footer">
                <button type="button" class="btn-secondary" id="btn-modal-cancel">취소</button>
                <button type="submit" class="btn-primary">추가</button>
            </div>
        </form>
    </div>
</div>

<!-- 활동 모달 제어 JS -->
<script src="${pageContext.request.contextPath}/js/activity.js"></script>
</body>
</html>
