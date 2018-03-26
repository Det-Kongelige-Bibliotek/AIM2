<div class="second-tab container">
    <ul class="nav nav-tabs" id="words">
        <li class="nav-item">
            <a class="nav-link active" data-toggle="tab" href="#pending">Pending keywords</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-toggle="tab" href="#approvedForCategory">Approved keywords</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" data-toggle="tab" href="#rejectedForCategory" role="tab">Rejected keywords</a>
        </li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane fade show active container" id="pending">
            <jsp:include page="pending.jsp"/>
        </div>
        <div class="tab-pane fade container" id="approvedForCategory">
            <jsp:include page="approvedForCategory.jsp"/>
        </div>
        <div class="tab-pane fade container" id="rejectedForCategory">
            <jsp:include page="rejectedForCategory.jsp"/>
        </div>
    </div>
</div>