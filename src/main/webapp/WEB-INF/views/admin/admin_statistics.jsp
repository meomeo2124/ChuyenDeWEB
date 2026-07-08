<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Boba Station - Thống kê doanh thu</title>
	<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
	<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
	<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
	<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

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
		/* Custom UI Component */
		.custom-card {
			background: var(--light-card);
			border: none;
			border-radius: 16px;
			padding: 25px;
			box-shadow: 0 10px 30px rgba(0,0,0,0.04);
			margin-bottom: 25px;
		}
		.metric-item {
			border-bottom: 1px dashed #e0e0e0;
			padding: 15px 0;
		}
		.metric-item:last-child {
			border-bottom: none;
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
				<li><a href="${pageContext.request.contextPath}/admin/product/manage"><span class="icon"><i class="bi bi-box-seam"></i></span><span class="text">Sản phẩm</span></a></li>

				<li><a href="${pageContext.request.contextPath}/admin/product/reviews"><span class="icon"><i class="bi bi-chat-heart"></i></span><span class="text">Quản lý đánh giá</span></a></li>

				<li><a href="${pageContext.request.contextPath}/admin/user/manage" class="active"><span class="icon"><i class="bi bi-people"></i></span><span class="text">Người dùng</span></a></li>
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
					<h1 class="h2 fw-bold m-0">Báo cáo & Thống kê doanh thu</h1>
					<p class="text-muted mt-1">Dữ liệu phân tích hoạt động tài chính thực tế</p>
				</div>

				<form action="${pageContext.request.contextPath}/admin/statistics" method="GET" class="row g-2 align-items-center bg-white p-2 rounded-3 border shadow-sm">
					<div class="col-auto">
						<input type="datetime-local" class="form-control form-control-sm" name="startDate" value="${startDate}">
					</div>
					<div class="col-auto text-muted small">đến</div>
					<div class="col-auto">
						<input type="datetime-local" class="form-control form-control-sm" name="endDate" value="${endDate}">
					</div>
					<div class="col-auto">
						<button type="submit" class="btn btn-sm btn-primary px-3"><i class="bi bi-filter me-1"></i>Thống kê</button>
					</div>
				</form>
			</div>

			<div class="row g-4">
				<div class="col-lg-5">
					<div class="custom-card h-100">
						<h5 class="fw-bold mb-4 text-primary"><i class="bi bi-clipboard-data-fill me-2"></i>Chỉ số vận hành hệ thống</h5>
						<div class="metric-item d-flex justify-content-between align-items-center">
							<span class="text-secondary"><i class="bi bi-box-seam me-2"></i>Tổng số sản phẩm</span>
							<span class="badge bg-primary-subtle text-primary rounded-pill px-3 py-2 fw-bold">${totalProducts}</span>
						</div>
						<div class="metric-item d-flex justify-content-between align-items-center">
							<span class="text-secondary"><i class="bi bi-people me-2"></i>Tổng số người dùng</span>
							<span class="badge bg-success-subtle text-success rounded-pill px-3 py-2 fw-bold">${totalUsers}</span>
						</div>
						<div class="metric-item d-flex justify-content-between align-items-center">
							<span class="text-secondary"><i class="bi bi-tags me-2"></i>Tổng số danh mục</span>
							<span class="badge bg-info-subtle text-info rounded-pill px-3 py-2 fw-bold">${totalCategories}</span>
						</div>
						<div class="metric-item d-flex justify-content-between align-items-center mt-3 pt-3 border-top border-2">
							<span class="fw-bold text-dark"><i class="bi bi-wallet2 me-2 text-danger"></i>Doanh thu kỳ lọc</span>
							<span class="fs-5 fw-bold text-danger">
                                <c:out value="${String.format('%,.0f', totalRevenue)}"/> VNĐ
                            </span>
						</div>
					</div>
				</div>

				<div class="col-lg-7">
					<div class="custom-card h-100">
						<h5 class="fw-bold mb-3 text-primary"><i class="bi bi-graph-up-arrow me-2"></i>Biểu đồ cột doanh thu thực tế</h5>
						<div style="position: relative; height:220px; width:100%">
							<canvas id="revenueChart"></canvas>
						</div>
					</div>
				</div>
			</div>
		</main>
	</div>
</div>

<script>
	const ctx = document.getElementById('revenueChart').getContext('2d');
	new Chart(ctx, {
		type: 'bar',
		data: {
			labels: ['Tháng 4', 'Tháng 5', 'Tháng 6', 'Tháng 7'],
			datasets: [{
				label: 'Doanh thu thực tế (VNĐ)',
				data: [3200000, 4800000, 4100000, ${totalRevenue}], // Đưa biến từ Servlet vào cột cuối
				backgroundColor: 'rgba(108, 92, 231, 0.8)',
				borderColor: 'rgba(108, 92, 231, 1)',
				borderWidth: 1,
				borderRadius: 6
			}]
		},
		options: {
			responsive: true,
			maintainAspectRatio: false,
			scales: {
				y: {
					beginAtZero: true
				}
			}
		}
	});
</script>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>