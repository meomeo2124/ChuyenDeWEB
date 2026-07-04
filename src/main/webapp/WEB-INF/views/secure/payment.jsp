<%@ page pageEncoding="UTF-8" language="java" %>
<%-- ĐỒNG BỘ SUITE TAGLIB JAKARTA CHUẨN --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<%@ include file="/template/includes/headerResource.jsp" %>
	<title>Thanh Toán - Boba Station</title>
</head>
<body class="bg-light">
<%@ include file="/template/includes/navbar.jsp" %>

<c:if test="${empty sessionScope.user}">
	<div class="container py-5 text-center">
		<div class="alert alert-danger d-inline-block">Vui lòng đăng nhập để thực hiện thanh toán.</div>
		<br>
		<a href="${pageContext.request.contextPath}/login" class="btn btn-primary">Đăng nhập ngay</a>
	</div>
</c:if>

<c:if test="${empty sessionScope.cart.items}">
	<div class="container py-5 text-center">
		<div class="alert alert-warning d-inline-block">Giỏ hàng của bạn đang trống.</div>
		<br>
		<a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Tiếp tục mua sắm</a>
	</div>
</c:if>

<c:if test="${not empty sessionScope.user and not empty sessionScope.cart.items}">
	<c:set var="TY_GIA" value="1" />

	<section class="container py-5">
		<h2 class="text-center mb-4">Thông Tin Thanh Toán</h2>

			<%-- Hiển thị thông báo lỗi truyền từ Controller nếu có --%>
		<c:if test="${not empty param.error}">
			<div class="alert alert-danger text-center">${param.error}</div>
		</c:if>

		<div class="row g-4">
			<div class="col-lg-7">
				<div class="card shadow-sm p-4 bg-white rounded">
					<h4 class="mb-3 text-secondary">Tóm tắt đơn hàng</h4>
					<table class="table table-striped align-middle">
						<thead class="table-dark">
						<tr>
							<th>Sản phẩm</th>
							<th class="text-center">Số lượng</th>
							<th class="text-end">Đơn giá</th>
							<th class="text-end">Tổng</th>
						</tr>
						</thead>
						<tbody>
						<c:forEach var="cartItem" items="${sessionScope.cart.items.values}">
							<tr>
								<td><c:out value="${cartItem.product.name}"/></td>
								<td class="text-center">${cartItem.quantity}</td>
								<td class="text-end">
									<fmt:formatNumber value="${cartItem.product.price * TY_GIA}" pattern="#,###"/> VNĐ
								</td>
								<td class="text-end fw-bold">
									<fmt:formatNumber value="${cartItem.product.price * cartItem.quantity * TY_GIA}" pattern="#,###"/> VNĐ
								</td>
							</tr>
						</c:forEach>

						<tr class="table-light">
							<td colspan="3" class="text-end"><strong>Tạm tính:</strong></td>
							<td class="text-end"><fmt:formatNumber value="${sessionScope.cart.totalPrice * TY_GIA}" pattern="#,###"/> VNĐ</td>
						</tr>
						<tr class="table-light">
							<td colspan="3" class="text-end"><strong>Phí vận chuyển:</strong></td>
							<td class="text-end">
								<fmt:formatNumber value="${(sessionScope.cart.totalPrice > 0 ? 10.00 : 0.00) * TY_GIA}" pattern="#,###"/> VNĐ
							</td>
						</tr>
						<tr class="table-info fs-5">
							<td colspan="3" class="text-end"><strong>Tổng cộng:</strong></td>
							<td class="text-end text-danger fw-bold">
								<fmt:formatNumber value="${(sessionScope.cart.totalPrice + (sessionScope.cart.totalPrice > 0 ? 10.00 : 0.00)) * TY_GIA}" pattern="#,###"/> VNĐ
							</td>
						</tr>
						</tbody>
					</table>
				</div>
			</div>

			<div class="col-lg-5">
				<div class="card shadow-sm p-4 bg-white rounded">
					<h4 class="mb-3 text-secondary">Phương thức thanh toán</h4>

					<form action="${pageContext.request.contextPath}/secure/payment" method="post">
						<input type="hidden" name="action" value="pay">

						<div class="d-flex flex-column gap-3 my-4">
							<div class="form-check p-3 border rounded shadow-sm">
								<input class="form-check-input ms-1" type="radio" name="paymentMethod" value="cash_on_delivery" id="cash_on_delivery" checked>
								<label class="form-check-label ms-2 fw-bold" for="cash_on_delivery">
									💵 Tiền mặt (Thanh toán khi nhận hàng - COD)
								</label>
							</div>

								<%-- ĐÃ DI CHUYỂN: Đưa mục chọn VietQR vào bên trong thẻ Form hợp lệ --%>
							<div class="form-check p-3 border border-info rounded shadow-sm bg-light">
								<input class="form-check-input ms-1" type="radio" name="paymentMethod" value="vietqr" id="vietqr">
								<label class="form-check-label ms-2 fw-bold text-info" for="vietqr">
									📸 Quét mã VietQR (Tự động hiển thị chính xác số tiền cần trả)
								</label>
							</div>

							<div class="form-check p-3 border rounded shadow-sm opacity-50">
								<input class="form-check-input ms-1" type="radio" name="paymentMethod" value="credit_card" id="credit_card" disabled>
								<label class="form-check-label ms-2" for="credit_card">💳 Thẻ tín dụng (Bảo trì)</label>
							</div>

							<div class="form-check p-3 border rounded shadow-sm opacity-50">
								<input class="form-check-input ms-1" type="radio" name="paymentMethod" value="paypal" id="paypal" disabled>
								<label class="form-check-label ms-2" for="paypal">🌐 PayPal (Bảo trì)</label>
							</div>

							<div class="form-check p-3 border rounded shadow-sm opacity-50">
								<input class="form-check-input ms-1" type="radio" name="paymentMethod" value="momo" id="momo" disabled>
								<label class="form-check-label ms-2" for="momo">🔮 Ví điện tử MoMo (Bảo trì)</label>
							</div>

							<div class="form-check p-3 border border-primary rounded shadow-sm bg-light">
								<input class="form-check-input ms-1" type="radio" name="paymentMethod" value="vnpay" id="vnpay">
								<label class="form-check-label ms-2 fw-bold text-primary" for="vnpay">
									🇻🇳 Cổng thanh toán trực tuyến VNPay (Thẻ ATM / Mobile Banking)
								</label>
							</div>
						</div>

						<div class="d-grid mt-4">
							<button type="submit" class="btn btn-success btn-lg shadow">Xác nhận đặt hàng</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</section>
</c:if>

<%@ include file="/template/includes/footer.jsp" %>
</body>
</html>