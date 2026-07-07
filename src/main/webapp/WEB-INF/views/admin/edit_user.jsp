<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Boba Station - Chỉnh sửa người dùng</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f8f9fa;
            color: #2d3436;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px 0;
        }
        .form-card {
            background: #ffffff;
            border: none;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(108, 92, 231, 0.06);
            width: 100%;
            max-width: 550px;
            overflow: hidden;
        }
        .card-header-custom {
            background: linear-gradient(135deg, #6c5ce7, #4834d4);
            color: #ffffff;
            padding: 25px;
            text-align: center;
        }
        .card-body-custom {
            padding: 30px;
        }
        .form-control:focus {
            border-color: #a29bfe;
            box-shadow: 0 0 0 0.25rem rgba(108, 92, 231, 0.15);
        }
        .btn-update {
            background-color: #6c5ce7;
            border-color: #6c5ce7;
            color: #ffffff;
            transition: all 0.3s ease;
        }
        .btn-update:hover {
            background-color: #4834d4;
            border-color: #4834d4;
            color: #ffffff;
        }
    </style>
</head>
<body>

<div class="form-card m-3 shadow-sm">
    <div class="card-header-custom">
        <h3 class="fw-bold m-0"><i class="bi bi-person-gear me-2"></i>Chỉnh sửa người dùng</h3>
        <p class="small text-white-50 m-0 mt-1">Cập nhật hồ sơ thành viên hoặc tài khoản quản trị</p>
    </div>

    <div class="card-body-custom">
        <form action="${pageContext.request.contextPath}/admin/user/update" method="post">
            <input type="hidden" name="user_id" value="${user.id}">

            <div class="mb-3">
                <label for="username" class="form-label fw-medium text-secondary">Tên người dùng</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-person-fill"></i></span>
                    <input type="text" class="form-control rounded-end-3 border-start-0 ps-0"
                           id="username" name="username" value="<c:out value='${user.username}'/>" placeholder="Nhập họ và tên..." required>
                </div>
            </div>

            <div class="mb-3">
                <label for="email" class="form-label fw-medium text-secondary">Địa chỉ Email</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-envelope-fill"></i></span>
                    <input type="email" class="form-control rounded-end-3 border-start-0 ps-0"
                           id="email" name="email" value="<c:out value='${user.email}'/>" placeholder="name@example.com" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="phone" class="form-label fw-medium text-secondary">Số điện thoại</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-telephone-fill"></i></span>
                    <input type="tel" class="form-control rounded-end-3 border-start-0 ps-0"
                           id="phone" name="phone" value="<c:out value='${user.phone}'/>" placeholder="Nhập số điện thoại di động..." required>
                </div>
            </div>

            <div class="mb-4">
                <label for="address" class="form-label fw-medium text-secondary">Địa chỉ cư trú</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-geo-alt-fill"></i></span>
                    <input type="text" class="form-control rounded-end-3 border-start-0 ps-0"
                           id="address" name="address" value="<c:out value='${user.address}'/>" placeholder="Số nhà, tên đường, phường/xã..." required>
                </div>
            </div>

            <div class="d-grid gap-2 d-sm-flex justify-content-sm-end mt-4">
                <a href="${pageContext.request.contextPath}/admin/user/manage" class="btn btn-light rounded-pill px-4 text-secondary border">
                    <i class="bi bi-arrow-left me-1"></i>Quay lại
                </a>
                <button type="submit" class="btn btn-update rounded-pill px-4 fw-semibold shadow-sm">
                    <i class="bi bi-check-lg me-1"></i>Lưu thay đổi
                </button>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>