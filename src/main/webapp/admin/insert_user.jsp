<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%-- SỬA CHUẨN TAGLIB JAKARTA ĐỒNG BỘ SPRING 6 --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Thêm người dùng mới</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet" crossorigin="anonymous">
</head>
<body>
<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">
            <h1 class="my-4 text-center">Thêm người dùng mới</h1>

            <%-- SỬA: Action trỏ chính xác về POST Endpoint của AdminUserController --%>
            <form action="${pageContext.request.contextPath}/admin/user/insert" method="post">

                <div class="mb-3">
                    <label for="username" class="form-label">Tên người dùng</label>
                    <input type="text" class="form-control" id="username" name="username" required>
                </div>

                <div class="mb-3">
                    <label for="email" class="form-label">Email</label>
                    <input type="email" class="form-control" id="email" name="email" required>
                </div>

                <%-- BỔ SUNG: Trường nhập mật khẩu bắt buộc cho tài khoản mới --%>
                <div class="mb-3">
                    <label for="password" class="form-label">Mật khẩu</label>
                    <input type="password" class="form-control" id="password" name="password" required>
                </div>

                <div class="mb-3">
                    <label for="phone" class="form-label">Số điện thoại</label>
                    <input type="text" class="form-control" id="phone" name="phone" required>
                </div>

                <div class="mb-3">
                    <label for="address" class="form-label">Địa chỉ</label>
                    <input type="text" class="form-control" id="address" name="address" required>
                </div>

                <div class="text-center mt-4">
                    <button type="submit" class="btn btn-primary">Thêm người dùng</button>
                    <%-- BỔ SUNG: Nút quay lại trang danh sách chuẩn đường dẫn Spring --%>
                    <a href="${pageContext.request.contextPath}/admin/user/manage" class="btn btn-secondary">Quay lại</a>
                </div>
            </form>
        </div>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/js/bootstrap.bundle.min.js" crossorigin="anonymous"></script>
</body>
</html>