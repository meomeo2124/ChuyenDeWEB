<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<%-- Thay vì dùng đường dẫn tương đối dễ bị lỗi khi đổi URL, hãy dùng contextPath --%>
	<link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet" />
	<title>Reset Password</title>
</head>
<body>
<%@ include file="/template/includes/navbar.jsp"%>

<div class="container">
	<%-- Nhúng trực tiếp file EmailInput.jsp từ cùng thư mục view --%>
	<jsp:include page="EmailInput.jsp"></jsp:include>
</div>

<%@ include file="/template/includes/footer.jsp"%>
</body>
</html>