<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Boba Station - Thêm người dùng mới</title>
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
        /* Modernized Form Card */
        .custom-form-card {
            background: #ffffff;
            border: none;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.03);
            max-width: 650px;
            margin: 0 auto;
        }
        .form-control:focus {
            border-color: #a29bfe;
            box-shadow: 0 0 0 0.25rem rgba(108, 92, 231, 0.15);
        }
        .btn-insert {
            background-color: #6c5ce7;
            border-color: #6c5ce7;
            color: #ffffff;
            transition: all 0.3s ease;
        }
        .btn-insert:hover {
            background-color: #4834d4;
            border-color: #4834d4;
            color: #ffffff;
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
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-4 border-bottom">
                <div>
                    <h1 class="h2 fw-bold m-0">Thêm người dùng mới</h1>
                    <p class="text-muted mt-1">Tạo mới tài khoản thành viên khách hàng hoặc nhân viên</p>
                </div>
            </div>

            <%-- KHỐI HIỂN THỊ THÔNG BÁO LỖI NẾU CÓ --%>
            <c:if test="${not empty error}">
                <div class="alert alert-danger border-0 shadow-sm rounded-3 text-center mb-4">
                    <i class="bi bi-exclamation-triangle-fill me-2"></i>${error}
                </div>
            </c:if>

            <div class="custom-form-card p-4 p-sm-5 border">
                <h5 class="fw-bold mb-4 text-primary text-center text-sm-start"><i class="bi bi-person-plus-fill me-2"></i>Thông tin tài khoản khởi tạo</h5>

                <form action="${pageContext.request.contextPath}/admin/user/insert" method="post">

                    <div class="mb-3">
                        <label for="username" class="form-label fw-medium text-secondary">Tên người dùng</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-person-fill"></i></span>
                            <input type="text" class="form-control rounded-end-3 border-start-0 ps-0" id="username" name="username" placeholder="Nhập họ và tên tài khoản..." required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="email" class="form-label fw-medium text-secondary">Địa chỉ Email</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-envelope-fill"></i></span>
                            <input type="email" class="form-control rounded-end-3 border-start-0 ps-0" id="email" name="email" placeholder="name@example.com" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="password" class="form-label fw-medium text-secondary">Mật khẩu khởi tạo</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-key-fill"></i></span>
                            <input type="password" class="form-control rounded-end-3 border-start-0 ps-0" id="password" name="password" placeholder="Nhập mật khẩu bí mật..." required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="phone" class="form-label fw-medium text-secondary">Số điện thoại</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-telephone-fill"></i></span>
                            <input type="text" class="form-control rounded-end-3 border-start-0 ps-0" id="phone" name="phone" placeholder="Nhập số điện thoại di động..." required>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label for="address" class="form-label fw-medium text-secondary">Địa chỉ cư trú</label>
                        <div class="input-group">
                            <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-geo-alt-fill"></i></span>
                            <input type="text" class="form-control rounded-end-3 border-start-0 ps-0" id="address" name="address" placeholder="Số nhà, tên đường, khu vực sinh sống..." required>
                        </div>
                    </div>

                    <div class="d-grid gap-2 d-sm-flex justify-content-sm-end mt-4">
                        <a href="${pageContext.request.contextPath}/admin/user/manage" class="btn btn-light rounded-pill px-4 text-secondary border">
                            <i class="bi bi-arrow-left me-1"></i>Quay lại
                        </a>
                        <button type="submit" class="btn btn-insert rounded-pill px-4 fw-semibold shadow-sm">
                            <i class="bi bi-person-plus me-1"></i>Tạo tài khoản
                        </button>
                    </div>
                </form>
            </div>
        </main>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>