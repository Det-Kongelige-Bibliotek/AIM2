<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<c:set var="category" value="${param.category}"/>
<c:set var="status" value="${controller_status}"/>
<c:set var="currentCategoryPath" value = "${requestScope['javax.servlet.forward.servlet_path']}" />
<p> variable status: ${status} and status from controller: ${controller_status} and from param ${param.status} </p>
<div class="second-tab container">
    <ul class="nav nav-tabs" id="words">
        <li class="nav-item">
            <a class="nav-link PENDING" href="?status=PENDING">Pending keywords</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ACCEPTED" href="?status=ACCEPTED">Approved keywords</a>
        </li>
        <li class="nav-item">
            <a class="nav-link REJECTED" href="?status=REJECTED">Rejected keywords</a>
        </li>
    </ul>

    <div class="tab-content">

        <div class="tab-pane fade container" id="PENDING_${category}">
            <c:if test="${controller_status == 'PENDING'}">
                <jsp:include page="wordsTable.jsp">
                    <jsp:param name="status" value="${status}"/>
                </jsp:include>
            </c:if>
        </div>
        <div class="tab-pane fade container" id="ACCEPTED_${category}">
            <c:if test="${controller_status == 'ACCEPTED'}">
                <jsp:include page="wordsTable.jsp">
                    <jsp:param name="status" value="${status}"/>
                </jsp:include>
            </c:if>
        </div>
        <div class="tab-pane fade container" id="REJECTED_${category}">
            <c:if test="${controller_status == 'REJECTED'}">
                <jsp:include page="wordsTable.jsp">
                    <jsp:param name="status" value="${status}"/>
                </jsp:include>
            </c:if>
        </div>
    </div>
</div>
<script>console.log('${currentCategoryPath}');</script>