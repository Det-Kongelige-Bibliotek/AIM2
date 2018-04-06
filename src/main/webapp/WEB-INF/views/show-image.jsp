<!DOCTYPE html>
<html lang="en" 
      xmlns:s="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

  <%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
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
      <h1>AIM Image</h1>
    </div>
    <div class="container">
        <div class="row">
            <div class="col-md-6">
                <dl class="dl-horizontal">
                    <dt>id</dt><dd>${image_details.id}</dd>
                    <dt>cumulus id</dt><dd>${image_details.cumulus_id}</dd>
                    <dt>status</dt><dd>${image_details.status}</dd>
                    <dt>Keywords</dt><dd>
                    <c:forEach items="${image_words}" var="word">
                        <p><a href="/words/${word.category}?status=${word.status}">${word.text_da} (${word.text_en})</a></p>
                    </c:forEach>
                    &nbsp;</dd>
                </dl>
            </div>
            <div class="col-md-6">
                <img src="/image_store/${image_details.path}" class="img-fluid" alt="Responsive image">
            </div>
        </div>
    </div>
  </body>
</html>
