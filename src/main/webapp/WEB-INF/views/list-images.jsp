<!DOCTYPE html>
<html lang="en"
      xmlns:spring="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
    <title>AIM-Images</title>
    <jsp:include page="includes/head.jsp"/>
</head>

<body>
<c:set var="URLPath" value="${requestScope['javax.servlet.forward.servlet_path']}"/>

<jsp:include page="includes/topBar.jsp">
    <jsp:param name="page" value="images"/>
</jsp:include>
<div class="jumbotron text-center">
    <h1>AIM Images</h1>
</div>

<div class="album py-5">
    <div class="container">
        <c:if test="${not empty word.id}">
            <div class="text-center pb-3">
                <p>Keyword: <b>${word.textEn}</b> (en) / <b>${word.textDa}</b> (da)</p>
            </div>
        </c:if>
        <div class="row">
            <c:forEach items="${imageWords}" var="imageWord">
                <c:set var="image" value="${imageWord.getKey()}"/>
                <c:set var="words" value="${imageWord.getValue()}"/>
                <div class="col-md-4">
                    <a href="${pageContext.request.contextPath}/images/${image.id}">
                        <div class="card mb-4 box-shadow">
                            <img class="card-img-top"
                                 alt="broken image link, we presume."
                                 style="height: 225px; width: 100%; display: block;"
                                 src="${image_url}/${image.path}"
                                 data-holder-rendered="true">
                            <div class="card-body bg-light">
                                <p class="card-text">${image.cumulusId}</p>
                                <dl class="dl-horizontal">
                                    <dt>Keywords</dt>
                                    <c:forEach items="${words}" var="word">
                                        <dd>${word.textDa} (${word.textEn}) [${word.confidence} %]</dd>
                                    </c:forEach>
                                </dl>
                            </div>
                        </div>
                    </a>
                </div>
            </c:forEach>
        </div>
    </div>
    
    <div class="container">
        <c:if test="${hasMore eq true}">
            <a class="btn btn-success" href="${pageContext.request.contextPath}/images?offset=${nextOffset}&limit=${limit}">More images</a>
        </c:if>
    </div>
</div>

</body>
</html>
