<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입 실패</title>
</head>
<body>
<h2>회원가입에 실패했습니다.</h2>
<p>이미 사용 중인 아이디이거나 서버 오류일 수 있습니다.</p>
<a href="<%=request.getContextPath()%>/joinForm">다시 회원가입</a>
</body>
</html>
