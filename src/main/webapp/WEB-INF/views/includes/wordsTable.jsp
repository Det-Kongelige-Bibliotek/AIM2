<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="status" value="${param.status}"/>

<table class="table table-striped">
    <thead>
    <tr>
        <th>id</th>
        <th>English</th>
        <th>Danish</th>
        <th colspan="2"></th>
        <c:if test="${status=='PENDING'}">
            <th colspan="2"></th>
        </c:if>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${words}" var="word">
        <tr>
            <td>${word.id}</td>
            <td>${word.text_en}</td>
            <td>${word.text_da}</td>
            <c:if test="${status=='REJECTED'||status=='PENDING'}">
                <td>
                    <button type="button" class="btn btn-success">Approve</button>
                </td>
                <td>
                    <button type="button" class="btn btn-success">Approve for all</button>
                </td>
            </c:if>
            <c:if test="${status=='ACCEPTED'||status=='PENDING'}">
                <td>
                    <button type="button" class="btn btn-danger">Reject</button>
                </td>
                <td>
                    <button type="button" class="btn btn-danger">Reject for all</button>
                </td>
            </c:if>
        </tr>
    </c:forEach>
    </tbody>
</table>