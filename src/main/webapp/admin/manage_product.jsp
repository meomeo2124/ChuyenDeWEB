<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Quản Lý Sản Phẩm</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
	<style>
		.container { max-width: 1200px; margin: 0 auto; padding: 20px; background-color: #fff; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }
		h1 { color: #333; text-align: center; margin-bottom: 20px; }
		.btn-back, .btn-add { display: inline-block; margin-bottom: 20px; padding: 10px 20px; border-radius: 5px; text-decoration: none; text-align: center; transition: background-color 0.3s ease; }
		.btn-back { background-color: #6c757d; color: white; margin-right: 10px; }
		.btn-back:hover { background-color: #5a6268; }
		.btn-add { background-color: #4CAF50; color: white; }
		.btn-add:hover { background-color: #45A049; }
		table { width: 100%; border-collapse: collapse; margin-top: 20px; }
		th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
		th { background-color: #343a40; color: white; font-weight: bold; }
		tr:hover { background-color: #f1f1f1; color: #333; transition: background-color 0.3s ease; }
		.action-links { display: flex; gap: 8px; align-items: center; }
		.action-links a, .action-links button { padding: 5px 10px; text-decoration: none; border-radius: 3px; border: none; font-size: 14px; }
		.action-links a.edit { background-color: #007bff; color: white; }
		.action-links button.delete { background-color: #dc3545; color: white; }
		.product-image { max-width: 100px; max-height: 100px; background-color: #f0f0f0; display: block; }
	</style>
</head>
<body>
<header class="admin-header">
	<div class="logo">Admin Boba Station</div>
	<div class="header-right">
		<input type="text" class="search-bar" placeholder="Tìm kiếm...">
		<button class="add-btn" onclick="toggleSidebar()">Thêm mới</button>
	</div>
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
	<div class="container">
		<h1>Quản Lý Sản Phẩm</h1>

		<%-- Hiển thị thông báo Alert --%>
		<c:if test="${not empty success}">
			<div class="alert alert-success text-center">${success}</div>
		</c:if>
		<c:if test="${not empty error}">
			<div class="alert alert-danger text-center">${error}</div>
		</c:if>

		<a href="${pageContext.request.contextPath}/admin/dashboard" class="btn-back">Quay lại Dashboard</a>
		<a href="${pageContext.request.contextPath}/admin/product/insert" class="btn-add">Thêm Sản Phẩm Mới</a>

		<table>
			<thead>
			<tr>
				<th>Tên Sản Phẩm</th>
				<th>Mô Tả</th>
				<th>Giá</th>
				<th>Danh Mục</th>
				<th>Ảnh</th>
				<th>Tồn kho</th>
				<th>Thao Tác</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="product" items="${productList}">
				<tr>
					<td><c:out value="${product.name}" default="Chưa có tên"/></td>
					<td><c:out value="${product.description}" default="Chưa có mô tả"/></td>
					<td>${product.price} VNĐ</td>
					<td><c:out value="${product.category != null ? product.category.title : 'Chưa phân loại'}"/></td>
					<td>
						<img src="${pageContext.request.contextPath}/image/product/${not empty product.photo ? product.photo : 'no-sample.png'}"
							 alt="Product" class="product-image" width="100" height="100" style="object-fit: cover;"
							 onerror="this.src='${pageContext.request.contextPath}/image/product/no-sample.png';">
					</td>
					<td>${product.stock}</td>
					<td class="action-links">
						<a href="${pageContext.request.contextPath}/admin/product/edit?id=${product.id}" class="edit">Chỉnh Sửa</a>

							<%-- SỬA: Chuyển nút Xóa sang phương thức POST an toàn khớp với Controller --%>
						<form action="${pageContext.request.contextPath}/admin/product/delete" method="POST" onsubmit="return confirm('Bạn có chắc chắn muốn xóa?');" style="margin:0;">
							<input type="hidden" name="id" value="${product.id}">
							<button type="submit" class="delete">Xóa</button>
						</form>
					</td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>
</main>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
	function toggleSidebar() {
		document.querySelector('.admin-nav').classList.toggle('expanded');
		document.querySelector('.admin-main').classList.toggle('expanded');
	}
</script>
</body>
</html>