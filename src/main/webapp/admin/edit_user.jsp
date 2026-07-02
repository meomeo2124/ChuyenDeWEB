<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%-- SỬA CHUẨN TAGLIB JAKARTA --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chỉnh sửa thông tin người dùng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
</head>
<body>
<div class="container mt-5">
    <h2 class="text-center">Chỉnh sửa thông tin người dùng</h2>

    <%-- SỬA: Action trỏ đến /admin/user/update --%>
    <form action="${pageContext.request.contextPath}/admin/user/update" method="post">
        <%-- SỬA: Đổi name từ 'id' thành 'user_id' cho khớp với Controller --%>
        <input type="hidden" name="user_id" value="${user.id}">

        <div class="mb-3">
            <label for="username" class="form-label">Tên người dùng</label>
            <input type="text" class="form-control" id="username" name="username" value="${user.username}" required>
        </div>

        <div class="mb-3">
            <label for="email" class="form-label">Email</label>
            <input type="email" class="form-control" id="email" name="email" value="${user.email}" required>
        </div>

        <div class="mb-3">
            <label for="phone" class="form-label">Số điện thoại</label>
            <input type="tel" class="form-control" id="phone" name="phone" value="${user.phone}" required>
        </div>

        <div class="mb-3">
            <label for="address" class="form-label">Địa chỉ</label>
            <input type="text" class="form-control" id="address" name="address" value="${user.address}" required>
        </div>

        <div class="text-center">
            <button type="submit" class="btn btn-primary">Cập nhật</button>
            <%-- SỬA: Quay lại đúng Endpoint quản lý của Spring --%>
            <a href="${pageContext.request.contextPath}/admin/user/manage" class="btn btn-secondary">Quay lại</a>
        </div>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
</body>
</html>