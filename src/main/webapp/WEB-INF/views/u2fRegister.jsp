<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>U2F Register</title>

<script src="${contextPath}/resources/js/u2f-api-1.1.js"></script>

<script>
var request = ${data};
setTimeout(function() {
    u2f.register(
        request.appId,
        request.registerRequests,
        request.registeredKeys,
        function(data) {
            var form = document.getElementById('form');
            var reg = document.getElementById('tokenResponse');
            console.log("reg is " +reg);
            if(data.errorCode) {
                switch (data.errorCode) {
                    case 4:
                        alert("This device is already registered.");
                        break;

                    default:
                        alert("U2F failed with error: " + data.errorCode);
                }
            } else {
                reg.value=JSON.stringify(data);
                form.submit();
            }
        }
    );
}, 1000);
</script>
</head>
<!-- <body>

<form method="GET" action="/startRegistration" id="form">
    <label for="username">Username</label>
    <input name="username" id="username" autofocus/>
    <button type="Submit">Register</button>
</form>

</body> -->
    <body>
    <p>Touch your U2F token.</p>
        <form method="POST" action="finishRegistration" id="form" onsubmit="return false;">
            <input type="hidden" name="username" value="${username}"/>
            <input type="hidden" name="tokenResponse" id="tokenResponse"/>
        </form>
    </body>
</html>