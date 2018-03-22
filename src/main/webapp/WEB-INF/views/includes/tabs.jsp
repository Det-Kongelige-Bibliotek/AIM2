<ul class="nav nav-tabs" id="words">
    <li class="nav-item">
        <a class="nav-link active" data-toggle="tab" href="#pending">Pending keywords</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" data-toggle="tab" href="#approvedForCategory">Approved for this category</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" data-toggle="tab" href="#rejectedForCategory" role="tab">Rejected for this category</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" data-toggle="tab" href="#approvedForAll" role="tab">Approved for all</a>
    </li>
    <li class="nav-item">
        <a class="nav-link" data-toggle="tab" href="#rejectedForAll" role="tab">Rejected for all</a>
    </li>
</ul>
<div class="tab-content">
    <div class="tab-pane fade show active container" id="pending">
        <jsp:include page="pending.jsp" />
    </div>
    <div class="tab-pane fade container" id="approvedForCategory">
        <jsp:include page="approvedForCategory.jsp" />
    </div>
    <div class="tab-pane fade container" id="rejectedForCategory">
        <jsp:include page="rejectedForCategory.jsp" />
    </div>
    <div class="tab-pane fade container" id="approvedForAll">
        <jsp:include page="approvedForAll.jsp" />
    </div>
    <div class="tab-pane fade container" id="rejectedForAll">
        <jsp:include page="rejectedForAll.jsp" />
    </div>
</div>