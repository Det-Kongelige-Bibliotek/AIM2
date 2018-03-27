<div class="second-tab container">
    <ul class="nav nav-tabs" id="words">
        <li class="nav-item">
            <a class="nav-link PENDING" href="?status=PENDING">Pending keywords</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ACCEPTED" href="?status=ACCEPTED">Approved keywords</a>
        </li>
        <li class="nav-item REJECTED">
            <a class="nav-link" href="?status=REJECTED">Rejected keywords</a>
        </li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane fade container" id="PENDING">
            <jsp:include page="pending.jsp"/>
        </div>
        <div class="tab-pane fade container" id="ACCEPTED">
            <jsp:include page="approvedForCategory.jsp"/>
        </div>
        <div class="tab-pane fade container" id="REJECTED">
            <jsp:include page="rejectedForCategory.jsp"/>
        </div>
    </div>
</div>
