<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<jsp:include page="/template/includes/headerResource.jsp" />
	<title>Boba Station - Giỏ hàng của bạn</title>

	<link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
	<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

	<style>
		body {
			font-family: 'Inter', sans-serif;
			background-color: #f8f9fa;
			color: #2d3436;
		}
		.cart-item {
			transition: all 0.3s ease;
			padding: 15px 0;
		}
		.cart-item:hover {
			background-color: #fafafa;
		}
		.quantity-input {
			width: 50px;
			font-weight: 600;
		}
		/* Triệt tiêu nút tăng giảm mặc định */
		.quantity-input::-webkit-outer-spin-button,
		.quantity-input::-webkit-inner-spin-button {
			-webkit-appearance: none;
			margin: 0;
		}
		.card-custom {
			background: #ffffff;
			border: none;
			border-radius: 16px;
			box-shadow: 0 10px 30px rgba(0,0,0,0.03);
		}
		.btn-checkout {
			border-radius: 30px;
			padding: 12px;
			font-weight: 700;
			transition: all 0.3s ease;
		}
	</style>
</head>
<body>

<%@ include file="/template/includes/navbar.jsp"%>

<div class="container py-5">
	<div class="d-flex align-items-center mb-4">
		<h2 class="fw-bold m-0"><i class="bi bi-cart3 text-primary me-2"></i>Giỏ hàng đặt đồ</h2>
	</div>

	<div class="row g-4">
		<div class="col-lg-8">
			<div class="card card-custom p-3 mb-4">
				<div class="card-body p-2">
					<c:choose>
						<c:when test="${not empty cart.items}">
							<c:forEach var="cartItem" items="${cart.items.values()}">
								<div id="row-${cartItem.product.id}" class="cart-item">
									<div class="row align-items-center g-3">
										<div class="col-3 col-md-2">
											<img src="${pageContext.request.contextPath}/image/product/${cartItem.product.photo}"
												 alt="${cartItem.product.name}" class="img-fluid rounded-3 border">
										</div>
										<div class="col-9 col-md-4">
											<h6 class="fw-bold text-dark m-0"><c:out value="${cartItem.product.name}"/></h6>
											<span class="badge bg-light text-secondary border mt-1">Đồ uống hệ thống</span>
										</div>
										<div class="col-6 col-md-3">
											<div class="input-group input-group-sm" style="max-width: 110px;">
												<button class="btn btn-outline-secondary" type="button"
														onclick="changeQuantity(${cartItem.product.id}, -1)">-</button>
												<input type="number" class="form-control text-center quantity-input"
													   id="quantity-${cartItem.product.id}"
													   onchange="updateQuantity(${cartItem.product.id}, this.value)"
													   value="${cartItem.quantity}" min="1"
													   max="${cartItem.product.stock}"
													   data-price="${cartItem.product.price}"
													   data-original-quantity="${cartItem.quantity}">
												<button class="btn btn-outline-secondary" type="button"
														onclick="changeQuantity(${cartItem.product.id}, 1)">+</button>
											</div>
										</div>
										<div class="col-4 col-md-2 text-end">
                                            <span class="fw-bold text-dark">
                                                <span id="item-total-${cartItem.product.id}">
                                                    <fmt:formatNumber value="${cartItem.product.price * cartItem.quantity}" pattern="#,###" />
                                                </span> đ
                                            </span>
										</div>
										<div class="col-2 col-md-1 text-end">
											<button class="btn btn-sm btn-outline-danger border-0" onclick="removeItem(${cartItem.product.id})" title="Xóa khỏi giỏ">
												<i class="bi bi-trash3-fill"></i>
											</button>
										</div>
									</div>
									<hr class="text-muted opacity-25 mt-3 mb-0">
								</div>
							</c:forEach>
						</c:when>
						<c:otherwise>
							<div id="empty-cart-msg" class="text-center py-5">
								<div class="fs-1 text-muted mb-3"><i class="bi bi-cart-x"></i></div>
								<h5 class="text-muted fw-medium">Giỏ hàng của bạn đang trống rỗng</h5>
							</div>
						</c:otherwise>
					</c:choose>
				</div>
			</div>

			<div class="text-start">
				<a href="${pageContext.request.contextPath}/" class="btn btn-light border rounded-pill px-4 btn-sm text-secondary">
					<i class="bi bi-arrow-left me-2"></i>Quay lại thực đơn
				</a>
			</div>
		</div>

		<div class="col-lg-4">
			<div class="card card-custom p-3 mb-4">
				<div class="card-body">
					<h5 class="fw-bold mb-4 text-dark"><i class="bi bi-receipt-cutoff text-primary me-2"></i>Tóm tắt đơn hàng</h5>

					<form action="${pageContext.request.contextPath}/secure/payment" method="POST">
						<input type="hidden" name="action" value="pay">
						<c:forEach var="cartItem" items="${cart.items.values()}">
							<input type="hidden" name="productIds" value="${cartItem.product.id}">
							<input type="hidden" name="quantities" value="${cartItem.quantity}">
							<input type="hidden" name="prices" value="${cartItem.product.price}">
						</c:forEach>

						<input type="hidden" name="subtotal" value="${cart.totalPrice}">
						<input type="hidden" name="shipping" value="${cart.totalPrice > 0 ? 15000 : 0}">
						<input type="hidden" name="total" value="${cart.totalPrice + (cart.totalPrice > 0 ? 15000 : 0)}">

						<div class="d-flex justify-content-between mb-3 text-secondary small">
							<span>Tạm tính tiền hàng:</span>
							<span id="subtotal" class="fw-semibold text-dark">
                                <fmt:formatNumber value="${cart.totalPrice}" pattern="#,###" /> VNĐ
                            </span>
						</div>
						<div class="d-flex justify-content-between mb-3 text-secondary small">
							<span>Phí giao hàng (Ship):</span>
							<span id="shipping" class="fw-semibold text-dark">
                                <fmt:formatNumber value="${cart.totalPrice > 0 ? 15000 : 0}" pattern="#,###" /> VNĐ
                            </span>
						</div>
						<hr class="text-muted opacity-25 my-3">
						<div class="d-flex justify-content-between align-items-center mb-4">
							<strong class="text-dark">Tổng cộng:</strong>
							<strong id="total" class="fs-4 text-danger fw-bold">
								<fmt:formatNumber value="${cart.totalPrice + (cart.totalPrice > 0 ? 15000 : 0)}" pattern="#,###" /> VNĐ
							</strong>
						</div>

						<c:choose>
							<c:when test="${cart.totalPrice <= 0}">
								<button id="checkoutButton" class="btn btn-secondary w-100 btn-checkout shadow-sm" disabled>Giỏ hàng trống</button>
							</c:when>
							<c:otherwise>
								<button id="checkoutButton" class="btn btn-primary w-100 btn-checkout shadow-sm" style="background-color: #6c5ce7; border-color: #6c5ce7;">Tiến hành đặt đơn</button>
							</c:otherwise>
						</c:choose>
					</form>

					<%-- THÔNG BÁO ALERT ĐỒNG BỘ --%>
					<c:if test="${not empty param.success}">
						<div class="alert alert-success border-0 shadow-sm rounded-3 text-center small mt-3"><i class="bi bi-check-circle-fill me-1"></i>${param.success}</div>
					</c:if>
					<c:if test="${not empty param.error}">
						<div class="alert alert-danger border-0 shadow-sm rounded-3 text-center small mt-3"><i class="bi bi-exclamation-triangle-fill me-1"></i>${param.error}</div>
					</c:if>
				</div>
			</div>

			<div class="card card-custom p-3">
				<div class="card-body">
					<h6 class="fw-bold mb-3 text-dark"><i class="bi bi-ticket-perforated text-primary me-2"></i>Áp dụng mã khuyến mãi</h6>
					<div class="input-group input-group-sm mb-1">
						<input type="text" class="form-control rounded-start-3" placeholder="Nhập mã ưu đãi...">
						<button class="btn btn-outline-primary px-3 fw-medium rounded-end-3" type="button">Áp dụng</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<%-- GIỮ NGUYÊN: Footer chung --%>
<%@ include file="/template/includes/footer.jsp"%>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
	function formatCurrencyVN(amount) {
		return amount.toLocaleString('vi-VN') + " VNĐ";
	}

	// 1. AJAX XÓA ITEM KHỎI GIỎ HÀNG
	function removeItem(productId) {
		$.ajax({
			url: "${pageContext.request.contextPath}/secure/cart?action=removeItem",
			type: "POST",
			data: { id: productId },
			success: function(response) {
				if(response.success) {
					$("#subtotal").text(formatCurrencyVN(response.subtotal));
					$("#shipping").text(formatCurrencyVN(response.shipping));
					$("#total").text(formatCurrencyVN(response.total));

					$("#row-" + productId).fadeOut(300, function() {
						$(this).remove();
						if ($(".cart-item").length === 0) {
							location.reload();
						}
					});
				} else {
					alert(response.message);
				}
			}
		});
	}

	// 2. AJAX CẬP NHẬT SỐ LƯỢNG LY TRÀ SỮA
	function updateQuantity(productId, newQuantity) {
		newQuantity = parseInt(newQuantity);
		const input = $('#quantity-' + productId);
		const maxQuantity = parseInt(input.attr('max'));
		const price = parseFloat(input.data('price'));

		if (newQuantity < 1) newQuantity = 1;
		if (newQuantity > maxQuantity) {
			alert("Số lượng yêu cầu đã đạt tới giới hạn tồn kho của cửa hàng!");
			newQuantity = maxQuantity;
		}

		$.ajax({
			url: '${pageContext.request.contextPath}/secure/cart?action=updateQuantity',
			type: 'POST',
			data: { productId: productId, quantity: newQuantity },
			success: function(response) {
				var data = typeof response === 'string' ? JSON.parse(response) : response;
				if (data.success) {
					input.val(newQuantity);
					$('#item-total-' + productId).text((newQuantity * price).toLocaleString('vi-VN'));

					updateSummary(data);
					checkCheckoutButton(data.subtotal);
				} else {
					alert('Lỗi: ' + data.message);
					input.val(input.data('original-quantity'));
				}
			},
			error: function(xhr) {
				alert('Mất kết nối tới máy chủ Boba Station.');
				input.val(input.data('original-quantity'));
			}
		});
	}

	function changeQuantity(productId, delta) {
		const input = $('#quantity-' + productId);
		let inStock = parseInt(input.attr('max'));
		let newQuantity = parseInt(input.val()) + delta;
		newQuantity = Math.max(1, Math.min(newQuantity, inStock));
		input.val(newQuantity);
		updateQuantity(productId, newQuantity);
	}

	function updateSummary(data) {
		$('#subtotal').text(formatCurrencyVN(data.subtotal));
		$('#shipping').text(formatCurrencyVN(data.shipping));
		$('#total').text(formatCurrencyVN(data.total));

		// Đồng bộ ngược lại các giá trị vào thẻ input ẩn để submit form chuẩn xác
		$('input[name="subtotal"]').val(data.subtotal);
		$('input[name="shipping"]').val(data.shipping);
		$('input[name="total"]').val(data.total);
	}

	function checkCheckoutButton(subtotal) {
		const checkoutButton = $('#checkoutButton');
		if (subtotal <= 0) {
			checkoutButton.removeClass('btn-primary').addClass('btn-secondary').prop('disabled', true).text('Giỏ hàng trống');
		} else {
			checkoutButton.removeClass('btn-secondary').addClass('btn-primary').prop('disabled', false).text('Tiến hành đặt đơn');
		}
	}

	$(document).ready(function() {
		checkCheckoutButton(${cart.totalPrice != null ? cart.totalPrice : 0});
	});
</script>
</body>
</html>