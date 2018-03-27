<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="category" value="${param.category}"/>
<c:set var="status" value="${param.status}"/>

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
            <jsp:include page="wordsTable.jsp">
                <jsp:param name="status" value="${status}"/>
            </jsp:include>
        </div>
        <div class="tab-pane fade container" id="ACCEPTED_${category}">
            <jsp:include page="wordsTable.jsp">
                <jsp:param name="status" value="${status}"/>
            </jsp:include>
        </div>
        <div class="tab-pane fade container" id="REJECTED_${category}">
            <jsp:include page="wordsTable.jsp">
                <jsp:param name="status" value="${status}"/>
            </jsp:include>
        </div>
    </div>
</div>
