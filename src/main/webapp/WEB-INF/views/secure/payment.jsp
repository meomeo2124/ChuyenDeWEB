<%@ page pageEncoding="UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<%@ include file="/template/includes/headerResource.jsp" %>
	<title>Thanh Toán</title>
</head>
<body>
<%@ include file="/template/includes/navbar.jsp" %>

<c:if test="${empty sessionScope.user}">
	<div class="container py-5">
		<div class="alert alert-danger">Vui lòng đăng nhập để thanh toán.</div>
		<a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Đăng nhập</a>
	</div>
</c:if>

<c:if test="${empty sessionScope.cart.items}">
	<div class="container py-5">
		<div class="alert alert-warning">Giỏ hàng của bạn đang trống.</div>
		<a href="${pageContext.request.contextPath}/Homepage" class="btn btn-primary">Tiếp tục mua sắm</a>
	</div>
</c:if>

<c:if test="${not empty sessionScope.user and not empty sessionScope.cart.items}">
	<section class="container py-5">
		<h2>Thanh Toán</h2>
		<c:if test="${not empty param.error}">
			<div class="alert alert-danger">${param.error}</div>
		</c:if>
		<div class="row">
			<div class="col-md-6">
				<h3>Thông tin đơn hàng</h3>
				<table class="table table-bordered">
					<thead>
					<tr>
						<th>Sản phẩm</th>
						<th>Số lượng</th>
						<th>Giá</th>
						<th>Tổng</th>
					</tr>
					</thead>
					<tbody>
						<%-- SỬA ĐỔI: Sử dụng .values thay vì .values() để bảo đảm JSTL biên dịch mượt mà --%>
					<c:forEach var="cartItem" items="${sessionScope.cart.items.values}">
						<tr>
							<td>${cartItem.product.name}</td>
							<td>${cartItem.quantity}</td>
							<td><fmt:formatNumber value="${cartItem.product.price}" type="currency" currencyCode="VND"/></td>
							<td><fmt:formatNumber value="${cartItem.product.price * cartItem.quantity}" type="currency" currencyCode="VND"/></td>
						</tr>
					</c:forEach>
					<tr>
						<td colspan="3" class="text-right"><strong>Tạm tính:</strong></td>
						<td><fmt:formatNumber value="${sessionScope.cart.totalPrice}" type="currency" currencyCode="VND"/></td>
					</tr>
					<tr>
						<td colspan="3" class="text-right"><strong>Phí vận chuyển:</strong></td>
						<td><fmt:formatNumber value="${sessionScope.cart.totalPrice > 0 ? 10.00 : 0.00}" type="currency" currencyCode="VND"/></td>
					</tr>
					<tr>
						<td colspan="3" class="text-right"><strong>Tổng cộng:</strong></td>
						<td><fmt:formatNumber value="${sessionScope.cart.totalPrice + (sessionScope.cart.totalPrice > 0 ? 10.00 : 0.00)}" type="currency" currencyCode="VND"/></td>
					</tr>
					</tbody>
				</table>
			</div>
			<div class="col-md-6">
				<h3>Chọn Phương Thức Thanh Toán</h3>
				<form action="${pageContext.request.contextPath}/secure/payment" method="post">
					<input type="hidden" name="action" value="pay">
					<div class="form-check">
						<input class="form-check-input" type="radio" name="paymentMethod" value="cash_on_delivery" id="cash_on_delivery" checked>
						<label class="form-check-label" for="cash_on_delivery">Tiền mặt (Thanh toán khi nhận hàng)</label>
					</div>
					<div class="form-check">
						<input class="form-check-input" type="radio" name="paymentMethod" value="credit_card" id="credit_card">
						<label class="form-check-label" for="credit_card">Thẻ tín dụng</label>
					</div>
					<div class="form-check">
						<input class="form-check-input" type="radio" name="paymentMethod" value="paypal" id="paypal">
						<label class="form-check-label" for="paypal">PayPal</label>
					</div>
					<div class="form-check">
						<input class="form-check-input" type="radio" name="paymentMethod" value="momo" id="momo">
						<label class="form-check-label" for="momo">MoMo</label>
					</div>
					<div class="form-check">
						<input class="form-check-input" type="radio" name="paymentMethod" value="vnpay" id="vnpay">
						<label class="form-check-label" for="vnpay">VNPay</label>
					</div>
					<button type="submit" class="btn btn-primary btn-lg mt-3">Thanh toán</button>
				</form>
			</div>
		</div>
	</section>
</c:if>

<%@ include file="/template/includes/footer.jsp" %>
</body>
</html>