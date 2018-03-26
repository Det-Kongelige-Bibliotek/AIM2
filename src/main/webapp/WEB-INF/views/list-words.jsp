<!DOCTYPE html>
<html lang="en" 
      xmlns:spring="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
  <title>List of all words</title>
  <jsp:include page="includes/head.jsp" />
</head>

<body>
<div class="jumbotron text-center">
    <h1>AIM Words</h1>
</div>
<div class="container">
    <jsp:include page="includes/categoryTabs.jsp" />
</div>


<!-- table>
<tbody>
<c:forEach items="${words}" var="word">
<tr>
    <td>${word.id}</td>
    <td>${word.text_en}</td>
    <td>${word.text_da}</td>
    <td>${word.status}</td>
</tr>
</c:forEach>
</table>
</div>-->


</body>
</html>
