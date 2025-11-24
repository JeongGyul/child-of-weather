<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>로그인</title>
</head>
<body>
<h1>로그인</h1>

<form action="<%=request.getContextPath()%>/login.do" method="post">
    아이디: <input type="text" name="id"><br>
    비밀번호: <input type="password" name="pw"><br>
    <button type="submit">로그인</button>
</form>

<p>
    아직 회원이 아니신가요?
    <a href="<%=request.getContextPath()%>/joinForm">회원가입</a>
</p>
</body>
</html>
