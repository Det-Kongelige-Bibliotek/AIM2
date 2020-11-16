<!DOCTYPE html>
<html lang="en" 
      xmlns:spring="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
  <title>AIM-Text</title>
  <jsp:include page="includes/head.jsp" />
</head>

<body>

<jsp:include page="includes/topBar.jsp">
    <jsp:param name="page" value="text"/>
</jsp:include>

<div class="jumbotron text-center">
    <h1>AIM Text</h1>
</div>


<div class="container">
    <ul class="nav nav-tabs" id="downloadCsv">
        <br/>
        <c:forEach items="${categories}" var="category">
            <li class="nav-item">
                <a class="${category} nav-link" href="${pageContext.request.contextPath}/text/${category}">${category}</a>
            </li>
        </c:forEach>
    </ul>
    <c:if test="${images != null}">
        <div class="container btn">
            <c:url var="downloadUrl" value="/text/download">
                <c:param name="category" value="${currentCategory}" />
            </c:url>
            <a class="btn btn-info" href="${downloadUrl}" role="button">Download as CSV</a>
        </div>

        <table class="table table-striped"
               xmlns:spring="http://www.springframework.org/tags"
               xmlns:jsp="http://java.sun.com/JSP/Page"
               xmlns:c="http://java.sun.com/jsp/jstl/core"
               xmlns="http://www.w3.org/1999/xhtml">
            <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

            <thead>
            <tr>
                <th>Image</th>
                <th>Image name</th>
                <th>Forside</th>
                <th>Text</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach items="${images}" var="image">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/images/${image.id}">${image.id}</a></td>
                    <td>${image.cumulusId}</td>
                    <td>${image.isFront}</td>
                    <td>${image.ocr}</td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>
</div>


</body>
</html>
