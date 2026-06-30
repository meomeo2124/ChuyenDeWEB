<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<jsp:include page="/template/includes/headerResource.jsp" />
	<title>Shopping Cart</title>
	<style>
		.quantity-input {
			width: 60px;
		}
		.cart-item {
			transition: all 0.3s ease;
		}
		.alert {
			margin-top: 10px;
		}
		/* Ẩn mũi tên tăng giảm mặc định của trình duyệt để giao diện đẹp hơn */
		.quantity-input::-webkit-outer-spin-button,
		.quantity-input::-webkit-inner-spin-button {
			-webkit-appearance: none;
			margin: 0;
		}
	</style>
</head>
<body>
<%@ include file="/template/includes/navbar.jsp"%>

<div class="container py-5">
	<h1 class="mb-5">Your Shopping Cart</h1>
	<div class="row">
		<div class="col-lg-8">
			<div class="card mb-4">
				<div class="card-body">
					<c:choose>
						<c:when test="${not empty cart.items}">
							<c:forEach var="cartItem" items="${cart.items.values()}">
							<div id="row-${cartItem.product.id}" class="cart-item mb-3">
								<div class="row align-items-center">
									<div class="col-md-3">
										<img src="${pageContext.request.contextPath}/image/product/${cartItem.product.photo}"
										     alt="${cartItem.product.name}" class="img-fluid rounded">
									</div>
									<div class="col-md-4">
										<h5 class="card-title">${cartItem.product.name}</h5>
										<p class="text-muted">Category: ${cartItem.product.category}</p>
									</div>
									<div class="col-md-2">
										<div class="input-group">
											<button class="btn btn-outline-secondary btn-sm" type="button"
											        onclick="changeQuantity(${cartItem.product.id}, -1)">-</button>
											<input type="number" class="form-control form-control-sm text-center quantity-input"
											       id="quantity-${cartItem.product.id}"
											       onchange="updateQuantity(${cartItem.product.id}, this.value)"
											       value="${cartItem.quantity}" min="1"
											       max="${cartItem.product.stock}"
											       data-price="${cartItem.product.price}"
											       data-original-quantity="${cartItem.quantity}">
											<button class="btn btn-outline-secondary btn-sm" type="button"
											        onclick="changeQuantity(${cartItem.product.id}, 1)">+</button>
										</div>
									</div>
									<div class="col-md-2 text-end">
										<p class="fw-bold">$ <span id="item-total-${cartItem.product.id}">${cartItem.product.price * cartItem.quantity}</span></p>
									</div>
									<div class="col-md-1">
										<button class="btn btn-sm btn-outline-danger" onclick="removeItem(${cartItem.product.id})">
											<i class="bi bi-trash"></i>
										</button>
									</div>
								</div>
								<hr>
							</div>
						</c:forEach>
					</c:when>
					<c:otherwise>
						<div id="empty-cart-msg" class="row cart-item mb-3">
							<div class="col-md-12 text-center py-4">
								<h5 class="card-title text-muted">Your Cart Is Empty</h5>
							</div>
						</div>
					</c:otherwise>
					</c:choose>
				</div>
			</div>
			<div class="text-start mb-4">
				<a href="${pageContext.request.contextPath}/" class="btn btn-outline-primary">
					<i class="bi bi-arrow-left me-2"></i>Continue Shopping
				</a>
			</div>
		</div>

		<div class="col-lg-4">
			<div class="card cart-summary">
				<div class="card-body">
					<h5 class="card-title mb-4">Order Summary</h5>
					<form action="${pageContext.request.contextPath}/secure/payment" method="POST">
						<input type="hidden" name="action" value="proceedToPayment">
						<c:forEach var="cartItem" items="${cart.items.values()}">
							<input type="hidden" name="productIds" value="${cartItem.product.id}">
							<input type="hidden" name="quantities" value="${cartItem.quantity}">
							<input type="hidden" name="prices" value="${cartItem.product.price}">
						</c:forEach>
						<input type="hidden" name="subtotal" value="${cart.totalPrice}">
						<input type="hidden" name="shipping" value="${cart.totalPrice > 0 ? 10.00 : 0.00}">
						<input type="hidden" name="total" value="${cart.totalPrice + (cart.totalPrice > 0 ? 10.00 : 0.00)}">

						<div class="d-flex justify-content-between mb-3">
							<span>Subtotal</span>
							<span id="subtotal" class="subtotal">
									<fmt:formatNumber value="${cart.totalPrice}" type="currency" />
								</span>
						</div>
						<div class="d-flex justify-content-between mb-3">
							<span>Shipping</span>
							<span id="shipping" class="shipping">
									<fmt:formatNumber value="${cart.totalPrice > 0 ? 10.00 : 0.00}" type="currency" />
								</span>
						</div>
						<hr>
						<div class="d-flex justify-content-between mb-4">
							<strong>Total</strong>
							<strong id="total" class="total">
								<fmt:formatNumber value="${cart.totalPrice + (cart.totalPrice > 0 ? 10.00 : 0.00)}" type="currency" />
							</strong>
						</div>
						<c:choose>
							<c:when test="${cart.totalPrice <= 0}">
								<button id="checkoutButton" class="btn btn-warning w-100" disabled>Proceed to Checkout</button>
							</c:when>
							<c:otherwise>
								<button id="checkoutButton" class="btn btn-primary w-100">Proceed to Checkout</button>
							</c:otherwise>
						</c:choose>
					</form>
					<c:if test="${not empty param.success}">
						<div class="alert alert-success">${param.success}</div>
					</c:if>
					<c:if test="${not empty param.error}">
						<div class="alert alert-danger">${param.error}</div>
					</c:if>
				</div>
			</div>
			<div class="card mt-4">
				<div class="card-body">
					<h5 class="card-title mb-3">Apply Promo Code</h5>
					<div class="input-group mb-3">
						<input type="text" class="form-control" placeholder="Enter promo code">
						<button class="btn btn-outline-secondary" type="button">Apply</button>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>

<%@ include file="/template/includes/footer.jsp"%>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
	// 1. HÀM XÓA SẢN PHẨM KHỎI GIỎ HÀNG (SỬ DỤNG HIỆU ỨNG TRỰC TIẾP TRÊN FRONT-END)
	function removeItem(productId) {
		$.ajax({
			url: "${pageContext.request.contextPath}/secure/cart?action=removeItem",
			type: "POST",
			data: { id: productId },
			success: function(response) {
				if(response.success) {
					// Cập nhật lại 3 ô tiền tổng ở cột bên phải
					$("#subtotal").text("$" + response.subtotal.toFixed(2));
					$("#shipping").text("$" + response.shipping.toFixed(2));
					$("#total").text("$" + response.total.toFixed(2));

					// Hiệu ứng FadeOut mượt mà xóa hàng thô ra khỏi giao diện trực tiếp
					$("#row-" + productId).fadeOut(300, function() {
						$(this).remove();
						// Nếu xóa hết sạch hàng, hiển thị thông báo giỏ hàng trống
						if ($(".cart-item").length === 0) {
							location.reload(); // Refresh nhẹ để hiển thị trạng thái Empty sạch sẽ
						}
					});
				} else {
					alert(response.message);
				}
			}
		});
	}

	// 2. HÀM CẬP NHẬT SỐ LƯỢNG KHI THAY ĐỔI Ô NHẬP HOẶC ẤN + -
	function updateQuantity(productId, newQuantity) {
		newQuantity = parseInt(newQuantity);
		const input = $('#quantity-' + productId);
		const maxQuantity = parseInt(input.attr('max'));
		const price = parseFloat(input.data('price'));

		if (newQuantity < 1) newQuantity = 1;
		if (newQuantity > maxQuantity) {
			alert("Số lượng yêu cầu vượt quá hàng tồn kho hiện có!");
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
					// Cập nhật giá tổng của riêng sản phẩm đó dòng đó
					$('#item-total-' + productId).text((newQuantity * price).toFixed(2));
					// Cập nhật bảng tổng tiền hóa đơn
					updateSummary(data);
					checkCheckoutButton(data.subtotal);
				} else {
					alert('Có lỗi xảy ra: ' + data.message);
					input.val(input.data('original-quantity'));
				}
			},
			error: function(xhr) {
				alert('Có lỗi xảy ra khi kết nối máy chủ.');
				input.val(input.data('original-quantity'));
			}
		});
	}

	// 3. HÀM ĐIỀU KHIỂN NÚT BẤM + -
	function changeQuantity(productId, delta) {
		const input = $('#quantity-' + productId);
		let inStock = parseInt(input.attr('max'));
		let newQuantity = parseInt(input.val()) + delta;
		newQuantity = Math.max(1, Math.min(newQuantity, inStock));
		input.val(newQuantity);
		updateQuantity(productId, newQuantity);
	}

	function updateSummary(data) {
		$('#subtotal').text('$' + data.subtotal.toFixed(2));
		$('#shipping').text('$' + data.shipping.toFixed(2));
		$('#total').text('$' + data.total.toFixed(2));
	}

	function checkCheckoutButton(subtotal) {
		const checkoutButton = $('#checkoutButton');
		if (subtotal <= 0) {
			checkoutButton.removeClass('btn-primary').addClass('btn-warning').prop('disabled', true).text('Proceed to Checkout');
		} else {
			checkoutButton.removeClass('btn-warning').addClass('btn-primary').prop('disabled', false).text('Proceed to Checkout');
		}
	}

	$(document).ready(function() {
		checkCheckoutButton(${cart.totalPrice != null ? cart.totalPrice : 0});
	});
</script>
</body>
</html>