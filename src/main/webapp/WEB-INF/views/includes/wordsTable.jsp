<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="status" value="${param.status}"/>
<%
    java.util.List<dk.kb.aim.WordStatus> ws = java.util.Arrays.asList(dk.kb.aim.WordStatus.values());
    pageContext.setAttribute("statuses", ws);
%>
<table class="table table-striped">
    <thead>
    <tr>
        <th>id</th>
        <th>English</th>
        <th>Danish</th>
        <c:if test="${status=='REJECTED'||status=='PENDING'}">
            <th colspan="2">Approve</th>
        </c:if>

        <c:if test="${status=='ACCEPTED'||status=='PENDING'}">
            <th colspan="2">Reject</th>
        </c:if>
        <th>Images</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${words}" var="word">
        <tr>
      <form action="/update" id="word_form_id_${word.id}">
            <td>${word.id}<input type="hidden" name="id" value="${word.id}"/></td>
            <td>${word.text_en}<input type="hidden" name="text_en" value="${word.text_en}"/></td>
            <td><input type="text" name="text_da" value="${word.text_da}"/></td>
	    <input type="hidden" name="back_to" value="/words/${word.category}?status=${word.status}"/>
            <c:if test="${status=='REJECTED'||status=='PENDING'}">
	      <input type="hidden" name="status" value="ACCEPTED"/>
                <td>
                    <button type="submit" name="category" value="${word.category}" class="btn btn-success">Approve</button>
                </td>
                <td>
                    <button type="submit" name="category" value="aim" class="btn btn-success">Approve for AIM</button>
                </td>
            </c:if>
            <c:if test="${status=='ACCEPTED'||status=='PENDING'}">
	      <input type="hidden" name="status" value="REJECTED"/>
                <td>
		  <button type="submit" name="category" value="${word.category}" class="btn btn-success">Reject</button>
                </td>
                <td>
                    <button type="submit" name="category" value="aim" class="btn btn-success">Reject for AIM</button>
                </td>
            </c:if>



	  </form>
            <td>
                <a class="btn btn-info" href="/images" role="button">See images</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script type="text/javascript">
    var categories = "${categories}".replace('[', '').replace(']', '').split(", ");
    var statuses = "${statuses}".replace('[', '').replace(']', '').split(", ");
</script>

