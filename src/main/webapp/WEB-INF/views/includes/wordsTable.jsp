<table class="table table-striped"
       xmlns:spring="http://www.springframework.org/tags"
       xmlns:jsp="http://java.sun.com/JSP/Page"
       xmlns:c="http://java.sun.com/jsp/jstl/core"
       xmlns="http://www.w3.org/1999/xhtml"
       id="table_${param.category}_${param.status}">
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
    <c:set var="status" value="${controller_status}"/>
    <%
        java.util.List<dk.kb.aim.repository.WordStatus> ws = java.util.Arrays.asList(dk.kb.aim.repository.WordStatus.values());
        pageContext.setAttribute("statuses", ws);
        String host = request.getHeader("HOST");
    %>

    <thead>
    <tr>
        <th onclick="sortTable(0, table_${param.category}_${param.status})">id</th>
        <th onclick="sortTable(1, table_${param.category}_${param.status})">English</th>
        <th onclick="sortTable(2, table_${param.category}_${param.status})">Danish</th>
        <c:if test="${status=='REJECTED'||status=='PENDING'}">
            <th colspan="2">Approve</th>
        </c:if>
        <c:if test="${status=='ACCEPTED'||status=='PENDING'}">
            <th colspan="2">Reject</th>
        </c:if>
        <c:if test="${status=='PENDING'}">
            <th onclick="sortTable(5, table_${param.category}_${param.status})">Count</th>
        </c:if>
        <c:if test="${status!='PENDING'}">
            <th onclick="sortTable(4, table_${param.category}_${param.status})">Count</th>
        </c:if>
        <th>Images</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${words}" var="word">
        <tr>
            <form action="${pageContext.request.contextPath}/words/update" id="word_form_id_${word.id}">
                <td>${word.id}<input type="hidden" name="id" value="${word.id}"/></td>
                <td>${word.textEn}<input type="hidden" name="text_en" value="${word.textEn}"/></td>
                <td><input type="text" name="text_da" value="${word.textDa}"/></td>
                <input type="hidden" name="back_to"
                       value="/words/${word.category}?status=${word.status}"/>
                <c:if test="${status=='REJECTED'||status=='PENDING'}">
                    <td>
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" value="ACCEPTED:${word.category}" id="accept_${word.id}">
                            <label class="form-check-label" for="${word.category}_accept_${word.id}">
                                Approve
                            </label>
                        </div>
                    </td>
                    <td>
                        <button type="submit" name="op_category" value="ACCEPTED:AIM" class="btn btn-success">Approve
                            for AIM
                        </button>
                    </td>
                </c:if>
                <c:if test="${status=='ACCEPTED'||status=='PENDING'}">
                    <td>
                        <button type="submit" name="op_category" value="REJECTED:${word.category}"
                                class="btn btn-danger">Reject
                        </button>
                    </td>
                    <td>
                        <button type="submit" name="op_category" value="REJECTED:AIM" class="btn btn-danger">Reject for
                            AIM
                        </button>
                    </td>
                </c:if>
            </form>
            <td>${word.count}</td>
            <td>
                <c:url value="/word_images/${word.id}" var="imgUrl">
                    <c:param name="offset" value="0" />
                    <c:param name="limit" value="12" />
                </c:url>
                <a class="btn btn-info" href="${imgUrl}" role="button">See images</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
<script type="text/javascript">
    var categories = "${categories}".replace('[', '').replace(']', '').split(", ");
    var statuses = "${statuses}".replace('[', '').replace(']', '').split(", ");
</script>

<script>
/* Taken from example from w3schools.*/
function sortTable(n, table_id) {
  var table, rows, switching, i, x, y, shouldSwitch, dir, switchcount = 0;
  table = document.getElementById(table_id);
  table = table_id;
  switching = true;
  // Set the sorting direction to ascending:
  dir = "asc";
  /* Make a loop that will continue until
  no switching has been done: */
  while (switching) {
    // Start by saying: no switching is done:
    switching = false;
    rows = table.rows;
    /* Loop through all table rows (except the
    first, which contains table headers): */
    for (i = 1; i < (rows.length - 1); i++) {
      // Start by saying there should be no switching:
      shouldSwitch = false;
      /* Get the two elements you want to compare,
      one from current row and one from the next: */
      x = rows[i].getElementsByTagName("TD")[n];
      y = rows[i + 1].getElementsByTagName("TD")[n];

      /* Get the value in number form or lowercase if nan.*/
      x_value = x.innerHTML.valueOf();
      y_value = y.innerHTML.valueOf();
      if(x_value.replace(/<.*/i, "") && !isNaN(x_value.replace(/<.*/i, ""))) {
        x_value = Number(x_value.replace(/<.*/i, ""));
        y_value = Number(y_value.replace(/<.*/i, ""));
      }

      /* Check if the two rows should switch place,
      based on the direction, asc or desc: */
      if (dir == "asc") {
        if (x_value > y_value) {
          // If so, mark as a switch and break the loop:
          shouldSwitch = true;
          break;
        }
      } else if (dir == "desc") {
        if (x_value < y_value) {
          // If so, mark as a switch and break the loop:
          shouldSwitch = true;
          break;
        }
      }
    }
    if (shouldSwitch) {
      /* If a switch has been marked, make the switch
      and mark that a switch has been done: */
      rows[i].parentNode.insertBefore(rows[i + 1], rows[i]);
      switching = true;
      // Each time a switch is done, increase this count by 1:
      switchcount ++;
    } else {
      /* If no switching has been done AND the direction is "asc",
      set the direction to "desc" and run the while loop again. */
      if (switchcount == 0 && dir == "asc") {
        dir = "desc";
        switching = true;
      }
    }
  }
}
</script>
