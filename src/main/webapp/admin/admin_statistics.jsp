<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Thống kê</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5" style="max-width: 700px;">
	<div class="card shadow">
		<div class="card-header bg-dark text-white text-center">
			<h2 class="mb-0">Thống Kê Tổng Quan Hệ Thống</h2>
		</div>
		<div class="card-body p-4">
			<ul class="list-group list-group-flushfs-5">
				<li class="list-group-item d-flex justify-content-between align-items-center py-3">
					Tổng số sản phẩm
					<span class="badge bg-primary rounded-pill fs-6">${totalProducts}</span>
				</li>
				<li class="list-group-item d-flex justify-content-between align-items-center py-3">
					Tổng số người dùng
					<span class="badge bg-success rounded-pill fs-6">${totalUsers}</span>
				</li>
				<li class="list-group-item d-flex justify-content-between align-items-center py-3">
					Tổng số danh mục
					<span class="badge bg-info text-dark rounded-pill fs-6">${totalCategories}</span>
				</li>
				<li class="list-group-item d-flex justify-content-between align-items-center py-3 fw-bold table-light text-danger">
					Doanh thu hệ thống
					<span>${totalRevenue} VNĐ</span>
				</li>
			</ul>
			<div class="text-center mt-4">
				<a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary">Quay lại Dashboard</a>
			</div>
		</div>
	</div>
</div>
</body>
</html>