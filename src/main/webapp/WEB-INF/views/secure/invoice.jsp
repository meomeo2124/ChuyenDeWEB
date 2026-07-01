<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <%@ include file="/template/includes/headerResource.jsp"%>
    <title>Hóa Đơn</title>
</head>
<body>
<%@ include file="/template/includes/navbar.jsp"%>

<c:if test="${empty sessionScope.user}">
    <div class="container py-5">
        <div class="alert alert-danger">Vui lòng đăng nhập để xem hóa đơn.</div>
        <a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Đăng nhập</a>
    </div>
</c:if>

<c:if test="${empty order}">
    <div class="container py-5">
        <div class="alert alert-danger">Không tìm thấy hóa đơn.</div>
        <a href="${pageContext.request.contextPath}/Homepage" class="btn btn-primary">Tiếp tục mua sắm</a>
    </div>
</c:if>

<c:if test="${not empty sessionScope.user and not empty order}">
    <section style="background-color: #eee;">
        <div class="container py-5">
            <div class="row d-flex justify-content-center">
                <div class="col-md-8 col-lg-6 col-xl-4">
                    <div class="card rounded-3">
                        <div class="card-body mx-1 my-2">
                            <h2>Hóa Đơn</h2>
                            <p>Mã Hóa Đơn: ${order.id}</p>
                            <p>Phương Thức Thanh Toán:
                                <c:choose>
                                    <c:when test="${order.paymentMethod == 'cash_on_delivery'}">Tiền mặt (Thanh toán khi nhận hàng)</c:when>
                                    <c:when test="${order.paymentMethod == 'credit_card'}">Thẻ tín dụng</c:when>
                                    <c:when test="${order.paymentMethod == 'paypal'}">PayPal</c:when>
                                    <c:when test="${order.paymentMethod == 'momo'}">MoMo</c:when>
                                    <c:when test="${order.paymentMethod == 'vnpay'}">VNPay</c:when>
                                    <c:otherwise>Không xác định</c:otherwise>
                                </c:choose>
                            </p>
                            <p>Tổng Tiền: <fmt:formatNumber value="${order.totalPrice}" type="currency" currencyCode="VND"/></p>
                            <table class="table table-bordered">
                                <thead>
                                <tr>
                                    <th>Sản Phẩm</th>
                                    <th>Số Lượng</th>
                                    <th>Giá</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="item" items="${order.items}">
                                    <tr>
                                        <td>${item.productName}</td>
                                        <td>${item.quantity}</td>
                                        <td><fmt:formatNumber value="${item.price}" type="currency" currencyCode="VND"/></td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                            <a href="${pageContext.request.contextPath}/secure/generateInvoicePDF?orderId=${order.id}" class="btn btn-primary">Tải PDF</a>
                            <a href="${pageContext.request.contextPath}/Homepage" class="btn btn-secondary">Tiếp tục mua sắm</a>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</c:if>

<%@ include file="/template/includes/footer.jsp"%>
</body>
</html>