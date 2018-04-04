<!DOCTYPE html>
<html lang="en"
      xmlns:s="http://www.springframework.org/tags"
      xmlns:jsp="http://java.sun.com/JSP/Page"
      xmlns:c="http://java.sun.com/jsp/jstl/core"
      xmlns="http://www.w3.org/1999/xhtml">

<%@ taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<head>
    <title>AIM Workflow</title>
    <jsp:include page="includes/head.jsp"/>
</head>

<body>
<jsp:include page="includes/topBar.jsp">
    <jsp:param name="page" value="workflow"/>
</jsp:include>
<div class="jumbotron text-center">
    <h1>AIM workflow</h1>
</div>
<div id="main" class="container">
    <p><b>Current state:</b> ${workflow.getState()}</p>
    <p><b>Next run:</b> ${workflow.getNextRunDate()}</p>

    <table class="table table-striped">
        <thead>
        <tr>
            <th>Name of step</th>
            <th>State</th>
            <th>Time for last run (in millis)</th>
            <th>Results for last run</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach items="${workflow.getSteps()}" var="step">
            <tr>
                <td>${step.getName()}</td>
                <td>${step.getStatus()}</td>
                <td>${step.getTimeForLastRun()}</td>
                <td>${step.getResultOfLastRun()}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>


    <h2></h2>
    <form action="workflow/run" method="post">
        <button type="submit" class="btn btn-success" id="runWorkflow">Run now</button>
    </form>
    <div>
    </div>
</div>
</body>
</html>
