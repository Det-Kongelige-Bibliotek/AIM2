<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="category" value="${param.category}"/>
<c:set var="status" value="${controllerStatus}"/>
<c:set var="currentCategoryPath" value="${requestScope['javax.servlet.forward.servlet_path']}"/>

<div class="second-tab container">
    <ul class="nav nav-tabs" id="words">
        <li class="nav-item">
            <a class="nav-link PENDING${status=='PENDING' ? ' active' : ''}" href="?status=PENDING">
                Pending keywords
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link ACCEPTED${status=='ACCEPTED' ? ' active' : ''}" href="?status=ACCEPTED">
                Approved keywords
            </a>
        </li>
        <li class="nav-item">
            <a class="nav-link REJECTED${status=='REJECTED' ? ' active' : ''}" href="?status=REJECTED">
                Rejected keywords
            </a>
        </li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane fade container${status=='PENDING' ? ' show active' : ''}" id="PENDING_${category}">
        </div>
        <div class="tab-pane fade container${status=='ACCEPTED' ? ' show active' : ''}" id="ACCEPTED_${category}">
        </div>
        <div class="tab-pane fade container${status=='REJECTED' ? ' show active' : ''}" id="REJECTED_${category}">
        </div>

        <jsp:include page="wordsTable.jsp">
            <jsp:param name="status" value="${status}"/>
            <jsp:param name="category" value="${category}"/>
        </jsp:include>
    </div>
</div>
