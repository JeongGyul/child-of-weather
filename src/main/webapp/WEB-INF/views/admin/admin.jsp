<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    request.setCharacterEncoding("UTF-8");
%>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>관리자 페이지 - 날씨의 아이</title>
    <!-- 개발용 Tailwind CDN -->
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-50">
<div class="min-h-screen flex flex-col items-center py-10">

    <!-- 상단 제목 + 대시보드로 -->
    <div class="w-full max-w-5xl flex items-center justify-between mb-4 px-1">
        <h1 class="text-xl font-semibold text-gray-900">
            관리자 대시보드
        </h1>
        <a href="${pageContext.request.contextPath}/dashboard.do"
           class="inline-flex items-center gap-1 text-sm text-gray-700 border border-gray-300 rounded-lg px-3 py-1.5 hover:bg-gray-100">
            ← 대시보드로
        </a>
    </div>

    <!-- 본문 카드 -->
    <div class="w-full max-w-5xl bg-white rounded-2xl shadow-md p-8 space-y-8">

        <!-- 요약 카드 -->
        <section class="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div class="rounded-2xl bg-blue-50 p-6">
                <div class="text-sm text-gray-500 mb-1">전체 회원 수</div>
                <div class="text-3xl font-bold">
                    <c:out value="${totalMemberCount}" />
                </div>
            </div>
            <div class="rounded-2xl bg-green-50 p-6">
                <div class="text-sm text-gray-500 mb-1">신규 가입</div>
                <div class="text-3xl font-bold">
                    <c:out value="${newUserCount}" />
                </div>
                <div class="text-xs text-gray-500 mt-1">
                    * 더미 데이터
                </div>
            </div>
            <div class="rounded-2xl bg-purple-50 p-6">
                <div class="text-sm text-gray-500 mb-1">활성 사용자</div>
                <div class="text-3xl font-bold">
                    <c:out value="${activeUserCount}" />
                </div>
                <div class="text-xs text-gray-500 mt-1">
                    * 더미 데이터
                </div>
            </div>
        </section>

        <!-- 회원 목록 -->
        <section class="space-y-4">
            <h2 class="text-lg font-semibold">회원 목록</h2>
            <p class="text-sm text-gray-500">
                전체 회원 정보 및 삭제 관리
            </p>

            <div class="overflow-x-auto border rounded-2xl">
                <table class="min-w-full text-sm">
                    <thead class="bg-slate-50 border-b">
                    <tr>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">이름</th>
                        <th class="px-4 py-3 text-left font-medium text-gray-600">이메일</th>
                        <th class="px-4 py-3 text-center font-medium text-gray-600">관리</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="m" items="${memberList}">
                        <tr class="border-b last:border-b-0 hover:bg-slate-50">
                            <td class="px-4 py-3">
                                <c:out value="${m.name}" />
                            </td>
                            <td class="px-4 py-3">
                                <c:out value="${m.email}" />
                            </td>
                            <td class="px-4 py-3 text-center">
                                <form action="${pageContext.request.contextPath}/admin.do"
                                      method="post"
                                      onsubmit="return confirm('정말 이 회원을 삭제하시겠습니까?');">
                                    <input type="hidden" name="email" value="${m.email}" />
                                    <button type="submit"
                                            class="px-3 py-1.5 text-xs rounded-lg bg-red-600 text-white hover:bg-red-700">
                                        삭제
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty memberList}">
                        <tr>
                            <td colspan="3" class="px-4 py-6 text-center text-gray-400">
                                등록된 회원이 없습니다.
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </section>

    </div>
</div>
</body>
</html>
