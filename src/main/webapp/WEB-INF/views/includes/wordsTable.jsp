<table class="table table-striped"
       xmlns:spring="http://www.springframework.org/tags"
       xmlns:jsp="http://java.sun.com/JSP/Page"
       xmlns:c="http://java.sun.com/jsp/jstl/core"
       xmlns="http://www.w3.org/1999/xhtml">
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <c:set var="status" value="${controller_status}"/>
    <%
        java.util.List<dk.kb.aim.repository.WordStatus> ws = java.util.Arrays.asList(dk.kb.aim.repository.WordStatus.values());
        pageContext.setAttribute("statuses", ws);
        String host = request.getHeader("HOST");
    %>

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
            <form action="${pageContext.request.contextPath}/words/update" id="word_form_id_${word.id}">
                <td>${word.id}<input type="hidden" name="id" value="${word.id}"/></td>
                <td>${word.textEn}<input type="hidden" name="text_en" value="${word.textEn}"/></td>
                <td><input type="text" name="text_da" value="${word.textDa}"/></td>
                <input type="hidden" name="back_to"
                       value="/words/${word.category}?status=${word.status}"/>
                <c:if test="${status=='REJECTED'||status=='PENDING'}">
                    <td>
                        <button type="submit" name="op_category" value="ACCEPTED:${word.category}"
                                class="btn btn-success">Approve
                        </button>
                    </td>
                    <td>
                        <button type="submit" name="op_category" value="ACCEPTED:AIM" class="btn btn-success">Approve
                            for AIM
                        </button>
                    </td>
                </c:if>
                <c:if test="${status=='ACCEPTED'||status=='PENDING'}">
                    <td>
                        <button type="submit" name="op_category" value="REJECTED:${word.category}"
                                class="btn btn-danger">Reject
                        </button>
                    </td>
                    <td>
                        <button type="submit" name="op_category" value="REJECTED:AIM" class="btn btn-danger">Reject for
                            AIM
                        </button>
                    </td>
                </c:if>
            </form>
            <td>
                <c:url value = "/word_images/${word.id}" var = "imgUrl">
                    <c:param name = "limit" value = "10"/>
                </c:url>
                <a class="btn btn-info" href="${imgUrl}" role="button">See images</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script type="text/javascript">
    var categories = "${categories}".replace('[', '').replace(']', '').split(", ");
    var statuses = "${statuses}".replace('[', '').replace(']', '').split(", ");
</script>

