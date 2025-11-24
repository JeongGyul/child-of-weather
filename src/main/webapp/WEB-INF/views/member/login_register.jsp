<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>날씨의 아이 - 로그인</title>
    
    <script src="https://cdn.tailwindcss.com"></script>
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body class="min-h-screen bg-gradient-to-br from-blue-500 via-indigo-500 to-purple-600 flex items-center justify-center p-4">

    <div class="w-full max-w-md">
        <div class="text-center mb-8">
            <div class="inline-flex items-center gap-3 mb-4">
                <div class="bg-white p-3 rounded-2xl shadow-lg">
                    <svg xmlns="http://www.w3.org/2000/svg" width="40" height="40" viewBox="0 0 24 24" fill="none" stroke="#2563eb" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide lucide-cloud"><path d="M17.5 19c0-1.7-1.3-3-3-3c-1.1 0-2.1.6-2.6 1.5c-.5-.9-1.5-1.5-2.6-1.5c-1.7 0-3 1.3-3 3"/><path d="M3 16.6A6 6 0 0 1 12 7a6 6 0 0 1 5.6 4.4a3.5 3.5 0 0 1 2.3 6.6"/></svg>
                </div>
            </div>
            <h1 class="text-white text-3xl font-bold mb-2">날씨의 아이</h1>
            <p class="text-blue-100">똑똑한 개인 맞춤형 날씨 알림 서비스</p>
        </div>

        <div class="bg-white rounded-xl shadow-2xl overflow-hidden border border-gray-200">
            
            <div class="grid grid-cols-2 p-1 bg-gray-100 m-4 rounded-lg">
                <button onclick="switchTab('login')" id="btn-login" class="tab-btn active py-2 text-sm font-medium rounded-md transition-all">로그인</button>
                <button onclick="switchTab('signup')" id="btn-signup" class="tab-btn py-2 text-sm font-medium rounded-md transition-all text-gray-500 hover:text-gray-900">회원가입</button>
            </div>
			
            <c:if test="${not empty error}">
                <div class="mx-6 p-3 bg-red-50 text-red-600 text-sm rounded-md flex items-center gap-2 border border-red-200">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"/><line x1="12" x2="12" y1="8" y2="12"/><line x1="12" x2="12.01" y1="16" y2="16"/></svg>
                    <span>${error}</span>
                </div>
            </c:if>
            
            <c:if test="${not empty successMessage}">
                <div class="mx-6 p-3 mb-4 bg-green-50 text-green-600 text-sm rounded-md flex items-center gap-2 border border-green-200">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                    <span>${successMessage}</span>
                </div>
            </c:if>

            <div id="tab-login" class="tab-content active p-6 pt-2">
                <div class="mb-6">
                    <h2 class="text-2xl font-bold tracking-tight">로그인</h2>
                    <p class="text-sm text-gray-500">계정에 로그인하여 맞춤 날씨 정보를 받아보세요</p>
                </div>
                <form action="${pageContext.request.contextPath}/login.do" method="post" class="space-y-4">
                    <div class="space-y-2">
                        <label for="login-email" class="text-sm font-medium leading-none">이메일</label>
                        <input type="email" name="email" id="login-email" placeholder="example@email.com" required class="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400">
                    </div>
                    <div class="space-y-2">
                        <label for="login-password" class="text-sm font-medium leading-none">비밀번호</label>
                        <input type="password" name="password" id="login-password" placeholder="••••••••" required class="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400">
                    </div>
                    <button type="submit" class="w-full bg-black text-white hover:bg-gray-800 h-10 px-4 py-2 rounded-md text-sm font-medium transition-colors">로그인</button>
                </form>
            </div>

            <div id="tab-signup" class="tab-content p-6 pt-2">
                <div class="mb-6">
                    <h2 class="text-2xl font-bold tracking-tight">회원가입</h2>
                    <p class="text-sm text-gray-500">새 계정을 만들어 서비스를 시작하세요</p>
                </div>
                <form action="${pageContext.request.contextPath}/join.do" method="post" class="space-y-4">
                    <div class="space-y-2">
                        <label for="signup-name" class="text-sm font-medium leading-none">이름</label>
                        <input type="text" name="name" id="signup-name" placeholder="홍길동" required class="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400">
                    </div>
                    <div class="space-y-2">
                        <label for="signup-email" class="text-sm font-medium leading-none">이메일</label>
                        <input type="email" name="email" id="signup-email" placeholder="example@email.com" required class="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400">
                    </div>
                    <div class="space-y-2">
                        <label for="signup-password" class="text-sm font-medium leading-none">비밀번호</label>
                        <input type="password" name="password" id="signup-password" placeholder="••••••••" required class="flex h-10 w-full rounded-md border border-gray-300 bg-transparent px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-blue-400">
                    </div>
                    <button type="submit" class="w-full bg-black text-white hover:bg-gray-800 h-10 px-4 py-2 rounded-md text-sm font-medium transition-colors">회원가입</button>
                </form>
            </div>

        </div>

        <p class="text-center text-sm text-blue-100 mt-4">© 2025 날씨의 아이. All rights reserved.</p>
    </div>

    <script src="${pageContext.request.contextPath}/js/login.js"></script>
</body>
</html>