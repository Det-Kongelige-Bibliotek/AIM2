<!DOCTYPE html>
<html lang="en" 
      xmlns:spring="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
  <title>List all images</title>
  <jsp:include page="includes/head.jsp" />
</head>

<body>
<jsp:include page="includes/topBar.jsp">
    <jsp:param name="page" value="images"/>
</jsp:include>
<div class="jumbotron text-center">
    <h1>AIM Images</h1>
</div>

<div class="album py-5 bg-light">
    <div class="container">
        <div class="row">

            <c:forEach items="${images}" var="image">
                <div class="col-md-4">
                    <a href="/images/${image.id}">
                    <div class="card mb-4 box-shadow">
                        <img class="card-img-top"
                             alt="broken image link, we presume." style="height: 225px; width: 100%; display: block;"
                             src="/image_store/${image.path}"
                             data-holder-rendered="true">
                        <div class="card-body">
                            <p class="card-text">${image.id}</p>
                            <p class="card-text">${image.path}</p>
                            <p class="card-text">${image.status}</p>
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
