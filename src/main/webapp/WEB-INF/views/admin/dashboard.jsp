<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Boba Station - Admin Dashboard</title>
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
        /* Top Header Navigation */
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
        /* Dashboard Stats Cards */
        .stat-card {
            background: var(--light-card);
            border: none;
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.04);
            transition: transform 0.3s ease;
        }
        .stat-card:hover {
            transform: translateY(-5px);
        }
        .stat-icon {
            width: 50px;
            height: 50px;
            border-radius: 12px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            margin-bottom: 15px;
        }
        /* Modernized Elegant Table */
        .table-container {
            background: #fff;
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.04);
            margin-top: 30px;
        }
        .table img.product-img {
            width: 45px;
            height: 45px;
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
                <li><a href="${pageContext.request.contextPath}/admin/dashboard" class="active"><span class="icon"><i class="bi bi-speedometer2"></i></span><span class="text">Tổng quan</span></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/product/manage"><span class="icon"><i class="bi bi-box-seam"></i></span><span class="text">Sản phẩm</span></a></li>

                <li><a href="${pageContext.request.contextPath}/admin/product/reviews"><span class="icon"><i class="bi bi-chat-heart"></i></span><span class="text">Quản lý đánh giá</span></a></li>

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
                    <h1 class="h2 fw-bold m-0">Tổng quan hệ thống</h1>
                    <p class="text-muted mt-1">Hệ thống phân tích kinh doanh Boba Station dữ liệu thực</p>
                </div>

                <form action="${pageContext.request.contextPath}/admin/dashboard" method="GET" class="row g-2 align-items-center bg-white p-2 rounded-3 border shadow-sm">
                    <div class="col-auto">
                        <input type="datetime-local" class="form-control form-control-sm" name="startDate" value="${startDate}">
                    </div>
                    <div class="col-auto text-muted small">đến</div>
                    <div class="col-auto">
                        <input type="datetime-local" class="form-control form-control-sm" name="endDate" value="${endDate}">
                    </div>
                    <div class="col-auto">
                        <button type="submit" class="btn btn-sm btn-primary px-3"><i class="bi bi-filter me-1"></i>Lọc</button>
                    </div>
                </form>
            </div>

            <div class="row g-4">
                <div class="col-12 col-md-6 col-xl-3">
                    <div class="stat-card">
                        <div class="stat-icon bg-primary-subtle text-primary"><i class="bi bi-box-seam-fill"></i></div>
                        <h6 class="text-muted fw-normal">Tổng số sản phẩm</h6>
                        <h3 class="fw-bold my-2">${totalProducts}</h3>
                    </div>
                </div>
                <div class="col-12 col-md-6 col-xl-3">
                    <div class="stat-card">
                        <div class="stat-icon bg-success-subtle text-success"><i class="bi bi-people-fill"></i></div>
                        <h6 class="text-muted fw-normal">Tổng người dùng</h6>
                        <h3 class="fw-bold my-2">${totalUsers}</h3>
                    </div>
                </div>
                <div class="col-12 col-md-6 col-xl-3">
                    <div class="stat-card">
                        <div class="stat-icon bg-info-subtle text-info"><i class="bi bi-patch-check-fill"></i></div>
                        <h6 class="text-muted fw-normal">Đơn đã thu tiền</h6>
                        <h3 class="fw-bold my-2 text-success">${paidOrders} đơn</h3>
                    </div>
                </div>
                <div class="col-12 col-md-6 col-xl-3">
                    <div class="stat-card">
                        <div class="stat-icon bg-warning-subtle text-warning"><i class="bi bi-hourglass-split"></i></div>
                        <h6 class="text-muted fw-normal">Đơn chờ xử lý</h6>
                        <h3 class="fw-bold my-2 text-warning">${pendingOrders} đơn</h3>
                    </div>
                </div>

                <div class="col-12">
                    <div class="stat-card bg-gradient text-dark border-start border-4 border-danger">
                        <div class="d-flex justify-content-between align-items-center">
                            <div>
                                <h6 class="text-muted fw-normal">Doanh thu thực tế (Từ đơn đã thanh toán trong khoảng thời gian trên)</h6>
                                <h2 class="fw-bold my-1 text-danger"><c:out value="${String.format('%,.0f', totalRevenue)}"/> VNĐ</h2>
                            </div>
                            <div class="fs-1 text-danger-subtle opacity-50"><i class="bi bi-cash-stack"></i></div>
                        </div>
                    </div>
                </div>
            </div>

            <div class="table-container">
            </div>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>