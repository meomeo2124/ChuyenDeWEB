<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Boba Station Admin - Quản lý đánh giá</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body { font-family: 'Inter', sans-serif; background-color: #f8f9fa; color: #2d3436; }
        .admin-header { background: linear-gradient(135deg, #6c5ce7, #4834d4); color: #fff; padding: 15px 30px; display: flex; justify-content: space-between; align-items: center; }
        .admin-nav { background-color: #fff; border-right: 1px solid #e0e0e0; min-height: calc(100vh - 72px); padding-top: 20px; }
        .admin-nav ul { list-style: none; padding: 0; }
        .admin-nav ul li a { display: flex; align-items: center; padding: 14px 25px; color: #636e72; text-decoration: none; font-weight: 500; border-left: 4px solid transparent; }
        .admin-nav ul li a:hover, .admin-nav ul li a.active { background-color: #f1f2f6; color: #6c5ce7; border-left-color: #6c5ce7; }
        .admin-nav ul li a .icon { margin-right: 15px; font-size: 18px; }
        .table-card { background: #ffffff; border-radius: 16px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.02); overflow: hidden; }
        .table thead th { background-color: #f8f9fa; color: #636e72; font-weight: 600; text-transform: uppercase; font-size: 11px; padding: 15px; border-bottom: 2px solid #e0e0e0; }
        .table tbody td { padding: 15px; vertical-align: middle; font-size: 13.5px; }
    </style>
</head>
<body>

<header class="admin-header">
    <div class="fw-bold fs-5"><i class="bi bi-cup-straw me-2"></i>Admin Boba Station</div>
    <span class="badge bg-white text-dark p-2">Hệ thống Quản trị</span>
</header>

<div class="container-fluid">
    <div class="row">
        <nav class="col-md-3 col-lg-2 d-none d-md-block admin-nav">
            <ul>
                <li><a href="${pageContext.request.contextPath}/admin/dashboard"><span class="icon"><i class="bi bi-speedometer2"></i></span>Tổng quan</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/product/manage"><span class="icon"><i class="bi bi-box-seam"></i></span>Sản phẩm</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/product/reviews" class="active"><span class="icon"><i class="bi bi-chat-heart"></i></span>Quản lý đánh giá</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/user/manage"><span class="icon"><i class="bi bi-people"></i></span>Người dùng</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/category/manage"><span class="icon"><i class="bi bi-tags"></i></span>Danh mục</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/order/manage"><span class="icon"><i class="bi bi-receipt"></i></span>Đơn hàng</a></li>
                <li class="mt-4"><hr class="text-muted"></li>
                <li><a href="${pageContext.request.contextPath}/logout" class="text-danger"><span class="icon"><i class="bi bi-box-arrow-right"></i></span>Đăng xuất</a></li>
            </ul>
        </nav>

        <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
            <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-4 border-bottom">
                <div>
                    <h1 class="h3 fw-bold m-0">Quản lý phản hồi & Đánh giá</h1>
                    <p class="text-muted small mt-1">Kiểm duyệt các phản hồi chấm sao và hình ảnh thực tế từ khách hàng</p>
                </div>
            </div>

            <c:if test="${not empty success}"><div class="alert alert-success border-0 small shadow-sm mb-3">${success}</div></c:if>
            <c:if test="${not empty error}"><div class="alert alert-danger border-0 small shadow-sm mb-3">${error}</div></c:if>

            <div class="card table-card border p-3">
                <div class="table-responsive">
                    <table class="table align-middle m-0">
                        <thead>
                        <tr>
                            <th>Khách hàng / Tên đồ uống</th>
                            <th class="text-center">Số sao</th>
                            <th style="width: 40%;">Nội dung bình luận</th>
                            <th class="text-center">Ảnh thực tế</th>
                            <th class="text-center">Thời gian</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="rev" items="${reviewList}">
                            <tr>
                                <td>
                                    <span class="fw-bold text-dark d-block mb-1"><c:out value="${rev.username}"/></span>
                                    <c:if test="${rev.orderId > 0}">
                                        <span class="badge bg-light text-secondary border fw-medium" style="font-size: 11px;">
                                            <i class="bi bi-receipt me-1"></i>Đơn #${rev.orderId}
                                        </span>
                                    </c:if>
                                </td>
                                <td class="text-center text-warning" style="font-size: 12px;">
                                    <c:forEach begin="1" end="${rev.rating}"><i class="bi bi-star-fill"></i></c:forEach>
                                </td>
                                <td><p class="text-secondary m-0 text-wrap"><c:out value="${rev.comment}"/></p></td>
                                <td class="text-center">
                                    <c:if test="${not empty rev.imagePath}">
                                        <img src="${pageContext.request.contextPath}/uploads/${rev.imagePath}" class="rounded border" style="width: 45px; height: 45px; object-fit: cover; cursor:pointer;" onclick="window.open(this.src)">
                                    </c:if>
                                    <c:if test="${empty rev.imagePath}"><span class="text-muted small">-</span></c:if>
                                </td>
                                <td class="text-center text-muted small"><fmt:formatDate value="${rev.createdAt}" pattern="dd/MM/yyyy HH:mm"/></td>
                                <td class="text-center">
                                    <form action="${pageContext.request.contextPath}/admin/product/reviews/delete" method="POST" onsubmit="return confirm('Bạn có chắc chắn muốn gỡ bỏ vĩnh viễn đánh giá này không?');" style="margin:0;">
                                        <input type="hidden" name="reviewId" value="${rev.id}">
                                        <button type="submit" class="btn btn-sm btn-outline-danger border-0"><i class="bi bi-trash3-fill"></i></button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty reviewList}">
                            <tr><td colspan="6" class="text-center py-5 text-muted">Hệ thống chưa ghi nhận lượt đánh giá nào từ khách hàng.</td></tr>
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