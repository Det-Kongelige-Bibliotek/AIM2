<!DOCTYPE html>
<html lang="en" 
      xmlns:spring="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
  <title>List all images</title>
  <jsp:include page="includes/head.jsp" />
</head>

<body>
<div class="jumbotron text-center">
    <h1>AIM</h1>

<table>
<thead>Images</thead>
<tbody>
<c:forEach items="${images}" var="image">
<tr>
    <td>${image.id}</td>
    <td>${image.path}</td>
    <td>${image.text}</td>
    <td>${image.status}</td>
</tr>
</c:forEach>
</tbody>
</table
</div>
<!--
commented out this, for the time being/Sigge
div class="container">
    <jsp:include page="includes/tabs.jsp" />
</div-->
</body>
</html>
