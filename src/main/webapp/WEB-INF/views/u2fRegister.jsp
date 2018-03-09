<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<c:set var="contextPath" value="${pageContext.request.contextPath}"/>

<html>
<body>
    <form action="${contextPath}/u2fFinishRegister?otp=${otp}" method="POST">
        <label for="username">Username</label>
        <input name="username" id="username" autofocus/>
        <br><br>
        <label for="otp">YubiKey OTP</label>
        <input name="otp" id="otp" value="otp">
        <input type="hidden" name="otp" value="${otp}"/>
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <br><br>
        <button type="Submit">Register a YubiKey</button>
    </form>
</body>
</html>