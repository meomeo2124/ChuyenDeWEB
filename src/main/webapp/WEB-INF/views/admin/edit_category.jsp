<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Boba Station - Chỉnh sửa danh mục</title>
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
        <h3 class="fw-bold m-0"><i class="bi bi-pencil-square me-2"></i>Chỉnh sửa danh mục</h3>
        <p class="small text-white-50 m-0 mt-1">Cập nhật thông tin phân loại thực đơn hệ thống</p>
    </div>

    <div class="card-body-custom">
        <form action="${pageContext.request.contextPath}/admin/category/update" method="post">
            <input type="hidden" name="id" value="${category.id}" />

            <div class="mb-4">
                <label for="title" class="form-label fw-medium text-secondary">Tên danh mục</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-tag-fill"></i></span>
                    <input type="text" class="form-control rounded-end-3 border-start-0 ps-0"
                           name="title" id="title" value="<c:out value='${category.title}'/>"
                           placeholder="Nhập tên danh mục..." required />
                </div>
            </div>

            <div class="mb-4">
                <label for="description" class="form-label fw-medium text-secondary">Mô tả chi tiết</label>
                <textarea class="form-control rounded-3" name="description" id="description"
                          rows="4" placeholder="Nhập mô tả cho danh mục đồ uống này..." required><c:out value='${category.description}'/></textarea>
            </div>

            <div class="d-grid gap-2 d-sm-flex justify-content-sm-end mt-4">
                <a href="${pageContext.request.contextPath}/admin/category/manage" class="btn btn-light rounded-pill px-4 text-secondary border">
                    <i class="bi bi-arrow-left me-1"></i>Quay lại
                </a>
                <button type="submit" class="btn btn-update rounded-pill px-4 fw-semibold shadow-sm">
                    <i class="bi bi-check-lg me-1"></i>Cập nhật ngay
                </button>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>