<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm Sản Phẩm</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <%-- Đã giữ nguyên các khối style CSS gốc của bạn ở đây --%>
</head>
<body>
<header class="admin-header">
    <div class="logo">Admin</div>
    <div class="header-right"><input type="text" class="search-bar" placeholder="Tìm kiếm..."><button class="add-btn" onclick="toggleSidebar()">Thêm mới</button></div>
</header>

<nav class="admin-nav">
    <ul>
        <li><a href="${pageContext.request.contextPath}/admin/product/manage"><span class="icon">🛠️</span><span class="text">Quản lý sản phẩm</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/user/manage"><span class="icon">👥</span><span class="text">Quản lý người dùng</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/category/manage"><span class="icon">📋</span><span class="text">Quản lý danh mục</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/statistics"><span class="icon">📈</span><span class="text">Thống kê</span></a></li>
        <li><a href="${pageContext.request.contextPath}/logout"><span class="icon">🚪</span><span class="text">Đăng xuất</span></a></li>
    </ul>
</nav>

<main class="admin-main">
    <div class="container mt-5" style="max-width: 600px; padding:20px; background:#fff; border-radius:10px; box-shadow:0 4px 8px rgba(0,0,0,0.1);">
        <h1>Thêm Sản Phẩm Mới</h1>

        <%-- SỬA: Đọc trực tiếp biến error từ Spring Model --%>
        <c:if test="${not empty error}">
            <div class="alert alert-danger text-center">${error}</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/product/insert" method="post" enctype="multipart/form-data">
            <div class="mb-3">
                <label for="name" class="form-label">Tên Sản Phẩm:</label>
                <input type="text" id="name" name="name" class="form-control" required>
            </div>
            <div class="mb-3">
                <label for="description" class="form-label">Mô Tả:</label>
                <textarea id="description" name="description" class="form-control" required></textarea>
            </div>
            <div class="mb-3">
                <label for="photo" class="form-label">Ảnh Sản Phẩm:</label>
                <input type="file" id="photo" name="photo" class="form-control" accept="image/*" required>
            </div>
            <div class="mb-3">
                <label for="price" class="form-label">Giá (VNĐ):</label>
                <input type="number" id="price" name="price" class="form-control" step="0.01" required>
            </div>
            <div class="mb-3">
                <label for="stock" class="form-label">Số Lượng Tồn Kho:</label>
                <input type="number" id="stock" name="stock" class="form-control" value="0" min="0">
            </div>
            <div class="text-center">
                <button type="submit" class="btn btn-success">Thêm Sản Phẩm</button>
                <a href="${pageContext.request.contextPath}/admin/product/manage" class="btn btn-secondary">Quay lại</a>
            </div>
        </form>
    </div>
</main>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>