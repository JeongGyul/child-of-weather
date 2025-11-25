<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>마이페이지 - 날씨의 아이</title>
    <!-- Tailwind CDN (개발용) -->
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50">

<jsp:include page="/WEB-INF/views/common/header.jsp">
        <jsp:param name="activeMenu" value="my" />
    </jsp:include>
    
<div class="min-h-screen flex flex-col items-center py-10">

    <!-- 전체 카드 -->
    <div class="w-full max-w-5xl bg-white rounded-2xl shadow-md p-8 space-y-8">

        <!-- 상단: 프로필 정보 -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <!-- 프로필 왼쪽 -->
            <div class="flex flex-col items-center border rounded-2xl p-6 bg-slate-50">
                <div class="w-24 h-24 rounded-full bg-blue-500 flex items-center justify-center text-white text-3xl font-semibold mb-4">
                    <c:choose>
                        <c:when test="${not empty userName}">
                            ${fn:substring(userName, 0, 1)}
                        </c:when>
                        <c:otherwise>
                            유
                        </c:otherwise>
                    </c:choose>
                </div>
                <div class="text-lg font-semibold mb-1">
                    <c:out value="${empty userName ? '사용자' : userName}"/>
                </div>
                <div class="text-gray-500 text-sm mb-2">
                    <c:out value="${empty userEmail ? 'user@weather.com' : userEmail}"/>
                </div>
                <span class="px-3 py-1 text-xs rounded-full bg-slate-200 text-gray-700">
                    <c:out value="${empty userRole ? '일반 회원' : userRole}"/>
                </span>
            </div>

            <!-- 프로필 상세 정보 -->
            <div class="md:col-span-2 space-y-4">
                <div class="flex justify-between items-center">
                    <h2 class="text-lg font-semibold">프로필 정보</h2>

                    <!-- 수정 버튼: 프로필 수정 페이지로 이동 -->
                    <form action="${pageContext.request.contextPath}/mypage/edit.do" method="get">
                        <button
                            type="submit"
                            class="px-4 py-2 text-sm rounded-lg border border-gray-300 hover:bg-gray-100">
                            수정
                        </button>
                    </form>
                </div>

                <div class="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                    <div class="space-y-1">
                        <div class="text-gray-400">이름</div>
                        <div class="font-medium">
                            <c:out value="${empty userName ? '사용자' : userName}"/>
                        </div>
                    </div>
                    <div class="space-y-1">
                        <div class="text-gray-400">이메일</div>
                        <div class="font-medium">
                            <c:out value="${empty userEmail ? 'user@weather.com' : userEmail}"/>
                        </div>
                    </div>

                    <div class="space-y-1">
                        <div class="text-gray-400">현재 접속 지역</div>
                        <div class="flex items-center justify-between">
                            <span class="font-medium">
                                <c:out value="${empty currentRegion ? '미설정' : currentRegion}"/>
                            </span>
                            <button class="text-xs text-blue-600 border border-blue-100 px-2 py-1 rounded-full">
                                자동 감지
                            </button>
                        </div>
                    </div>

                    <div class="space-y-1">
                        <div class="text-gray-400">가입일</div>
                        <div class="font-medium">
                            <c:out value="${sessionScope.loginUser.createdAt}"/>
                        </div>
                    </div>

                    <div class="space-y-1">
                        <div class="text-gray-400">마지막 로그인</div>
                        <div class="font-medium">
                            <c:out value="${sessionScope.loginUser.lastLoginAt}"/>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 활동 요약 -->
        <div class="space-y-4">
            <h2 class="text-lg font-semibold">활동 요약</h2>
            <p class="text-sm text-gray-500">등록한 활동 및 경로 정보</p>

            <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
                <div class="rounded-2xl bg-blue-50 p-6 text-center">
                    <div class="text-sm text-gray-500 mb-2">등록된 활동</div>
                    <div class="text-3xl font-bold">
                        <c:out value="${empty activityCount ? 0 : activityCount}"/>
                    </div>
                </div>
                <div class="rounded-2xl bg-green-50 p-6 text-center">
                    <div class="text-sm text-gray-500 mb-2">등록된 경로</div>
                    <div class="text-3xl font-bold">
                        <c:out value="${empty routeCount ? 0 : routeCount}"/>
                    </div>
                </div>
                <div class="rounded-2xl bg-purple-50 p-6 text-center">
                    <div class="text-sm text-gray-500 mb-2">받은 알림</div>
                    <div class="text-3xl font-bold">
                        <c:out value="${empty alertCount ? 0 : alertCount}"/>
                    </div>
                </div>
            </div>
        </div>

        <!-- 계정 관리 -->
        <div class="space-y-4">
            <h2 class="text-lg font-semibold text-red-600">계정 관리</h2>
            <p class="text-sm text-gray-500">로그아웃 또는 계정 설정</p>

            <form action="${pageContext.request.contextPath}/logout.do" method="get">
                <button
                    type="submit"
                    class="w-full bg-red-600 hover:bg-red-700 text-white font-semibold py-3 rounded-xl">
                    ⏻ 로그아웃
                </button>
            </form>
        </div>
    </div>

</div>
</body>
</html>
