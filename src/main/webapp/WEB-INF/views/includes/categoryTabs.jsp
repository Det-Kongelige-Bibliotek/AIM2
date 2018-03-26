<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<ul class="nav nav-tabs" id="categories">
    <c:set var = "categories"  value = "aim, mammals"/>
<c:forEach  items="${categories}"  var="category">
    <li class="nav-item">
        <a class="nav-link <c:if test = "${category == 'aim'}">active</c:if>" data-toggle="tab" href="#${category}">${category}</a>
    </li>
</c:forEach >
</ul>
<div class="tab-content">
    <c:forEach items="aim,mammals"  var="category">
    <div class="tab-pane fade <c:if test = "${category == 'aim'}">show active</c:if> container" id="${category}">
        <%--<jsp:include page="${category}.jsp" />--%>
        <jsp:include page="tabs.jsp" />
    </div>
    </c:forEach >
</div>