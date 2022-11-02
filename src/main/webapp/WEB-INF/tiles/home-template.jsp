<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="tiles" uri="http://tiles.apache.org/tags-tiles" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib uri="http://www.springframework.org/tags"  prefix="spring"%>


<!DOCTYPE html>
<html lang="ko">
 <meta http-equiv="Cache-Control" content="no-cache, no-store, must-revalidate">
 <meta http-equiv="Expires" content="0">
 <meta http-equiv="Pragma" content="no-cache">
<head>
	<tiles:insertAttribute name="base" />
</head>
<body>	

	<tiles:insertAttribute name="head" />
		<tiles:insertAttribute name="html" />
	
	<tiles:insertAttribute name="footer" />
	
</body>

</html>
