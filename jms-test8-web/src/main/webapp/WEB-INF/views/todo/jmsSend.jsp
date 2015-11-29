<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Todo List</title>
<style type="text/css">
.strike {
    text-decoration: line-through;
}
</style>
</head>
<body>
    <h1>Todo List</h1>
    <div id="todoForm">
        <!-- (1) -->
        <form:form
           action="${pageContext.request.contextPath}/todo/${f:h(link)}"
            method="post" modelAttribute="todoForm">
            <!-- (2) -->
            <form:input path="title" />
            <form:button>送信</form:button>
        </form:form>
    </div>
    <hr />
</body>
</html>