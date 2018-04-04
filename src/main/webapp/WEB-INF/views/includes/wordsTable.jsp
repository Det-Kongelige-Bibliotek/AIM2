<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="status" value="${param.status}"/>
<%
    java.util.List<dk.kb.aim.WordStatus> ws =java.util.Arrays.asList(dk.kb.aim.WordStatus.values());
    pageContext.setAttribute("statuses", ws);
%>

<table class="table table-striped">
    <thead>
    <tr>
        <th>id</th>
        <th>English</th>
        <th>Danish</th>
        <c:if test="${status=='REJECTED'||status=='PENDING'}">
            <th colspan="2">Approve</th>
        </c:if>

        <c:if test="${status=='ACCEPTED'||status=='PENDING'}">
            <th colspan="2">Reject</th>
        </c:if>
        <th>Images</th>
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
            <td>
                <a class="btn btn-info" href="/images" role="button">See images</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script type="text/javascript">
    var categories = "${categories}".replace('[','').replace(']','').split(", ");
    var statuses = "${statuses}".replace('[','').replace(']','').split(", ");
</script>
