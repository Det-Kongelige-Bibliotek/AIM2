<!DOCTYPE html>
<html lang="en" xmlns:spring="http://www.springframework.org/tags" 
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core">
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<head>
<title>Hello World</title>
<jsp:include page="includes/head.jsp" />
<body>
<spring:url  value="/resources/text.txt" htmlEscape="true" var="springUrl" />
<strong>Spring URL:</strong> ${springUrl} at ${time}
<br>
JSTL URL: ${url}
<br>
<strong>Message:</strong> ${message}
</body>

</html>
