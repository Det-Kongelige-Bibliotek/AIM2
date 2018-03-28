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
</head>

<body>
<div class="jumbotron text-center">
    <h1>AIM workflow</h1>

    <p>Current state: ${workflow.getState()}</p>
    <p>Next run: ${workflow.getNextRunDate()}</p>

    <table>
        <tr>
            <td><b>Name of step</b></td>
            <td><b>State</b></td>
            <td><b>Time for last run (in millis)</b></td>
            <td><b>Results for last run</b></td>
        </tr>
        <c:forEach items="${workflow.getSteps()}" var="step">
            <tr>
                <td>${step.getName()}</td>
                <td>${step.getStatus()}</td>
                <td>${step.getTimeForLastRun()}</td>
                <td>${step.getResultOfLastRun()}</td>
            </tr>
        </c:forEach>
    </table>

</div>
<h2>Run workflow</h2>
<form action="workflow/run" method="post">
    <input type="submit" value="Execute now" />
</form>
<div>

</div>

</body>
</html>
