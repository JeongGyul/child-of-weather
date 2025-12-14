<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>활동 - Child Of Weather</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/activity.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">
<div class="app-root">

    <jsp:include page="/WEB-INF/views/common/header.jsp">
        <jsp:param name="activeMenu" value="activity" />
    </jsp:include>

    <main class="main-wrapper">

        <header class="page-header">
            <div class="page-title-wrap">
                <h1 class="page-title">정적 활동 관리</h1>
                <p class="page-desc">날씨에 맞는 최적의 타이밍을 추천받으세요</p>
            </div>
            <button type="button" class="btn-primary" id="btn-add-activity">
                + 활동 추가
            </button>
        </header>

        <section class="activity-grid" id="activity-grid">
            <c:forEach var="a" items="${activityList}">
                <article class="activity-card"
                         data-id="${a.memberActivityId}"
                         data-min-temp="${a.minTemperature}"
                         data-max-temp="${a.maxTemperature}"
                         data-max-humid="${a.maxHumidity}"
                         data-max-pop="${a.maxPrecipitation}"
                         data-duration="${a.defaultDurationMin != null ? a.defaultDurationMin : 60}">
                    
                    <div class="card-header">
                        <div class="card-title-wrap">
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

                            <div>
                                <div class="card-title-main">
                                    <c:choose>
                                        <c:when test="${a.activityTypeId == 1}">빨래 건조</c:when>
                                        <c:when test="${a.activityTypeId == 2}">세차</c:when>
                                        <c:when test="${a.activityTypeId == 3}">실외 운동</c:when>
                                        <c:when test="${a.activityTypeId == 4}">정원 가꾸기</c:when>
                                    </c:choose>
                                </div>
                                <div class="card-title-sub">
                                    소요 시간: ${a.defaultDurationMin != null ? a.defaultDurationMin : 60}분
                                </div>
                            </div>
                        </div>

                        <form method="post" action="${pageContext.request.contextPath}/activity/delete.do">
                            <input type="hidden" name="op" value="delete" />
                            <input type="hidden" name="memberActivityId" value="${a.memberActivityId}" />
                            <button class="icon-button" type="submit" title="삭제하기">&#128465;</button>
                        </form>
                    </div>

                    <div class="card-section">
                        <div class="section-label">
                            <span>&#9881; 최적 타이밍</span>
                            <c:choose>
                                <c:when test="${not empty a.recommendation and a.recommendation.timingStatus == 'BEST_NOW'}">
                                    <span class="status-badge status-good">진행 가능</span>
                                </c:when>
                                <c:when test="${not empty a.recommendation and a.recommendation.timingStatus == 'PLANNED'}">
                                    <span class="status-badge status-good" style="background-color: #e3f2fd; color: #1565c0;">예정됨</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="status-badge status-bad">보류 권장</span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <div class="time-row">
                            <span class="time-label">추천</span>
                            <span class="time-value" style="font-weight: bold; color: ${not empty a.recommendation ? '#2e7d32' : '#c62828'};">
                                <c:choose>
                                    <c:when test="${not empty a.recommendation}">
                                        <%-- DTO에 추가한 getter 사용 (날짜 + 시작시간 ~ 종료시간) --%>
                                        ${a.recommendation.formattedDate} 
                                        ${a.recommendation.formattedStartTime} ~ ${a.recommendation.formattedEndTime}
                                    </c:when>
                                    <c:otherwise>
                                        조건에 맞는 시간 없음
                                    </c:otherwise>
                                </c:choose>
                            </span>
                        </div>
                    </div>

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

<div class="modal-overlay" id="activity-modal-overlay">
    <div class="modal">
        <header class="modal-header">
            <div>
                <h2 class="modal-title">새 활동 추가</h2>
                <p class="modal-desc">
                    오늘의 날씨 정보가 기본으로 입력되어 있습니다.
                </p>
            </div>
            <button type="button" class="modal-close-btn" id="btn-modal-close">&#10005;</button>
        </header>

        <form class="modal-body" id="activity-form" method="post" action="${pageContext.request.contextPath}/activity.do">
            <div class="form-group full">
                <label class="form-label" for="activity-type">활동 유형</label>
                <div class="select-wrapper">
                    <select id="activity-type" name="activityTypeId" class="form-select" required>
                        <option value="" disabled selected>활동 유형을 선택하세요</option>
                        <option value="1" data-icon="&#128085;">빨래 건조</option>
                        <option value="2" data-icon="&#128663;">세차</option>
                        <option value="3" data-icon="&#127939;">실외 운동</option>
                        <option value="4" data-icon="&#127793;">정원 가꾸기</option>
                    </select>
                </div>
            </div>

            <div class="form-group">
                <label class="form-label" for="min-temp">최저 온도 (℃)</label>
                <input type="number" id="min-temp" name="minTemp" class="form-input" placeholder="예: 15">
            </div>

            <div class="form-group">
                <label class="form-label" for="max-temp">최고 온도 (℃)</label>
                <input type="number" id="max-temp" name="maxTemp" class="form-input" placeholder="예: 30">
            </div>

            <div class="form-group">
                <label class="form-label" for="max-humidity">최대 습도 (%)</label>
                <input type="number" id="max-humidity" name="maxHumidity" class="form-input" placeholder="예: 60">
            </div>

            <div class="form-group">
                <label class="form-label" for="max-pop">최대 강수확률 (%)</label>
                <input type="number" id="max-pop" name="maxPop" class="form-input" placeholder="예: 20">
            </div>

            <div class="modal-footer">
                <button type="button" class="btn-secondary" id="btn-modal-cancel">취소</button>
                <button type="submit" class="btn-primary">추가</button>
            </div>
        </form>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/activity.js"></script>
</body>
</html>