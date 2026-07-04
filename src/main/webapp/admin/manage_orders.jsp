<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%-- Ép kiểu Tiếng Việt để hiển thị tiền tệ chuẩn --%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Quản lý Đơn hàng - Admin</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.8.1/font/bootstrap-icons.css">
</head>
<body class="bg-light">
<div class="container mt-5">
  <div class="card shadow">
    <div class="card-header bg-dark text-white d-flex justify-content-between align-items-center">
      <h3 class="mb-0">Danh sách đơn hàng hệ thống</h3>
      <a href="${pageContext.request.contextPath}/admin/dashboard" class="btn btn-sm btn-secondary">Về Dashboard</a>
    </div>
    <div class="card-body">

      <%-- Khối hiển thị thông báo Alert (Thành công/Thất bại) từ Controller --%>
      <c:if test="${not empty param.success}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${param.success}
          <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
      </c:if>
      <c:if test="${not empty param.error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${param.error}
          <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
      </c:if>

      <table class="table table-bordered table-hover align-middle">
        <thead class="table-secondary text-center">
        <tr>
          <th>Mã đơn</th>
          <th>Tên tài khoản</th>
          <th>Tổng thanh toán</th>
          <th>Trạng thái</th>
          <th>Ngày đặt đơn</th>
          <th>Thao tác</th> </tr>
        </thead>
        <tbody>
        <c:forEach var="order" items="${orderList}">
          <tr>
            <td class="text-center"><strong>#${order.id}</strong></td>
            <td><c:out value="${order.shippingAddress}"/></td>
            <td class="text-danger fw-bold text-end">
                <%-- Định dạng số VNĐ --%>
              <fmt:formatNumber value="${order.totalPrice}" pattern="#,###" /> VNĐ
            </td>
            <td class="text-center">
              <c:choose>
                <c:when test="${order.status eq 'PAID'}">
                  <span class="badge bg-success">Đã thanh toán</span>
                </c:when>
                <c:when test="${order.status eq 'PENDING'}">
                  <span class="badge bg-warning text-dark">Chờ duyệt / Chờ thanh toán</span>
                </c:when>
                <c:otherwise>
                  <span class="badge bg-danger">Đã hủy / Thất bại</span>
                </c:otherwise>
              </c:choose>
            </td>
            <td class="text-center"><fmt:formatDate value="${order.orderDate}" pattern="dd/MM/yyyy HH:mm:ss"/></td>
            <td>
              <div class="d-flex justify-content-center gap-2">
                  <%-- NÚT DUYỆT ĐƠN: Chỉ hiện khi trạng thái là PENDING --%>
                <c:if test="${order.status eq 'PENDING'}">
                  <form action="${pageContext.request.contextPath}/admin/order/updateStatus" method="POST" style="margin: 0;">
                    <input type="hidden" name="id" value="${order.id}">
                    <input type="hidden" name="status" value="PAID">
                    <button type="submit" class="btn btn-sm btn-success" title="Duyệt đã nhận tiền" onclick="return confirm('Xác nhận đã thu đủ tiền cho đơn #${order.id}?');">
                      <i class="bi bi-check-circle"></i> Duyệt
                    </button>
                  </form>
                </c:if>

                  <%-- NÚT HỦY ĐƠN: Hiện khi trạng thái chưa bị Hủy --%>
                <c:if test="${order.status ne 'CANCELLED'}">
                  <form action="${pageContext.request.contextPath}/admin/order/updateStatus" method="POST" style="margin: 0;">
                    <input type="hidden" name="id" value="${order.id}">
                    <input type="hidden" name="status" value="CANCELLED">
                    <button type="submit" class="btn btn-sm btn-danger" title="Hủy đơn hàng" onclick="return confirm('Bạn có chắc muốn HỦY đơn #${order.id}?');">
                      <i class="bi bi-x-circle"></i> Hủy
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
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>