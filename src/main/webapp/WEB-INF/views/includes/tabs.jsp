<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="category" value="${param.category}"/>
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
            <jsp:include page="pending.jsp"/>
        </div>
        <div class="tab-pane fade container" id="ACCEPTED_${category}">
            <jsp:include page="approvedForCategory.jsp"/>
        </div>
        <div class="tab-pane fade container" id="REJECTED_${category}">
            <jsp:include page="rejectedForCategory.jsp"/>
        </div>
    </div>
</div>
