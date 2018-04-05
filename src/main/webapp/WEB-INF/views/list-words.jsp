<!DOCTYPE html>
<html lang="en" 
      xmlns:spring="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
  <title>AIM: List of all words</title>
  <jsp:include page="includes/head.jsp" />
</head>

<body>

<jsp:include page="includes/topBar.jsp">
    <jsp:param name="page" value="words"/>
</jsp:include>

<div class="jumbotron text-center">
    <h1>AIM Words</h1>
</div>

<div class="container">
    <jsp:include page="includes/categoryTabs.jsp" />
</div>

</body>
</html>
