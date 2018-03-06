<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>U2F Register</title>

<script src="${contextPath}/resources/js/u2f-api-1.1.js"></script>

<script>
<%HttpSession sess = request.getSession(true);
	session.getAttribute("data");%>
var json = '<%= session.getAttribute("data") %>';
var request = JSON.parse(json);
console.log("request is " +request);
console.log("request appId " +request.appId);
console.log("request registeredKeys " +request.registeredKeys);
setTimeout(function() {
	console.log("request appId inside setTimeout" + " " +request.appId);
	console.log("request appId inside setTimeout" + " " +request.registeredKeys.length);
	console.log("request appId inside setTimeout" + " " +request.registerRequests.length);
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

    <body>
    <p>Touch your U2F token to register.</p>
    	<b><%= request.getParameter("username") %></b>
<%--         <form method="POST" action="u2fFinishRegistration" id="form" onsubmit="return false;">
            <input type="hidden" name="username" value="<%= request.getParameter("username") %>"/>
            <input type="hidden" name="tokenResponse" id="tokenResponse"/>
        </form> --%>
        <form method="POST" action="${contextPath}/u2fFinishRegister" id="form" onsubmit="return false;">
            <input type="hidden" name="username" value="<%= request.getParameter("username") %>"/>
            <input type="hidden" name="tokenResponse" id="tokenResponse"/>
    	</form>
    </body>
</html>