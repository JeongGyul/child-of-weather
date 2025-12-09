<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>프로필 수정 - 날씨의 아이</title>
    <!-- Tailwind (개발용) -->
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50">
<div class="min-h-screen flex flex-col items-center py-10">

    <div class="w-full max-w-md bg-white rounded-2xl shadow-md p-8 space-y-6">
        <h1 class="text-xl font-semibold text-gray-900 mb-2">프로필 수정</h1>
        <p class="text-sm text-gray-500 mb-4">
            이름과 이메일 정보를 수정할 수 있습니다.
        </p>

        <!-- 에러 메시지 -->
        <c:if test="${not empty errorMsg}">
            <div class="mb-4 text-sm text-red-600 bg-red-50 border border-red-100 px-3 py-2 rounded-lg">
                ${errorMsg}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/mypage/edit.do" method="post" class="space-y-4">
            <!-- 이름 -->
            <div>
                <label for="name" class="block text-sm font-medium text-gray-700 mb-1">
                    이름
                </label>
                <input
                    type="text"
                    id="name"
                    name="name"
                    value="${not empty newName? newName : sessionScope.loginUser.name}"
                    required
                    class="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
            </div>

            <!-- 이메일 -->
            <div>
                <label for="email" class="block text-sm font-medium text-gray-700 mb-1">
                    이메일
                </label>
                <input
                    type="email"
                    id="email"
                    name="email"
                    value="${not empty newEmail? newEmail : sessionScope.loginUser.email}"
                    required
                    class="w-full border rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                />
            </div>

            <!-- 버튼 영역 -->
            <div class="flex gap-2 pt-2">
                <!-- 홈으로 이동 -->
                <a href="${pageContext.request.contextPath}/dashboard.do"
                   class="flex-1 inline-flex items-center justify-center border border-gray-300 rounded-lg px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                    홈으로
                </a>

                <!-- 마이페이지로 (수정 취소) -->
                <a href="${pageContext.request.contextPath}/mypage.do"
                   class="flex-1 inline-flex items-center justify-center border border-gray-300 rounded-lg px-4 py-2 text-sm text-gray-700 hover:bg-gray-100">
                    취소
                </a>

                <!-- 저장 -->
                <button
                    type="submit"
                    class="flex-1 inline-flex items-center justify-center bg-blue-600 hover:bg-blue-700 text-white rounded-lg px-4 py-2 text-sm font-medium">
                    저장
                </button>
            </div>
        </form>
    </div>

</div>
</body>
</html>
