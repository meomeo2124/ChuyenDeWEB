<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Boba Station - Quản lý kho hàng</title>
	<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
	<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

	<style>
		:root {
			--primary-color: #6c5ce7;
			--secondary-color: #a29bfe;
			--dark-bg: #1e1e24;
			--light-card: #ffffff;
			--text-main: #2d3436;
		}
		body {
			font-family: 'Inter', sans-serif;
			background-color: #f8f9fa;
			color: var(--text-main);
			overflow-x: hidden;
		}
		/* Top Header */
		.admin-header {
			background: linear-gradient(135deg, #6c5ce7, #4834d4);
			color: #fff;
			padding: 15px 30px;
			display: flex;
			justify-content: space-between;
			align-items: center;
			box-shadow: 0 4px 15px rgba(0,0,0,0.1);
		}
		.admin-header .logo {
			font-size: 22px;
			font-weight: 700;
			letter-spacing: 1px;
		}
		/* Side Navigation */
		.admin-nav {
			background-color: #fff;
			border-right: 1px solid #e0e0e0;
			min-height: calc(100vh - 72px);
			padding-top: 20px;
		}
		.admin-nav ul {
			list-style: none;
			padding: 0;
			margin: 0;
		}
		.admin-nav ul li a {
			display: flex;
			align-items: center;
			padding: 14px 25px;
			color: #636e72;
			text-decoration: none;
			font-weight: 500;
			transition: all 0.3s ease;
			border-left: 4px solid transparent;
		}
		.admin-nav ul li a:hover, .admin-nav ul li a.active {
			background-color: #f1f2f6;
			color: var(--primary-color);
			border-left-color: var(--primary-color);
		}
		.admin-nav ul li a .icon {
			margin-right: 15px;
			font-size: 18px;
		}
		/* Elegant Table Container */
		.table-container {
			background: #fff;
			border-radius: 16px;
			padding: 25px;
			box-shadow: 0 10px 30px rgba(0,0,0,0.04);
		}
		.product-img {
			width: 50px;
			height: 50px;
			object-fit: cover;
			border-radius: 10px;
			border: 2px solid #f1f2f6;
		}
	</style>
</head>
<body>

<header class="admin-header">
	<div class="logo"><i class="bi bi-cup-straw me-2"></i>Admin Boba Station</div>
	<div class="d-flex align-items-center gap-3">
		<span class="badge bg-white text-dark p-2"><i class="bi bi-person-badge-fill me-1"></i>Hệ thống Quản trị</span>
	</div>
</header>

<div class="container-fluid">
	<div class="row">
		<nav class="col-md-3 col-lg-2 d-none d-md-block admin-nav">
			<ul>
				<li><a href="${pageContext.request.contextPath}/admin/dashboard"><span class="icon"><i class="bi bi-speedometer2"></i></span><span class="text">Tổng quan</span></a></li>
				<li><a href="${pageContext.request.contextPath}/admin/product/manage" class="active"><span class="icon"><i class="bi bi-box-seam"></i></span><span class="text">Sản phẩm</span></a></li>
				<li><a href="${pageContext.request.contextPath}/admin/user/manage"><span class="icon"><i class="bi bi-people"></i></span><span class="text">Người dùng</span></a></li>
				<li><a href="${pageContext.request.contextPath}/admin/category/manage"><span class="icon"><i class="bi bi-tags"></i></span><span class="text">Danh mục</span></a></li>
				<li><a href="${pageContext.request.contextPath}/admin/order/manage"><span class="icon"><i class="bi bi-receipt"></i></span><span class="text">Đơn hàng</span></a></li>
				<li><a href="${pageContext.request.contextPath}/admin/statistics"><span class="icon"><i class="bi bi-bar-chart-line"></i></span><span class="text">Thống kê doanh thu</span></a></li>
				<li class="mt-4"><hr class="text-muted"></li>
				<li><a href="${pageContext.request.contextPath}/logout" class="text-danger"><span class="icon"><i class="bi bi-box-arrow-right"></i></span><span class="text">Đăng xuất</span></a></li>
			</ul>
		</nav>

		<main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
			<div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
				<div>
					<h1 class="h2 fw-bold m-0">Quản lý kho hàng</h1>
					<p class="text-muted mt-1">Cập nhật thông tin đồ uống, số lượng tồn kho thực tế</p>
				</div>
			</div>

			<%-- HIỂN THỊ THÔNG BÁO ALERT --%>
			<c:if test="${not empty success}">
				<div class="alert alert-success border-0 shadow-sm rounded-3 text-center"><i class="bi bi-check-circle-fill me-2"></i>${success}</div>
			</c:if>
			<c:if test="${not empty error}">
				<div class="alert alert-danger border-0 shadow-sm rounded-3 text-center"><i class="bi bi-exclamation-triangle-fill me-2"></i>${error}</div>
			</c:if>

			<div class="table-container">
				<div class="d-flex justify-content-between align-items-center mb-4">
					<h5 class="fw-bold m-0"><i class="bi bi-cup-hot me-2 text-primary"></i>Danh sách mặt hàng thực đơn</h5>
					<a href="${pageContext.request.contextPath}/admin/product/insert" class="btn btn-primary rounded-pill px-4 fw-semibold"><i class="bi bi-plus-lg me-1"></i>Thêm sản phẩm</a>
				</div>

				<div class="table-responsive">
					<table class="table table-hover align-middle">
						<thead class="table-light text-secondary">
						<tr>
							<th scope="col" style="width: 10%;" class="text-center">Ảnh</th>
							<th scope="col" style="width: 20%;">Tên Sản Phẩm</th>
							<th scope="col" style="width: 25%;">Mô Tả</th>
							<th scope="col" style="width: 15%;">Giá Bán</th>
							<th scope="col" style="width: 12%;">Danh Mục</th>
							<th scope="col" class="text-center" style="width: 8%;">Kho</th>
							<th scope="col" class="text-center" style="width: 10%;">Thao Tác</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach var="product" items="${productList}">
							<tr>
								<td class="text-center">
									<img src="${pageContext.request.contextPath}/image/product/${not empty product.photo ? product.photo : 'no-sample.png'}"
										 alt="Product" class="product-img"
										 onerror="this.src='${pageContext.request.contextPath}/image/product/no-sample.png';">
								</td>
								<td><span class="fw-bold text-dark"><c:out value="${product.name}" default="Chưa có tên"/></span></td>
								<td><span class="text-muted small"><c:out value="${product.description}" default="Chưa có mô tả"/></span></td>
								<td><span class="text-primary fw-semibold"><c:out value="${String.format('%,.0f', product.price)}"/> VNĐ</span></td>
								<td><span class="badge bg-light text-secondary border"><c:out value="${product.category != null ? product.category.title : 'Chưa phân loại'}"/></span></td>
								<td class="text-center"><span class="fw-medium">${product.stock}</span></td>
								<td>
									<div class="d-flex justify-content-center gap-2">
										<a href="${pageContext.request.contextPath}/admin/product/edit?id=${product.id}"
										   class="btn btn-sm btn-outline-warning rounded-circle p-2" title="Chỉnh sửa">
											<i class="bi bi-pencil-square"></i>
										</a>

										<form action="${pageContext.request.contextPath}/admin/product/delete" method="POST"
											  onsubmit="return confirm('Bạn có chắc chắn muốn xóa sản phẩm này?');" style="margin:0;">
											<input type="hidden" name="id" value="${product.id}">
											<button type="submit" class="btn btn-sm btn-outline-danger rounded-circle p-2" title="Xóa bỏ">
												<i class="bi bi-trash3"></i>
											</button>
										</form>
									</div>
								</td>
							</tr>
						</c:forEach>
						<c:if test="${empty productList}">
							<tr>
								<td colspan="7" class="text-center text-muted py-4">Kho hàng trống. Chưa có sản phẩm nào được khởi tạo.</td>
							</tr>
						</c:if>
						</tbody>
					</table>
				</div>
			</div>
		</main>
	</div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>