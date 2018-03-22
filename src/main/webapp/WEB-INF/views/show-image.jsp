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
    <div class="jumbotron text-center">
      <h1>AIM Image</h1>

      <table>
	<tr><th>id</th><td>${image_details.id}</td></tr>
	<tr><th>path</th><td>${image_details.path}</td></tr>
	<tr><th>status</th><td>${image_details.status}</td></tr>
	<tr><td colspan="2"><img src="/aim/image_store/${image_details.path}" alt="broken link, we presume."/></td></tr>
      </table>
    </div>

  </body>
</html>
