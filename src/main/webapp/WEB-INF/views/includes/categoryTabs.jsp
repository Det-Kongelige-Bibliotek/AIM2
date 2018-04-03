<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    java.util.List<dk.kb.cumulus.WordStatus> ws =java.util.Arrays.asList(dk.kb.cumulus.WordStatus.values());
    pageContext.setAttribute("statuses", ws);
%>

<%--<form action="" method="get">--%>
    <%--<select name="status" onchange="this.form.submit()">--%>
        <%--<c:forEach items="${statuses}" var="status">--%>
            <%--<c:choose>--%>
                <%--<c:when test="${status == param.status}">--%>
                    <%--<option selected="selected">${status}</option>--%>
                <%--</c:when>--%>
                <%--<c:otherwise>--%>
                    <%--<option>${status}</option>--%>
                <%--</c:otherwise>--%>
            <%--</c:choose>--%>
        <%--</c:forEach>--%>
    <%--</select>--%>
<%--</form>--%>

<ul class="nav nav-tabs" id="categories">
<c:forEach  items="${categories}"  var="category">
    <li class="nav-item">
        <a class="${category} nav-link"  href="/words/${category}">${category}</a>
    </li>
</c:forEach >
</ul>
<div class="tab-content">
    <c:forEach items="${categories}" var="category">
        <div class="tab-pane fade container" id="${category}">
            <jsp:include page="tabs.jsp">
                <jsp:param name="category" value="${category}"/>
            </jsp:include>
        </div>
    </c:forEach>
</div>

<script type="text/javascript">
    var categories = "${categories}".replace('[','').replace(']','').split(", ");
</script>
