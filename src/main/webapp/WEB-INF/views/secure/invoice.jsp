<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <%-- GIỮ NGUYÊN: Khối resource gốc của bạn để nhận CSS/JS chung của hệ thống --%>
    <%@ include file="/template/includes/headerResource.jsp"%>
    <title>Boba Station - Hóa đơn thanh toán</title>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f5f6fa;
            color: #2d3436;
        }
        .invoice-wrapper {
            padding: 50px 0;
            background-color: #f5f6fa;
        }
        .invoice-card {
            background: #ffffff;
            border: none;
            border-radius: 20px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.05);
            overflow: hidden;
            border: 1px solid #eef2f3;
        }
        .invoice-header {
            background: linear-gradient(135deg, #6c5ce7, #4834d4);
            color: #ffffff;
            padding: 40px;
            text-align: center;
        }
        .invoice-body {
            padding: 40px;
        }
        .table-invoice thead th {
            background-color: #f8f9fa;
            color: #636e72;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 11px;
            letter-spacing: 0.5px;
            border-bottom: 2px solid #eaeded;
            padding: 12px;
        }
        .table-invoice tbody td {
            padding: 16px 12px;
            border-bottom: 1px solid #f1f2f6;
        }
        .cost-summary {
            background-color: #f8f9fa;
            border-radius: 12px;
            padding: 20px;
            border: 1px solid #f1f2f6;
        }
        .btn-action {
            border-radius: 30px;
            padding: 10px 24px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
    </style>
</head>
<body>

<%@ include file="/template/includes/navbar.jsp"%>

<%-- TRƯỜNG HỢP 1: CHƯA ĐĂNG NHẬP --%>
<c:if test="${empty sessionScope.user}">
    <div class="container py-5 my-5 text-center" style="max-width: 500px;">
        <div class="card p-4 shadow-sm border-0 rounded-4">
            <div class="text-danger fs-1 mb-3"><i class="bi bi-exclamation-octagon-fill"></i></div>
            <h5 class="fw-bold mb-2">Yêu cầu xác thực tài khoản</h5>
            <p class="text-muted small mb-4">Vui lòng đăng nhập vào hệ thống Boba Station để xem chi tiết hóa đơn này.</p>
            <a href="${pageContext.request.contextPath}/login" class="btn btn-primary btn-action w-100" style="background-color: #6c5ce7; border-color: #6c5ce7;">Đăng nhập ngay</a>
        </div>
    </div>
</c:if>

<%-- TRƯỜNG HỢP 2: ĐÃ ĐĂNG NHẬP NHƯNG KHÔNG TÌM THẤY ĐƠN HÀNG --%>
<c:if test="${not empty sessionScope.user and empty order}">
    <div class="container py-5 my-5 text-center" style="max-width: 500px;">
        <div class="card p-4 shadow-sm border-0 rounded-4">
            <div class="text-warning fs-1 mb-3"><i class="bi bi-search"></i></div>
            <h5 class="fw-bold mb-2">Không tìm thấy hóa đơn</h5>
            <p class="text-muted small mb-4">Mã hóa đơn không tồn tại hoặc đã bị hủy khỏi hệ thống lưu trữ.</p>
            <a href="${pageContext.request.contextPath}/Homepage" class="btn btn-outline-secondary btn-action w-100">Tiếp tục mua sắm</a>
        </div>
    </div>
</c:if>

<%-- TRƯỜNG HỢP 3: THỎA MÃN ĐẦY ĐỦ ĐIỀU KIỆN - HIỂN THỊ HÓA ĐƠN ĐIỆN TỬ CAO CẤP --%>
<c:if test="${not empty sessionScope.user and not empty order}">
    <section class="invoice-wrapper">
        <div class="container">
            <div class="row justify-content-center">
                <div class="col-md-10 col-lg-8">
                    <div class="invoice-card">

                        <div class="invoice-header">
                            <h2 class="fw-bold m-0"><i class="bi bi-check2-circle me-2"></i>Giao dịch hoàn tất</h2>
                            <p class="text-white-50 m-0 mt-2">Đơn hàng của bạn đã được ghi nhận vào hệ thống kho Boba Station</p>
                        </div>

                        <div class="invoice-body">
                            <c:if test="${not empty msg}">
                                <div class="alert alert-info border-0 shadow-sm rounded-3 mb-4 text-center small">
                                    <i class="bi bi-info-circle-fill me-2"></i>${msg}
                                </div>
                            </c:if>

                            <div class="row g-4 mb-4">
                                <div class="col-sm-6">
                                    <span class="text-muted small text-uppercase d-block mb-1" style="letter-spacing: 0.5px;">Mã số hóa đơn</span>
                                    <span class="fw-bold fs-5 text-dark">#${order.id}</span>
                                </div>
                                <div class="col-sm-6 text-sm-end">
                                    <span class="text-muted small text-uppercase d-block mb-1" style="letter-spacing: 0.5px;">Phương thức thanh toán</span>
                                    <span class="fw-semibold text-secondary">
                                        <c:choose>
                                            <c:when test="${order.paymentMethod eq 'cash_on_delivery' or order.paymentMethod eq 'CASH'}">Tiền mặt (Thanh toán khi nhận hàng)</c:when>
                                            <c:when test="${order.paymentMethod eq 'vnpay' or order.paymentMethod eq 'VNPAY'}">Cổng điện tử VNPay</c:when>
                                            <c:when test="${order.paymentMethod eq 'vietqr' or order.paymentMethod eq 'VIETQR'}">Chuyển khoản nhanh VietQR</c:when>
                                            <c:when test="${order.paymentMethod eq 'credit_card'}">Thẻ tín dụng</c:when>
                                            <c:when test="${order.paymentMethod eq 'paypal'}">Tài khoản PayPal</c:when>
                                            <c:when test="${order.paymentMethod eq 'momo'}">Ví điện tử MoMo</c:when>
                                            <c:otherwise>Thanh toán qua thẻ</c:otherwise>
                                        </c:choose>
                                    </span>
                                </div>
                            </div>

                            <hr class="text-muted opacity-25 my-4">

                            <h6 class="fw-bold text-uppercase text-secondary mb-3 small" style="letter-spacing: 1px;"><i class="bi bi-cup-hot-fill me-2 text-primary"></i>Chi tiết thực đơn đặt hàng</h6>
                            <div class="table-responsive mb-4">
                                <table class="table table-invoice align-middle">
                                    <thead>
                                    <tr>
                                        <th scope="col" style="width: 50%;">Tên đồ uống / Topping</th>
                                        <th scope="col" class="text-center" style="width: 20%;">Số lượng</th>
                                        <th scope="col" class="text-end" style="width: 30%;">Đơn giá</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                        <%-- Khởi tạo biến tính tổng tiền hàng tạm tính --%>
                                    <c:set var="subTotal" value="0" />
                                    <c:forEach var="item" items="${order.items}">
                                        <c:set var="subTotal" value="${subTotal + (item.price * item.quantity)}" />
                                        <tr>
                                            <td>
                                                <span class="fw-semibold text-dark"><c:out value="${item.productName}"/></span>
                                            </td>
                                            <td class="text-center fw-medium text-secondary">${item.quantity} Ly</td>
                                            <td class="text-end fw-bold text-dark">
                                                <fmt:formatNumber value="${item.price * item.quantity}" pattern="#,###"/> đ
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>

                            <div class="d-flex flex-column flex-sm-row justify-content-center gap-3 pt-2">
                                <a href="${pageContext.request.contextPath}/" class="btn btn-outline-secondary btn-action px-4">
                                    <i class="bi bi-arrow-left me-2"></i>Tiếp tục mua sắm
                                </a>
                                <a href="${pageContext.request.contextPath}/secure/generateInvoicePDF?orderId=${order.id}" class="btn btn-primary btn-action px-4 shadow-sm" style="background-color: #6c5ce7; border-color: #6c5ce7;">
                                    <i class="bi bi-file-earmark-pdf me-2"></i>Tải hóa đơn (PDF)
                                </a>
                            </div>

                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</c:if>

<%-- GIỮ NGUYÊN: Chân trang hệ thống gốc --%>
<%@ include file="/template/includes/footer.jsp"%>

</body>
</html>