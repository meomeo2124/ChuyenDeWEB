<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Boba Station - Quản lý đơn hàng</title>
  <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

  <style>
    :root {
      --primary-color: #6c5ce7;
      --secondary-color: #a29bfe;
      --dark-bg: #1e1e24;
      --light-card: #ffffff;
      --text-main: #2d3436;
    }
    body {
      font-family: 'Inter', sans-serif;
      background-color: #f8f9fa;
      color: var(--text-main);
      overflow-x: hidden;
    }
    /* Top Header Navigation */
    .admin-header {
      background: linear-gradient(135deg, #6c5ce7, #4834d4);
      color: #fff;
      padding: 15px 30px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    }
    .admin-header .logo {
      font-size: 22px;
      font-weight: 700;
      letter-spacing: 1px;
    }
    /* Side Navigation */
    .admin-nav {
      background-color: #fff;
      border-right: 1px solid #e0e0e0;
      min-height: calc(100vh - 72px);
      padding-top: 20px;
    }
    .admin-nav ul {
      list-style: none;
      padding: 0;
      margin: 0;
    }
    .admin-nav ul li a {
      display: flex;
      align-items: center;
      padding: 14px 25px;
      color: #636e72;
      text-decoration: none;
      font-weight: 500;
      transition: all 0.3s ease;
      border-left: 4px solid transparent;
    }
    .admin-nav ul li a:hover, .admin-nav ul li a.active {
      background-color: #f1f2f6;
      color: var(--primary-color);
      border-left-color: var(--primary-color);
    }
    .admin-nav ul li a .icon {
      margin-right: 15px;
      font-size: 18px;
    }
    /* Modernized Elegant Table Container */
    .table-container {
      background: #fff;
      border-radius: 16px;
      padding: 25px;
      box-shadow: 0 10px 30px rgba(0,0,0,0.04);
    }
    .badge-custom {
      padding: 6px 12px;
      border-radius: 30px;
      font-weight: 500;
      font-size: 12px;
    }
  </style>
</head>
<body>

<header class="admin-header">
  <div class="logo"><i class="bi bi-cup-straw me-2"></i>Admin Boba Station</div>
  <div class="d-flex align-items-center gap-3">
    <span class="badge bg-white text-dark p-2"><i class="bi bi-person-badge-fill me-1"></i>Hệ thống Quản trị</span>
  </div>
</header>

<div class="container-fluid">
  <div class="row">
    <nav class="col-md-3 col-lg-2 d-none d-md-block admin-nav">
      <ul>
        <li><a href="${pageContext.request.contextPath}/admin/dashboard"><span class="icon"><i class="bi bi-speedometer2"></i></span><span class="text">Tổng quan</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/product/manage"><span class="icon"><i class="bi bi-box-seam"></i></span><span class="text">Sản phẩm</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/user/manage"><span class="icon"><i class="bi bi-people"></i></span><span class="text">Người dùng</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/category/manage"><span class="icon"><i class="bi bi-tags"></i></span><span class="text">Danh mục</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/order/manage" class="active"><span class="icon"><i class="bi bi-receipt"></i></span><span class="text">Đơn hàng</span></a></li>
        <li><a href="${pageContext.request.contextPath}/admin/statistics"><span class="icon"><i class="bi bi-bar-chart-line"></i></span><span class="text">Thống kê doanh thu</span></a></li>
        <li class="mt-4"><hr class="text-muted"></li>
        <li><a href="${pageContext.request.contextPath}/logout" class="text-danger"><span class="icon"><i class="bi bi-box-arrow-right"></i></span><span class="text">Đăng xuất</span></a></li>
      </ul>
    </nav>

    <main class="col-md-9 ms-sm-auto col-lg-10 px-md-4 py-4">
      <div class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pt-3 pb-2 mb-3 border-bottom">
        <div>
          <h1 class="h2 fw-bold m-0">Quản lý Đơn hàng</h1>
          <p class="text-muted mt-1">Theo dõi, duyệt và điều phối hóa đơn trà sữa toàn hệ thống</p>
        </div>
      </div>

      <%-- KHỐI HIỂN THỊ THÔNG BÁO ALERT ĐÃ SỬA SANG PARAM ĐỒNG BỘ --%>
      <c:if test="${not empty param.success}">
        <div class="alert alert-success border-0 shadow-sm rounded-3 alert-dismissible fade show" role="alert">
          <i class="bi bi-check-circle-fill me-2"></i>${param.success}
          <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
      </c:if>
      <c:if test="${not empty param.error}">
        <div class="alert alert-danger border-0 shadow-sm rounded-3 alert-dismissible fade show" role="alert">
          <i class="bi bi-exclamation-triangle-fill me-2"></i>${param.error}
          <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
      </c:if>

      <div class="table-container">
        <h5 class="fw-bold mb-4"><i class="bi bi-layers-half text-primary me-2"></i>Lịch sử giao dịch gần đây</h5>

        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead class="table-light text-secondary">
            <tr>
              <th scope="col" class="text-center" style="width: 10%;">Mã đơn</th>
              <th scope="col" style="width: 25%;">Địa chỉ nhận hàng</th>
              <th scope="col" style="width: 20%;">Tổng thanh toán</th>
              <th scope="col" class="text-center" style="width: 15%;">Trạng thái</th>
              <th scope="col" class="text-center" style="width: 15%;">Ngày đặt đơn</th>
              <th scope="col" class="text-center" style="width: 15%;">Thao tác</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="order" items="${orderList}">
              <tr>
                <td class="text-center">
                  <span class="badge bg-light text-dark border p-2">#${order.id}</span>
                </td>
                <td>
                  <span class="fw-medium text-dark"><c:out value="${order.shippingAddress}"/></span>
                </td>
                <td>
                  <span class="text-danger fw-bold"><fmt:formatNumber value="${order.totalPrice}" pattern="#,###" /> VNĐ</span>
                </td>
                <td class="text-center">
                  <c:choose>
                    <c:when test="${order.status eq 'PAID'}">
                      <span class="badge-custom bg-success-subtle text-success"><i class="bi bi-patch-check-fill me-1"></i>Đã thanh toán</span>
                    </c:when>
                    <c:when test="${order.status eq 'PENDING'}">
                      <span class="badge-custom bg-warning-subtle text-warning"><i class="bi bi-hourglass-split me-1"></i>Chờ duyệt</span>
                    </c:when>
                    <c:otherwise>
                      <span class="badge-custom bg-danger-subtle text-danger"><i class="bi bi-x-circle-fill me-1"></i>Đã hủy đơn</span>
                    </c:otherwise>
                  </c:choose>
                </td>
                <td class="text-center text-muted small">
                  <fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm:ss"/>
                </td>
                <td>
                  <div class="d-flex justify-content-center gap-1">
                      <%-- NÚT DUYỆT ĐƠN --%>
                    <c:if test="${order.status eq 'PENDING'}">
                      <form action="${pageContext.request.contextPath}/admin/order/updateStatus" method="POST" style="margin: 0;">
                        <input type="hidden" name="id" value="${order.id}">
                        <input type="hidden" name="status" value="PAID">
                        <button type="submit" class="btn btn-sm btn-success rounded-pill px-2" onclick="return confirm('Xác nhận đã thu đủ tiền cho đơn #${order.id}?');">
                          <i class="bi bi-check-lg"></i> Duyệt
                        </button>
                      </form>
                    </c:if>

                      <%-- NÚT HỦY ĐƠN --%>
                    <c:if test="${order.status ne 'CANCELLED'}">
                      <form action="${pageContext.request.contextPath}/admin/order/updateStatus" method="POST" style="margin: 0;">
                        <input type="hidden" name="id" value="${order.id}">
                        <input type="hidden" name="status" value="CANCELLED">
                        <button type="submit" class="btn btn-sm btn-outline-danger rounded-pill px-2" onclick="return confirm('Bạn có chắc muốn HỦY đơn #${order.id}?');">
                          <i class="bi bi-trash"></i> Hủy
                        </button>
                      </form>
                    </c:if>
                  </div>
                </td>
              </tr>
            </c:forEach>
            <c:if test="${empty orderList}">
              <tr>
                <td colspan="6" class="text-center text-muted py-4">Hệ thống chưa có đơn hàng nào phát sinh.</td>
              </tr>
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