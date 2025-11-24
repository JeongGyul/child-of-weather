<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>회원가입</title>
</head>
<body>
<h1>회원가입</h1>

<form action="<%=request.getContextPath()%>/join.do" method="post">
    아이디: <input type="text" name="id"><br>
    비밀번호: <input type="password" name="pw"><br>
    이름: <input type="text" name="name"><br>
    이메일: <input type="text" name="email"><br>
    <button type="submit">가입하기</button>
</form>

<p><a href="<%=request.getContextPath()%>/index.jsp">로그인 화면으로</a></p>
</body>
</html>
