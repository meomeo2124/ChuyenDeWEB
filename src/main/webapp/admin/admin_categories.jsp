<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- ĐỔI SANG TAGLIB JAKARTA CHUẨN --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Danh sách danh mục</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h1 class="text-center mb-4">Danh sách danh mục</h1>

    <%-- VÙNG THÔNG BÁO ALERT MSG / ERROR --%>
    <c:if test="${not empty msg}">
        <div class="alert alert-success text-center">${msg}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger text-center">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/admin/category/insert" method="post" class="mb-4 p-4 border rounded bg-light">
        <h4 class="mb-3">Thêm danh mục mới</h4>
        <div class="mb-3">
            <label for="title" class="form-label">Tên danh mục</label>
            <input type="text" class="form-control" name="title" id="title" required />
        </div>
        <div class="mb-3">
            <label for="description" class="form-label">Mô tả</label>
            <textarea class="form-control" name="description" id="description" required></textarea>
        </div>
        <button type="submit" class="btn btn-primary">Thêm danh mục</button>
        <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-secondary ms-2">Về Dashboard</a>
    </form>

    <table class="table table-bordered table-striped align-middle">
        <thead class="table-dark">
        <tr>
            <th style="width: 10%;">ID</th>
            <th style="width: 25%;">Tên danh mục</th>
            <th style="width: 45%;">Mô tả</th>
            <th style="width: 20%;">Hành động</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="category" items="${categoryList}">
            <tr>
                <td>${category.id}</td>
                <td><strong><c:out value="${category.title}"/></strong></td>
                <td><c:out value="${category.description}"/></td>
                <td>
                    <div class="d-flex gap-2">
                            <%-- LINK SỬA CHUẨN SPRING --%>
                        <a href="${pageContext.request.contextPath}/admin/category/edit?id=${category.id}" class="btn btn-warning btn-sm">Sửa</a>

                            <%-- SỬA THÀNH POST FORM ĐỂ XÓA AN TOÀN --%>
                        <form action="${pageContext.request.contextPath}/admin/category/delete" method="post" onsubmit="return confirm('Bạn chắc chắn muốn xóa danh mục này?');" style="margin:0;">
                            <input type="hidden" name="id" value="${category.id}" />
                            <button type="submit" class="btn btn-danger btn-sm">Xóa</button>
                        </form>
                    </div>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty categoryList}">
            <tr>
                <td colspan="4" class="text-center text-muted">Chưa có danh mục nào được khởi tạo.</td>
            </tr>
        </c:if>
        </tbody>
    </table>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>