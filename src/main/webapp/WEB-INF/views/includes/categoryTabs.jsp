<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    java.util.List<dk.kb.aim.repository.WordStatus> ws = java.util.Arrays.asList(dk.kb.aim.repository.WordStatus.values());
    pageContext.setAttribute("statuses", ws);
%>
<c:set var="status" value="${controllerStatus}"/>
<ul class="nav nav-tabs" id="categories" role="tablist">
    <c:forEach items="${categories}" var="category">
        <li class="nav-item">
            <a class="${category} nav-link${currentCategory==category ? ' active' : ''}"
               href="${pageContext.request.contextPath}/words/${category}">
               ${category}
            </a>
        </li>
    </c:forEach>
</ul>
<div class="tab-content">
    <c:forEach items="${categories}" var="category">
        <div class="tab-pane fade container${currentCategory==category ? ' show active' : ''}"
             id="${category}">
            <c:if test="${currentCategory==category}">
                <jsp:include page="tabs.jsp">
                    <jsp:param name="category" value="${category}"/>
                </jsp:include>
            </c:if>
        </div>
    </c:forEach>
</div>
