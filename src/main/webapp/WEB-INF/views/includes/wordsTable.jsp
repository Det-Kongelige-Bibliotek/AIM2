<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<table class="table table-striped table-sm word-table"
       xmlns:spring="http://www.springframework.org/tags"
       xmlns:jsp="http://java.sun.com/JSP/Page"
       xmlns:c="http://java.sun.com/jsp/jstl/core"
       xmlns="http://www.w3.org/1999/xhtml"
       id="table_${param.category}_${param.status}">
    <c:set var="status" value="${controllerStatus}"/>
    <%
        java.util.List<dk.kb.aim.repository.WordStatus> ws = java.util.Arrays.asList(dk.kb.aim.repository.WordStatus.values());
        pageContext.setAttribute("statuses", ws);
        String host = request.getHeader("HOST");
    %>

    <thead>
    <tr>
        <th>
            <c:url value="/words/${category}" var="idSortUrl">
                <c:param name="status" value="${status}" />
                <c:param name="orderBy" value="id" />
                <c:param name="ascending" value="${!ascending || orderBy != 'id'}" />
            </c:url>
            <a href="${idSortUrl}">Id
                <span style="float:center">
                    <i class="fa ${orderBy!='id' ? 'fa-sort' : ascending ? 'fa-sort-desc' : 'fa-sort-asc'}"> </i>
                </span>
            </a>
        </th>
        <th>
            <c:url value="/words/${category}" var="textEnSortUrl">
                <c:param name="status" value="${status}" />
                <c:param name="orderBy" value="text_en" />
                <c:param name="ascending" value="${!ascending || orderBy != 'text_en'}" />
            </c:url>
            <a href="${textEnSortUrl}">English
                <span style="float:center">
                    <i class="fa ${orderBy!='text_en' ? 'fa-sort' : ascending ? 'fa-sort-desc' : 'fa-sort-asc'}"> </i>
                </span>
            </a>
        </th>
        <th>
            <c:url value="/words/${category}" var="textDaSortUrl">
                <c:param name="status" value="${status}" />
                <c:param name="orderBy" value="text_da" />
                <c:param name="ascending" value="${!ascending || orderBy != 'text_da'}" />
            </c:url>
            <a href="${textDaSortUrl}">Danish
                <span style="float:center">
                    <i class="fa ${orderBy!='text_da' ? 'fa-sort' : ascending ? 'fa-sort-desc' : 'fa-sort-asc'}"> </i>
                </span>
            </a>
        </th>
        <c:if test="${status=='ACCEPTED'||status=='REJECTED'}">
            <th colspan="2">Pending</th>
        </c:if>
        <c:if test="${status=='REJECTED'||status=='PENDING'}">
            <th colspan="2">Approve</th>
        </c:if>
        <c:if test="${status=='ACCEPTED'||status=='PENDING'}">
            <th colspan="2">Reject</th>
        </c:if>
        <th>
            <c:url value="/words/${category}" var="countSortUrl">
                <c:param name="status" value="${status}" />
                <c:param name="orderBy" value="count" />
                <c:param name="ascending" value="${!ascending || orderBy != 'count'}" />
            </c:url>
            <a href="${countSortUrl}">Count
                <span style="float:center">
                    <i class="fa ${orderBy!='count' ? 'fa-sort' : ascending ? 'fa-sort-desc' : 'fa-sort-asc'}"> </i>
                </span>
            </a>
        </th>
        <th>Images</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${words}" var="word">
        <tr>
            <form name="word_form" action="${pageContext.request.contextPath}/update/word" id="word_form_id_${word.id}">
                <td>
                    <span class="spinner-border" role="status" style="display:none">
                        <span class="sr-only">Loading...</span>
                    </span>
                    ${word.id}
                    <input type="hidden" name="id" value="${word.id}"/>
                    <input type="hidden" name="op_category" value=""/>
                </td>
                <td>${word.textEn}<input type="hidden" name="text_en" value="${word.textEn}"/></td>
                <td><input type="text" name="text_da" value="${word.textDa}"/></td>
                <c:if test="${status=='ACCEPTED'||status=='REJECTED'}">
                    <td>
                        <button type="submit"
                                onclick="this.form.op_category.value=this.value"
                                value="PENDING:${word.category}"
                                class="btn btn-warning">
                                Pending
                        </button>
                    </td>
                    <td>
                        <c:if test="${fn:toLowerCase(currentCategory)!='aim'}">
                        <button type="submit"
                                onclick="this.form.op_category.value=this.value"
                                value="PENDING:AIM" class="btn btn-warning">
                                Pending for AIM
                        </button>
                        </c:if>
                    </td>
                </c:if>
                <c:if test="${status=='REJECTED'||status=='PENDING'}">
                    <td>
                        <button type="submit"
                                onclick="this.form.op_category.value=this.value"
                                value="ACCEPTED:${word.category}"
                                class="btn btn-success">
                                Approve
                        </button>
                    </td>
                    <td>
                        <c:if test="${fn:toLowerCase(currentCategory)!='aim'}">
                        <button type="submit"
                                onclick="this.form.op_category.value=this.value"
                                value="ACCEPTED:AIM" class="btn btn-success">
                                Approve for AIM
                        </button>
                        </c:if>
                    </td>
                </c:if>
                <c:if test="${status=='ACCEPTED'||status=='PENDING'}">
                    <td>
                        <button type="submit"
                                onclick="this.form.op_category.value=this.value"
                                value="REJECTED:${word.category}"
                                class="btn btn-danger">
                                Reject
                        </button>
                    </td>
                    <td>
                        <c:if test="${fn:toLowerCase(currentCategory)!='aim'}">
                        <button type="submit"
                                onclick="this.form.op_category.value=this.value"
                                value="REJECTED:AIM"
                                class="btn btn-danger">
                                Reject for AIM
                        </button>
                        </c:if>
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

var frm = $('form[name=word_form]');
var clickedButton = null;

frm.submit(function (e) {
    e.preventDefault();
    var self = $(this);

    console.log($(this).closest('span'));
    $(this).closest('span').show();

    $.ajax({
        type: frm.attr('method'),
        url: frm.attr('action'),
        data: $(this).serialize(),
        success: function (data) {
            var here = self;
            self.closest('tr').fadeOut('fast',
                function(here) {
                    $(here).remove();
                }
            );
            console.log('Submission was successful.');
            //console.log(data)
        },
        error: function (data) {
            spinner.find('span.spinner-border').hide();
            console.log('An error occurred.');
            //console.log(data);
        },
    });
});
</script>
