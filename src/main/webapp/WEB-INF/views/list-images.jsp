<!DOCTYPE html>
<html lang="en"
      xmlns:spring="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
    <title>List all images for word</title>
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
                <p>Keyword: <b>${word.text_en}</b> (en) / <b>${word.text_da}</b> (da)</p>
            </div>
        </c:if>
        <div class="row">
            <c:forEach items="${images}" var="image">
                <div class="col-md-4">
                    <a href="/images/${image.id}">
                        <div class="card mb-4 box-shadow">
                            <img class="card-img-top"
                                 alt="broken image link, we presume."
                                 style="height: 225px; width: 100%; display: block;"
                                 src="/image_store/${image.path}"
                                 data-holder-rendered="true">
                            <div class="card-body bg-light">
                                <p class="card-text">${image.path}</p>
                                <dl class="dl-horizontal">
                                    <dt>Keywords</dt>
                                    <dd>cat</dd>
                                    <dd>mammal</dd>
                                </dl>
                            </div>
                        </div>
                    </a>
                </div>
            </c:forEach>

        </div>
    </div>
</div>

</body>
</html>
