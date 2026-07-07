<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<%-- Kế thừa tài nguyên gốc hệ thống của bạn --%>
	<jsp:include page="/template/includes/headerResource.jsp" />
	<title>Boba Station - Xác nhận thanh toán</title>

	<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
	<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

	<style>
		body {
			font-family: 'Inter', sans-serif;
			background-color: #f8f9fa;
			color: #2d3436;
		}
		.pay-card {
			background: #ffffff;
			border: none;
			border-radius: 16px;
			box-shadow: 0 10px 30px rgba(0,0,0,0.04);
			border: 1px solid #eef2f3;
		}
		.method-select {
			border: 2px solid #edeff1;
			border-radius: 12px;
			padding: 16px;
			cursor: pointer;
			transition: all 0.25s ease;
			display: flex;
			align-items: center;
			margin-bottom: 12px;
		}
		.method-select:hover {
			border-color: #a29bfe;
			background-color: #fcfbff;
		}
		/* Style khi ô Radio được chọn */
		.method-select input[type="radio"]:checked + label,
		.method-select input[type="radio"]:checked {
			color: #6c5ce7;
		}
		.method-icon {
			font-size: 24px;
			margin-right: 15px;
			color: #6c5ce7;
		}
		.summary-box {
			background-color: #f8f9fa;
			border-radius: 12px;
			padding: 20px;
			border: 1px solid #f1f2f6;
		}
	</style>
</head>
<body>

<%-- Kế thừa thanh điều hướng gốc --%>
<%@ include file="/template/includes/navbar.jsp"%>

<div class="container py-5">
	<div class="row g-4">

		<div class="col-lg-7">
			<div class="card pay-card p-4 mb-4">
				<h5 class="fw-bold mb-4 text-dark"><i class="bi bi-geo-alt-fill text-primary me-2"></i>Thông tin nhận hàng</h5>
				<div class="mb-3">
					<label class="form-label text-secondary small fw-medium">Tên người nhận tài khoản</label>
					<input type="text" class="form-control bg-light" value="${sessionScope.user.username}" disabled>
				</div>
				<div class="mb-3">
					<label class="form-label text-secondary small fw-medium">Địa chỉ giao hàng thực tế</label>
					<input type="text" class="form-control" value="${sessionScope.user.address}" placeholder="Nhập địa chỉ nhận trà sữa cụ thể...">
				</div>
			</div>

			<div class="card pay-card p-4">
				<h5 class="fw-bold mb-4 text-dark"><i class="bi bi-wallet2 text-primary me-2"></i>Chọn phương thức thanh toán</h5>

				<form action="${pageContext.request.contextPath}/secure/payment" method="POST">
					<input type="hidden" name="action" value="pay">

					<div class="method-select">
						<input type="radio" name="paymentMethod" id="cash" value="CASH" checked class="form-check-input me-3">
						<div class="method-icon"><i class="bi bi-cash-coin"></i></div>
						<label for="cash" class="form-check-label fw-semibold text-dark mb-0 m-0">Thanh toán tiền mặt khi nhận hàng (COD)</label>
					</div>

					<div class="method-select">
						<input type="radio" name="paymentMethod" id="vietqr" value="vietqr" class="form-check-input me-3">
						<div class="method-icon"><i class="bi bi-qr-code-scan"></i></div>
						<label for="vietqr" class="form-check-label fw-semibold text-dark mb-0 m-0">Chuyển khoản nhanh qua mã QR (VietQR)</label>
					</div>

					<div class="method-select">
						<input type="radio" name="paymentMethod" id="vnpay" value="vnpay" class="form-check-input me-3">
						<div class="method-icon"><i class="bi bi-credit-card-2-front-fill"></i></div>
						<label for="vnpay" class="form-check-label fw-semibold text-dark mb-0 m-0">Cổng thanh toán điện tử VNPay</label>
					</div>

					<div class="mt-4 pt-2">
						<button type="submit" class="btn btn-primary w-100 rounded-pill py-2.5 fw-bold shadow-sm" style="background-color: #6c5ce7; border-color: #6c5ce7;">
							<i class="bi bi-shield-lock-fill me-2"></i>Xác nhận mua hàng & Thanh toán
						</button>
					</div>
				</form>
			</div>
		</div>

		<div class="col-lg-5">
			<div class="card pay-card p-4">
				<h5 class="fw-bold mb-4 text-dark"><i class="bi bi-bag-check-fill text-primary me-2"></i>Giỏ hàng thanh toán</h5>

				<div class="mb-4" style="max-height: 220px; overflow-y: auto;">
					<c:forEach var="item" items="${sessionScope.cart.items.values()}">
						<div class="d-flex align-items-center justify-content-between mb-3 pb-2 border-bottom border-light">
							<div>
								<span class="fw-semibold text-dark d-block text-truncate" style="max-width: 220px;"><c:out value="${item.product.name}"/></span>
								<span class="text-muted small">Số lượng: ${item.quantity} Ly</span>
							</div>
							<span class="fw-bold text-secondary"><fmt:formatNumber value="${item.product.price * item.quantity}" pattern="#,###"/> đ</span>
						</div>
					</c:forEach>
				</div>

				<div class="summary-box">
					<c:set var="cartTotal" value="${sessionScope.cart.totalPrice}" />
					<div class="d-flex justify-content-between text-secondary mb-2 small">
						<span>Tạm tính tổng tiền hàng:</span>
						<span class="fw-medium text-dark"><fmt:formatNumber value="${cartTotal}" pattern="#,###"/> đ</span>
					</div>
					<div class="d-flex justify-content-between text-secondary mb-3 small">
						<span>Phí vận chuyển giao hàng:</span>
						<span class="fw-medium text-dark"><fmt:formatNumber value="${cartTotal > 0 ? 15000 : 0}" pattern="#,###"/> đ</span>
					</div>
					<hr class="text-muted opacity-25 my-3">
					<div class="d-flex justify-content-between align-items-center">
						<strong class="text-dark">Tổng tiền thanh toán:</strong>
						<strong class="fs-4 text-danger fw-bold"><fmt:formatNumber value="${cartTotal + (cartTotal > 0 ? 15000 : 0)}" pattern="#,###"/> VNĐ</strong>
					</div>
				</div>
			</div>
		</div>

	</div>
</div>
<%@ include file="/template/includes/footer.jsp"%>

</body>
</html>