<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<table class="table table-striped">
    <thead>
    <tr>
        <th>id</th>
        <th>English</th>
        <th>Danish</th>
        <th colspan="2"></th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${words}" var="word">
        <c:if test = "${word.status == 'REJECTED'}">
        <tr>
            <td>${word.id}</td>
            <td>${word.text_en}</td>
            <td>${word.text_da}</td>
            <td><button type="button" class="btn btn-success">Approve</button></td>
            <td><button type="button" class="btn btn-success">Approve for all</button></td>
        </tr>
        </c:if>
    </c:forEach>
    </tbody>
</table>