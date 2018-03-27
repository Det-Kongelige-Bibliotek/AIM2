<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="nav nav-tabs" id="categories">
    <c:set var = "categories"  value = "aim,mammals"/>
<c:forEach  items="${categories}"  var="category">
    <li class="nav-item">
        <a class="${category} nav-link <c:if test = "${category == 'aim'}"></c:if>"  href="/words/${category}">${category}</a>
    </li>
</c:forEach >
</ul>
<div class="tab-content">
    <c:forEach items="aim,mammals"  var="category">
    <div class="tab-pane fade container" id="${category}">
        <%--<jsp:include page="${category}.jsp" />--%>
        <jsp:include page="tabs.jsp" />
    </div>
    </c:forEach >
</div>

<script type="text/javascript">
    // Rewrite bootstrap default behaviour to be able to manipulate url
    $('.nav-tabs a').click(function (e) {
        $(this).tab('show');
    });

    // Set the correct category tab after loading the page
    $(document).ready(function () {
        <%--console.log("${categories}");--%>
        var categories = "${categories}".split(",");
        for (var i = 0; i < categories.length; i++) {
            if (window.location.href.indexOf(categories[i]) > -1)
            {
                document.getElementById(categories[i]).className += (' show active');
                document.getElementsByClassName(categories[i])[0].className += (' active');
            }

            var statuses = ['PENDING', 'ACCEPTED', 'REJECTED'];
            for (var j = 0; j < statuses.length; j++) {
                if (window.location.href.indexOf(statuses[j]) > -1)
                {
                    document.getElementById(statuses[j]).className += (' show active');
                    document.getElementsByClassName(statuses[j])[i].className += (' active');
                }
            }

        }


    });

</script>