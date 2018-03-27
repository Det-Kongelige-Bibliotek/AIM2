<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="nav nav-tabs" id="categories">
<c:forEach  items="${categories}"  var="category">
    <li class="nav-item">
        <a class="${category} nav-link"  href="/words/${category}">${category}</a>
    </li>
</c:forEach >
</ul>
<div class="tab-content">
    <c:forEach items="${categories}" var="category">
        <div class="tab-pane fade container" id="${category}">
            <jsp:include page="tabs.jsp">
                <jsp:param name="category" value="${category}"/>
            </jsp:include>
        </div>
    </c:forEach>
</div>

<script type="text/javascript">
    // Rewrite bootstrap default behaviour to be able to manipulate url
    $('.nav-tabs a').click(function (e) {
        $(this).tab('show');
    });

    // Activate the correct tabs after loading the page
    $(document).ready(function () {
        var categories = "${categories}".replace('[','').replace(']','').split(", ");// converting java array to javascript array
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
                    document.getElementById(statuses[j]+"_"+categories[i]).className += (' show active');
                    document.getElementsByClassName(statuses[j])[i].className += (' active');
                }
            }
        }
    });

</script>