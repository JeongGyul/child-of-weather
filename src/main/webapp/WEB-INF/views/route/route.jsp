<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>경로 관리 - 날씨의 아이</title>

   <!-- route.jsp 상단 <head> 안에 있는 네이버 지도 JS 로드 부분 -->

<!-- 네이버 지도 JS (childofweather1 Application의 Client ID 사용) -->
	<script
   	 type="text/javascript"
   	 src="https://oapi.map.naver.com/openapi/v3/maps.js?ncpKeyId=YOUR_MAP_KEY&submodules=geocoder&callback=initRouteMap">
	</script>



    <!-- 공통 헤더 스타일 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/header.css">
    <!-- 경로 페이지 전용 스타일 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/route.css">

    <!-- 경로 페이지 전용 JS -->
    <script>
        // contextPath를 JS에서 쓸 수 있게전역으로 노출
        window.appContextPath = '${pageContext.request.contextPath}';
    </script>
    <script defer src="${pageContext.request.contextPath}/js/route.js"></script>
</head>
<body>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="activeMenu" value="route"/>
</jsp:include>

<div class="app-root">
    <main class="main-wrapper">
        <!-- 상단 헤더 -->
        <div class="page-header">
            <div class="page-title-wrap">
                <h2 class="page-title">동적 경로 관리</h2>
                <p class="page-desc">경로별 날씨 위험을 미리 확인하세요</p>
            </div>
            <button id="btn-open-route-modal" class="btn-primary btn-add-route">
                <span class="btn-plus">＋</span>
                <span>경로 추가</span>
            </button>
        </div>

        <!-- 출발지/도착지 검색 + 네이버 지도 영역 -->
        <section class="route-search-section">
            <div class="route-search-form">
                <div class="form-group">
                    <label class="form-label" for="search-start">출발지 검색</label>
                    <input
                        id="search-start"
                        type="text"
                        class="form-input"
                        placeholder="예: 서울역"
                    />
                </div>
                <div class="form-group">
                    <label class="form-label" for="search-end">도착지 검색</label>
                    <input
                        id="search-end"
                        type="text"
                        class="form-input"
                        placeholder="예: 강남역"
                    />
                </div>
                <button type="button" id="btn-search-route" class="btn-primary">
                    경로 검색
                </button>
            </div>

            <div id="route-map" class="route-map"></div>
        </section>

        <!-- 경로 카드 리스트 -->
        <div id="route-list" class="route-grid"></div>

        <!-- 빈 상태 표시 -->
        <div id="route-empty" class="route-empty">
            <div class="route-empty-card">
                <p class="route-empty-main">등록된 경로가 없습니다.</p>
                <p class="route-empty-sub">경로를 추가하여 날씨 알림을 받으세요.</p>
            </div>
        </div>
    </main>
</div>

<!-- 경로 추가 모달 -->
<div id="route-modal-overlay" class="modal-overlay">
    <div class="modal">
        <div class="modal-header">
            <div>
                <h3 class="modal-title">새 경로 추가</h3>
                <p class="modal-desc">
                    경로의 날씨 정보와 위험 알림을 받을 수 있습니다.
                </p>
            </div>
            <button type="button" id="btn-close-route-modal" class="modal-close-btn">✕</button>
        </div>

        <form id="route-form" class="modal-body">
            <!-- 경로 이름 -->
            <div class="form-group full">
                <label for="name" class="form-label">경로 이름</label>
                <input
                    id="name"
                    name="name"
                    type="text"
                    class="form-input"
                    placeholder="예: 출퇴근길"
                    required
                />
            </div>

            <!-- 아이콘 선택 -->
            <div class="form-group full">
                <label for="icon" class="form-label">아이콘</label>
                <div class="select-wrapper">
                    <select id="icon" name="icon" class="form-select">
                        <option value="">아이콘을 선택하세요</option>
                        <option value="🚗">🚗 자동차</option>
                        <option value="🚇">🚇 지하철</option>
                        <option value="🚌">🚌 버스</option>
                        <option value="🚴">🚴 자전거</option>
                        <option value="🏃">🏃 도보</option>
                        <option value="⛰️">⛰️ 등산</option>
                    </select>
                </div>
            </div>

            <!-- 출발지 / 도착지 -->
            <div class="form-group">
                <label for="start" class="form-label">출발지</label>
                <input
                    id="start"
                    name="start"
                    type="text"
                    class="form-input"
                    placeholder="예: 집"
                    required
                />
            </div>

            <div class="form-group">
                <label for="end" class="form-label">도착지</label>
                <input
                    id="end"
                    name="end"
                    type="text"
                    class="form-input"
                    placeholder="예: 회사"
                    required
                />
            </div>

            <!-- 출발 시간 -->
            <div class="form-group">
                <label for="time" class="form-label">출발 시간</label>
                <input
                    id="time"
                    name="time"
                    type="time"
                    class="form-input"
                    required
                />
            </div>

            <!-- 도착 시간(선택) -->
            <div class="form-group">
                <label for="arrivalTime" class="form-label">도착 시간 (선택사항)</label>
                <input
                    id="arrivalTime"
                    name="arrivalTime"
                    type="time"
                    class="form-input"
                    placeholder="도착 예정 시간을 입력하세요"
                />
                <p class="form-help-text">
                    도착 시간을 입력하면 경로 전체 시간대의 날씨를 확인할 수 있습니다.
                </p>
            </div>

            <!-- 반복 요일 -->
            <div class="form-group full">
                <span class="form-label">반복 요일</span>
                <div class="day-chip-row">
                    <label class="day-chip">
                        <input type="checkbox" name="days" value="월" />
                        <span>월</span>
                    </label>
                    <label class="day-chip">
                        <input type="checkbox" name="days" value="화" />
                        <span>화</span>
                    </label>
                    <label class="day-chip">
                        <input type="checkbox" name="days" value="수" />
                        <span>수</span>
                    </label>
                    <label class="day-chip">
                        <input type="checkbox" name="days" value="목" />
                        <span>목</span>
                    </label>
                    <label class="day-chip">
                        <input type="checkbox" name="days" value="금" />
                        <span>금</span>
                    </label>
                    <label class="day-chip">
                        <input type="checkbox" name="days" value="토" />
                        <span>토</span>
                    </label>
                    <label class="day-chip">
                        <input type="checkbox" name="days" value="일" />
                        <span>일</span>
                    </label>
                </div>
                <p class="form-help-text">
                    선택하지 않으면 평일(월–금)이 기본값입니다.
                </p>
            </div>

            <!-- 푸터 버튼 -->
            <div class="modal-footer">
                <button type="button" id="btn-cancel-route" class="btn-secondary">취소</button>
                <button type="submit" class="btn-primary">추가</button>
            </div>
        </form>
    </div>
</div>

</body>
</html>
