<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Employees</title>
</head>
<body>
	<form method="post">
		Group name:
		<input name="name" type="text" value="" />
		<input type="submit" value="Add" />
	</form>
	<hr />
	<table border="1">
		<thead>
			<tr>
				<th>id</th>
				<th>name</th>
				<th>current employer</th>
			</tr>
		</thead>
		<tbody>
<c:forEach var="employee" items="${employees}">
			<tr>
				<td><c:out value="${employee.id}" /></td>
				<td><c:out value="${employee.name}" /></td>
				<td><c:out value="${employee.currentEmployer}" /></td>
			</tr>
</c:forEach>			
		</tbody>
	</table>
</body>
</html>