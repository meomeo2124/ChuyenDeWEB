<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Boba Station - Quản lý danh mục</title>
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
        /* Custom Modern Form & Card */
        .custom-card {
            background: var(--light-card);
            border: none;
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.04);
            margin-bottom: 25px;
        }
        /* Elegant Table */
        .table-container {
            background: #fff;
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.04);
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
                <li><a href="${pageContext.request.contextPath}/admin/user/manage"><span class="icon"><i class="bi bi-people"></i></span><span class="text">Người dùng</span></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/category/manage" class="active"><span class="icon"><i class="bi bi-tags"></i></span><span class="text">Danh mục</span></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/order/manage"><span class="icon"><i class="bi bi-receipt"></i></span><span class="text">Đơn hàng</span></a></li>
                <li><a href="${pageContext.request.contextPath}/admin/statistics"><span class="icon"><i class="bi bi-bar-chart-line"></i></span><span class="text">Thống kê doanh thu</span></a></li>
                <li class="mt-4"><hr class="text-muted"></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="text-danger"><span class="icon"><i class="bi bi-box-arrow-right"></i></span><span class="text">Đăng xuất</span></a></li>
            </ul>
        </nav>

        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
                <div>
                    <h1 class="h2 fw-bold m-0">Quản lý danh mục sản phẩm</h1>
                    <p class="text-muted mt-1">Phân loại thực đơn trà sữa và đồ uống hệ thống</p>
                </div>
            </div>

            <%-- VÙNG THÔNG BÁO ALERT ĐẸP HƠN --%>
            <c:if test="${not empty msg}">
                <div class="alert alert-success border-0 shadow-sm rounded-3 text-center"><i class="bi bi-check-circle-fill me-2"></i>${msg}</div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger border-0 shadow-sm rounded-3 text-center"><i class="bi bi-exclamation-triangle-fill me-2"></i>${error}</div>
            </c:if>

            <div class="custom-card">
                <h5 class="fw-bold mb-3 text-primary"><i class="bi bi-plus-circle-fill me-2"></i>Thêm danh mục mới</h5>
                <form action="${pageContext.request.contextPath}/admin/category/insert" method="post">
                    <div class="row g-3">
                        <div class="col-md-4">
                            <label for="title" class="form-label fw-medium text-secondary">Tên danh mục</label>
                            <input type="text" class="form-control rounded-3" name="title" id="title" placeholder="Ví dụ: Trà sữa truyền thống" required />
                        </div>
                        <div class="col-md-6">
                            <label for="description" class="form-label fw-medium text-secondary">Mô tả ngắn</label>
                            <input type="text" class="form-control rounded-3" name="description" id="description" placeholder="Nhập vài từ mô tả..." required />
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button type="submit" class="btn btn-primary w-100 rounded-3 py-2 fw-semibold"><i class="bi bi-save me-1"></i>Lưu lại</button>
                        </div>
                    </div>
                </form>
            </div>

            <div class="table-container">
                <h5 class="fw-bold mb-4"><i class="bi bi-collection me-2 text-primary"></i>Các danh mục đang hoạt động</h5>

                <div class="table-responsive">
                    <table class="table table-hover align-middle">
                        <thead class="table-light text-secondary">
                        <tr>
                            <th scope="col" style="width: 10%;">ID</th>
                            <th scope="col" style="width: 25%;">Tên danh mục</th>
                            <th scope="col" style="width: 45%;">Mô tả</th>
                            <th scope="col" style="width: 20%;" class="text-center">Hành động</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="category" items="${categoryList}">
                            <tr>
                                <td><span class="badge bg-light text-dark border p-2">#${category.id}</span></td>
                                <td><span class="fw-bold text-dark"><c:out value="${category.title}"/></span></td>
                                <td><span class="text-muted"><c:out value="${category.description}"/></span></td>
                                <td>
                                    <div class="d-flex justify-content-center gap-2">
                                        <a href="${pageContext.request.contextPath}/admin/category/edit?id=${category.id}"
                                           class="btn btn-sm btn-outline-warning rounded-pill px-3">
                                            <i class="bi bi-pencil-square me-1"></i>Sửa
                                        </a>

                                        <form action="${pageContext.request.contextPath}/admin/category/delete" method="post"
                                              onsubmit="return confirm('Bạn chắc chắn muốn xóa danh mục này?');" style="margin:0;">
                                            <input type="hidden" name="id" value="${category.id}" />
                                            <button type="submit" class="btn btn-sm btn-outline-danger rounded-pill px-3">
                                                <i class="bi bi-trash3 me-1"></i>Xóa
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty categoryList}">
                            <tr>
                                <td colspan="4" class="text-center text-muted py-4">Chưa có danh mục nào được khởi tạo trong hệ thống.</td>
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