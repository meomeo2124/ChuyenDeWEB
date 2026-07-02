<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sửa danh mục</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5" style="max-width: 600px;">
    <h1 class="text-center mb-4">Sửa danh mục</h1>

    <form action="${pageContext.request.contextPath}/admin/category/update" method="post" class="p-4 border rounded bg-light">
        <input type="hidden" name="id" value="${category.id}" />

        <div class="mb-3">
            <label for="title" class="form-label">Tên danh mục</label>
            <input type="text" class="form-control" name="title" id="title" value="${category.title}" required />
        </div>
        <div class="mb-3">
            <label for="description" class="form-label">Mô tả</label>
            <textarea class="form-control" name="description" id="description" required>${category.description}</textarea>
        </div>

        <div class="text-center mt-4">
            <button type="submit" class="btn btn-primary">Cập nhật danh mục</button>
            <a href="${pageContext.request.contextPath}/admin/category/manage" class="btn btn-secondary">Quay lại</a>
        </div>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>