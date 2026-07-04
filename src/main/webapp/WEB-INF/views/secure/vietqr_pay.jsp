<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN" />

<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <title>Thanh Toán VietQR</title>
  <jsp:include page="/template/includes/headerResource.jsp" />
  <style>
    .qr-container {
      max-width: 500px;
      margin: 0 auto;
      background: #fff;
      padding: 30px;
      border-radius: 15px;
      box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    }
    .timer-box {
      background: #fff3cd;
      color: #856404;
      padding: 15px;
      border-radius: 8px;
      border: 1px solid #ffeeba;
      font-size: 1.2rem;
      font-weight: bold;
    }
  </style>
</head>
<body class="bg-light">
<%@ include file="/template/includes/navbar.jsp"%>

<div class="container py-5">
  <div class="qr-container text-center">
    <h2 class="fw-bold mb-4">Chuyển Khoản Ngân Hàng</h2>

    <div class="timer-box mb-4">
      ⏳ Đơn hàng sẽ tự động hủy sau: <span id="countdown" class="text-danger fs-3 ms-2">10:00</span>
    </div>

    <div class="mb-4">
      <p class="mb-1 text-muted">Mã đơn hàng của bạn:</p>
      <h4 class="fw-bold text-primary">#${orderId}</h4>
    </div>

    <div class="mb-4">
      <p class="mb-1 text-muted">Số tiền cần thanh toán:</p>
      <h3 class="fw-bold text-danger">
        <fmt:formatNumber value="${totalPriceVnd}" pattern="#,###" /> VNĐ
      </h3>
    </div>

    <div class="bg-white p-3 rounded border mb-4 d-inline-block">
      <img src="${vietQrUrl}" alt="VietQR" class="img-fluid" style="max-width: 300px;">
    </div>

    <p class="text-muted small mb-4">
      Mở ứng dụng ngân hàng và quét mã QR phía trên để thanh toán.<br>
      Nội dung chuyển khoản đã được tạo tự động.
    </p>

    <form action="${pageContext.request.contextPath}/secure/vietqr-confirm" method="POST" id="confirmForm">
      <input type="hidden" name="orderId" value="${orderId}">
      <button type="submit" class="btn btn-primary btn-lg w-100 mb-3" id="btnConfirm">
        Tôi Đã Chuyển Khoản Thành Công
      </button>
    </form>

    <a href="${pageContext.request.contextPath}/secure/cart" class="btn btn-outline-secondary w-100">
      Hủy và quay lại giỏ hàng
    </a>
  </div>
</div>

<%@ include file="/template/includes/footer.jsp"%>

<script>
  // Thiết lập thời gian đếm ngược: 10 phút = 600 giây
  let timeLeft = 600;
  const countdownEl = document.getElementById('countdown');
  const btnConfirm = document.getElementById('btnConfirm');

  let timerId = setInterval(function() {
    let minutes = Math.floor(timeLeft / 60);
    let seconds = timeLeft % 60;

    // Format 00:00
    minutes = minutes < 10 ? '0' + minutes : minutes;
    seconds = seconds < 10 ? '0' + seconds : seconds;

    countdownEl.innerText = minutes + ':' + seconds;

    if (timeLeft <= 0) {
      clearInterval(timerId);
      btnConfirm.disabled = true; // Khóa nút xác nhận
      alert('⏳ Đã hết 10 phút chờ thanh toán. Vui lòng đặt hàng lại!');
      window.location.href = '${pageContext.request.contextPath}/secure/cart?error=Hết thời gian thanh toán mã QR';
    }
    timeLeft -= 1;
  }, 1000);

  // Hiệu ứng loading khi bấm xác nhận để tránh click nhiều lần
  document.getElementById('confirmForm').addEventListener('submit', function() {
    btnConfirm.innerHTML = '<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Đang xử lý...';
    btnConfirm.disabled = true;
  });
</script>
</body>
</html>