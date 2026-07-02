<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%-- CHUYỂN ĐỔI SANG CHUẨN TAGLIB JAKARTA ĐỒNG BỘ --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<header class="admin-header">
    <div class="logo">Admin Boba Station</div>
    <div class="header-right">
        <input type="text" class="search-bar" placeholder="Tìm kiếm...">
        <button class="add-btn" onclick="toggleSidebar()">Menu</button>
    </div>
</header>

<nav class="admin-nav">
    <ul>
        <%-- ĐỒNG BỘ ĐƯỜNG DẪN ĐẾN CÁC CONTROLLER SPRING MVC NEW --%>
        <li><a href="${pageContext.request.contextPath}/admin/product/manage"><span class="icon">🛠️</span><span class="text">Quản lý sản phẩm</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/user/manage"><span class="icon">👥</span><span class="text">Quản lý người dùng</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/category/manage"><span class="icon">📋</span><span class="text">Quản lý danh mục</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/statistics"><span class="icon">📈</span><span class="text">Thống kê</span></a></li>
        <li><a href="${pageContext.request.contextPath}/logout"><span class="icon">🚪</span><span class="text">Đăng xuất</span></a></li>
    </ul>
</nav>

<main class="admin-main">
    <h2 class="page-title">Admin Dashboard</h2>
    <p class="subtitle">"Giữ lửa năng lượng – Đánh tan cơn khát!"</p>

    <div class="stat-section">
        <div class="card">
            <h3>Tổng số sản phẩm</h3>
            <p class="value">${totalProducts}</p>
            <p class="trend">+10% so với tháng trước</p>
        </div>
        <div class="card">
            <h3>Tổng số người dùng</h3>
            <p class="value">${totalUsers}</p>
            <p class="trend">+5% so với tuần trước</p>
        </div>
        <div class="card">
            <h3>Doanh thu hệ thống</h3>
            <p class="value">${totalRevenue} VNĐ</p>
            <p class="trend">+7% so với hôm qua</p>
        </div>
    </div>

    <button class="view-report-btn">Xem báo cáo chi tiết</button>

    <div class="data-table">
        <h3>Danh sách sản phẩm hiện tại</h3>
        <table>
            <thead>
            <tr>
                <th>Hình ảnh</th>
                <th>Tên sản phẩm</th>
                <th>Giá bán</th>
                <th>Hành động</th>
            </tr>
            </thead>
            <tbody>
            <%-- SỬA ĐỔI: Sử dụng c:forEach để duyệt danh sách thực tế từ Database gửi lên thay vì dữ liệu cứng --%>
            <c:forEach var="p" items="${products}">
                <tr>
                    <td><img src="${pageContext.request.contextPath}/${p.img}" alt="Product" class="avatar" style="width:30px; height:30px; object-fit:cover;"></td>
                    <td><c:out value="${p.name}"/></td>
                    <td>${p.price} VNĐ</td>
                    <td>
                        <div class="btn-group">
                            <a href="${pageContext.request.contextPath}/admin/product/edit?id=${p.id}" class="btn btn-sm btn-primary">Sửa</a>
                        </div>
                    </td>
                </tr>
            </c:forEach>
            <c:if test="${empty products}">
                <tr>
                    <td colspan="4" class="text-center text-muted">Không có sản phẩm nào trong hệ thống.</td>
                </tr>
            </c:if>
            </tbody>
        </table>
    </div>
</main>

<script>
    function toggleSidebar() {
        document.querySelector('.admin-nav').classList.toggle('expanded');
        document.querySelector('.admin-main').classList.toggle('expanded');
    }
</script>
</body>
</html>