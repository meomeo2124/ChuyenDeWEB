<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Thêm ${pageContext.request.contextPath} vào trước đường dẫn CSS/JS -->
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/styles.css">
<script src="${pageContext.request.contextPath}/js/scripts.js"></script>

<!-- Nếu có dùng favicon hoặc font, cũng làm tương tự -->
<link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/assets/favicon.ico" />