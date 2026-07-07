<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Boba Station - Chỉnh sửa sản phẩm</title>
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
        .current-img-preview {
            border: 2px solid #f1f2f6;
            border-radius: 10px;
            padding: 5px;
            background-color: #fafafa;
            max-width: 120px;
            object-fit: cover;
        }
    </style>
</head>
<body>

<div class="form-card m-3 shadow-sm">
    <div class="card-header-custom">
        <h3 class="fw-bold m-0"><i class="bi bi-pencil-square me-2"></i>Chỉnh sửa sản phẩm</h3>
        <p class="small text-white-50 m-0 mt-1">Cập nhật thông tin chi tiết mặt hàng trong thực đơn</p>
    </div>

    <div class="card-body-custom">
        <form action="${pageContext.request.contextPath}/admin/product/update" method="post" enctype="multipart/form-data">
            <input type="hidden" name="id" value="${product.id}">

            <div class="mb-3">
                <label for="name" class="form-label fw-medium text-secondary">Tên sản phẩm</label>
                <div class="input-group">
                    <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-cup-hot-fill"></i></span>
                    <input type="text" id="name" name="name" class="form-control rounded-end-3 border-start-0 ps-0" value="<c:out value='${product.name}'/>" placeholder="Nhập tên trà sữa/đồ uống..." required>
                </div>
            </div>

            <div class="mb-3">
                <label for="category_id" class="form-label fw-medium text-secondary">Danh mục thực đơn</label>
                <select id="category_id" name="category_id" class="form-select rounded-3" required>
                    <c:forEach var="category" items="${categories}">
                        <option value="${category.id}" ${product.category.id == category.id ? 'selected' : ''}>
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
                        <input type="number" id="price" name="price" class="form-control rounded-end-3 border-start-0 ps-0" step="1" value="${product.price}" placeholder="Ví dụ: 35000" required>
                    </div>
                </div>

                <div class="col-md-6 mb-3">
                    <label for="stock" class="form-label fw-medium text-secondary">Số lượng tồn kho</label>
                    <div class="input-group">
                        <span class="input-group-text bg-light text-secondary border-end-0"><i class="bi bi-boxes"></i></span>
                        <input type="number" id="stock" name="stock" class="form-control rounded-end-3 border-start-0 ps-0" value="${product.stock}" min="0" required>
                    </div>
                </div>
            </div>

            <div class="mb-3">
                <label for="description" class="form-label fw-medium text-secondary">Mô tả sản phẩm</label>
                <textarea id="description" name="description" class="form-control rounded-3" rows="3" placeholder="Nhập thành phần, hương vị đồ uống..." required><c:out value='${product.description}'/></textarea>
            </div>

            <div class="mb-4">
                <label for="photo" class="form-label fw-medium text-secondary">Hình ảnh sản phẩm</label>
                <input type="file" id="photo" name="photo" class="form-control rounded-3" accept="image/*">
                <div class="form-text text-muted mb-2">Để trống tệp tin nếu bạn muốn giữ nguyên hình ảnh cũ.</div>

                <c:if test="${not empty product.photo}">
                    <div class="d-flex align-items-center gap-3 mt-3 p-2 border rounded bg-light">
                        <img src="${pageContext.request.contextPath}/image/product/${product.photo}" alt="Current Product Image" class="current-img-preview shadow-sm" onerror="this.src='${pageContext.request.contextPath}/image/product/no-sample.png';">
                        <div>
                            <span class="d-block small text-secondary fw-semibold">Ảnh hiện tại</span>
                            <span class="small text-muted text-break"><c:out value="${product.photo}"/></span>
                        </div>
                    </div>
                </c:if>
            </div>

            <div class="d-grid gap-2 d-sm-flex justify-content-sm-end mt-4">
                <a href="${pageContext.request.contextPath}/admin/product/manage" class="btn btn-light rounded-pill px-4 text-secondary border">
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