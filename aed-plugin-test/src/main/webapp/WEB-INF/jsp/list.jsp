<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Insert title here</title>
</head>
<body>
	<h1>List heading 1</h1>
	<table>
		<thead>
			<tr>
				<th>Item</th>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="${list}" var="item">
			<tr>
				<th><c:out value="${item}" /></th>
			</tr>
			</c:forEach>
		</tbody>
	</table>
</body>
</html>