<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="page" value="${param.page}"/>

<nav class="navbar navbar-expand-lg navbar-light bg-light">
    <div class="container">

        <a class="navbar-brand" href="#"></a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent"
                aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav ml-auto">
                <li class="nav-item <c:if test="${page=='words'}">active</c:if>">
                    <a class="nav-link" href="/words">Words</a>
                </li>
                <li class="nav-item <c:if test="${page=='images'}">active</c:if>">
                    <a class="nav-link" href="/images">Images</a>
                </li>
                <li class="nav-item <c:if test="${page=='workflow'}">active</c:if>">
                    <a class="nav-link" href="/workflow">Workflow</a>
                </li>
            </ul>
        </div>
    </div>
</nav>
