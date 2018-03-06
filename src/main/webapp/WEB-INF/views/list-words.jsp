<!DOCTYPE html>

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html lang="en">

<body>

<thead>Keywords</thead>
<tbody>
<c:forEach items="${words}" var="word">
<tr>
    <td>${word.id}</td>
    <td>${word.text_en}</td>"
    <td>${word.text_da}</td>"
    <td>${word.status}</td>"
</tr>
</c:forEach>
</tbody>
</body>
</html>