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
<%@ include file="../common/include.jsp"%>
</head>
<body>
    <h1>Todo List</h1>
    <div id="todoList">
        <ul>
            <!-- (3) -->
			${f:h(todo.todoId)}<br>
			${f:h(todo.description)}<br>
			${f:h(todo.finished)}
            
        </ul>
    </div>
</body>
</html>