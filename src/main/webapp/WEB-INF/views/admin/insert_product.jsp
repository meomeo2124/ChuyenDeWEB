<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Boba Station - Thêm sản phẩm mới</title>
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
            max-width: 600px;
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
        .form-control:focus, .form-select:focus {
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

<div class="form-card m-3 shadow-sm">
    <div class="card-header-custom">
        <h3 class="fw-bold m-0"><i class="bi bi-plus-circle-fill me-2"></i>Thêm sản phẩm mới</h3>
        <p class="small text-white-50 m-0 mt-1">Khởi tạo nguyên liệu, thức uống mới vào thực đơn hệ thống</p>
    </div>

    <div class="card-body-custom">
        <%-- THÔNG BÁO LỖI NẾU CÓ --%>
        <c:if test="${not empty error}">
            <div class="alert alert-danger border-0 shadow-sm rounded-3 text-center mb-4">
                <i class="bi bi-exclamation-triangle-fill me-2"></i>${error}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/product/insert" method="post" enctype="multipart/form-data">

            <div class="mb-3">
                <label for="name" class="form-label fw-medium text-secondary">Tên sản phẩm</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-cup-hot-fill"></i></span>
                    <input type="text" id="name" name="name" class="form-control rounded-end-3 border-start-0 ps-0" placeholder="Ví dụ: Trà sữa Trân châu Đường đen" required>
                </div>
            </div>

            <div class="mb-3">
                <label for="category_id" class="form-label fw-medium text-secondary">Thuộc danh mục</label>
                <select id="category_id" name="category_id" class="form-select rounded-3" required>
                    <option value="" disabled selected hidden>-- Chọn danh mục thực đơn --</option>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category.id}">
                            <c:out value="${category.title}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label for="price" class="form-label fw-medium text-secondary">Giá bán (VNĐ)</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-cash-coin"></i></span>
                        <input type="number" id="price" name="price" class="form-control rounded-end-3 border-start-0 ps-0" step="1" placeholder="Ví dụ: 39000" required>
                    </div>
                </div>

                <div class="col-md-6 mb-3">
                    <label for="stock" class="form-label fw-medium text-secondary">Số lượng nhập kho</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-boxes"></i></span>
                        <input type="number" id="stock" name="stock" class="form-control rounded-end-3 border-start-0 ps-0" value="0" min="0" required>
                    </div>
                </div>
            </div>

            <div class="mb-3">
                <label for="description" class="form-label fw-medium text-secondary">Mô tả đồ uống</label>
                <textarea id="description" name="description" class="form-control rounded-3" rows="3" placeholder="Nhập hương vị, topping đi kèm mặc định của sản phẩm..." required></textarea>
            </div>

            <div class="mb-4">
                <label for="photo" class="form-label fw-medium text-secondary">Hình ảnh đại diện</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-image-fill"></i></span>
                    <input type="file" id="photo" name="photo" class="form-control rounded-end-3 border-start-0" accept="image/*" required>
                </div>
                <div class="form-text text-muted">Hệ thống chấp nhận các định dạng tệp tin hình ảnh tiêu chuẩn (.png, .jpg, .jpeg)</div>
            </div>

            <div class="d-grid gap-2 d-sm-flex justify-content-sm-end mt-4">
                <a href="${pageContext.request.contextPath}/admin/product/manage" class="btn btn-light rounded-pill px-4 text-secondary border">
                    <i class="bi bi-arrow-left me-1"></i>Quay lại
                </a>
                <button type="submit" class="btn btn-insert rounded-pill px-4 fw-semibold shadow-sm">
                    <i class="bi bi-plus-lg me-1"></i>Thêm vào kho
                </button>
            </div>
        </form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>