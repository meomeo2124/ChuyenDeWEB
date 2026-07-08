<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<fmt:setLocale value="vi_VN" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <jsp:include page="/template/includes/headerResource.jsp" />
    <title>Boba Station - Thưởng Thức Trà Sữa Cao Cấp</title>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            font-family: 'Plus Jakarta Sans', sans-serif;
            background-color: #fcfcfd;
            color: #1d1d1f;
            font-size: 14px;
        }
        /* Banner tinh giản, không bị quá to và ngợp */
        .hero-banner {
            background: linear-gradient(135deg, #6c5ce7, #4834d4);
            color: #ffffff;
            padding: 45px 20px;
            text-align: center;
            border-radius: 0 0 24px 24px;
        }
        .hero-banner h1 {
            font-size: 2rem;
            font-weight: 700;
            letter-spacing: -0.5px;
        }
        /* Thu nhỏ Card và tạo hiệu ứng nịnh mắt */
        .product-card {
            background: #ffffff;
            border: 1px solid #f1f1f5;
            border-radius: 20px;
            transition: transform 0.4s cubic-bezier(0.16, 1, 0.3, 1), box-shadow 0.4s ease;
            overflow: hidden;
        }
        .product-card:hover {
            transform: translateY(-6px);
            box-shadow: 0 20px 35px rgba(108, 92, 231, 0.08);
            border-color: rgba(108, 92, 231, 0.2);
        }
        .product-img-wrapper {
            position: relative;
            padding-top: 100%;
            overflow: hidden;
            background-color: #f7f7f9;
            margin: 10px;
            border-radius: 14px;
        }
        .product-img-wrapper img {
            position: absolute;
            top: 0; left: 0; width: 100%; height: 100%;
            object-fit: cover;
            transition: transform 0.6s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .product-card:hover .product-img-wrapper img {
            transform: scale(1.05);
        }
        /* Tinh chỉnh cỡ chữ nhỏ gọn, sang trọng */
        .product-title {
            font-size: 14px;
            font-weight: 600;
            line-height: 1.4;
            height: 40px;
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
        }
        .product-price {
            font-size: 15px;
            font-weight: 700;
            color: #6c5ce7;
        }
        .btn-view-options {
            border-radius: 12px;
            font-weight: 600;
            font-size: 12px;
            border: 1px solid #6c5ce7;
            color: #6c5ce7;
            background: transparent;
            padding: 8px 12px;
            transition: all 0.2s ease;
        }
        .product-card:hover .btn-view-options {
            background-color: #6c5ce7;
            color: #ffffff;
        }
        .btn-show-more {
            border-radius: 14px;
            padding: 10px 24px;
            font-weight: 600;
            font-size: 13px;
            border: 1px solid #e2e2e9;
            color: #4b4b4d;
            background: #ffffff;
            transition: all 0.2s ease;
        }
        .btn-show-more:hover {
            background-color: #6c5ce7;
            color: #ffffff;
            border-color: #6c5ce7;
        }
    </style>
</head>
<body>

<%@ include file="/template/includes/navbar.jsp"%>

<header class="hero-banner">
    <div class="container">
        <h1>Boba Station</h1>
        <p class="lead text-white-50 fs-6 m-0 mt-1">"I never laugh until I have a Drink"</p>
    </div>
</header>
<!-- KHỐI MUA NHANH FLASH SALE GIỜ VÀNG GIÁ SỐC -->
<div class="container mt-4">
    <div class="card p-3" style="border: 2px solid #6c5ce7; border-radius: 20px; background: #fff; box-shadow: 0 0 15px rgba(108, 92, 231, 0.15);">
        <div class="d-flex flex-column flex-sm-row justify-content-between align-items-center mb-3 px-2 gap-2">
            <div class="d-flex align-items-center">
                <span class="badge bg-danger me-2 px-3 py-2 text-uppercase fw-bold animate-pulse" style="font-size: 12px; border-radius: 30px;">
                    <i class="bi bi-lightning-charge-fill me-1"></i>Flash Sale
                </span>
                <h5 class="fw-bold m-0 text-dark">Khung Giờ Vàng Giá Sốc</h5>
            </div>
            <!-- Đồng hồ đếm ngược -->
            <div class="d-flex align-items-center gap-2 bg-light px-3 py-1.5 rounded-pill border">
                <span class="text-secondary small fw-medium">Kết thúc trong:</span>
                <div id="countdown-timer" class="fw-bold text-danger fs-5 font-monospace">00:00:00</div>
            </div>
        </div>

        <!-- Danh sách 3 món signature giảm giá mạnh -->
        <div class="row g-3">
            <c:forEach var="product" items="${productList}" varStatus="status">
                <c:if test="${status.index < 3}">
                    <div class="col-md-4">
                        <div class="d-flex align-items-center p-2 rounded-3 border bg-light position-relative h-100">
                            <!-- Nhãn giảm giá bọc góc ảnh -->
                            <span class="position-absolute top-0 start-0 badge bg-danger m-2" style="border-radius: 8px;">-35%</span>
                            <img src="${pageContext.request.contextPath}/image/product/${not empty product.photo ? product.photo : 'no-sample.png'}"
                                 class="rounded-3 border" style="width: 75px; height: 75px; object-fit: cover;">
                            <div class="ms-3 flex-grow-1">
                                <h6 class="fw-bold text-dark mb-1 text-truncate" style="max-width: 150px;"><c:out value="${product.name}"/></h6>
                                <div class="small mb-2">
                                    <!-- Giá flash sale -->
                                    <span class="fw-bold text-danger fs-6"><fmt:formatNumber value="${product.price * 0.65}" pattern="#,###"/> đ</span>
                                    <span class="text-muted text-decoration-line-through ms-1" style="font-size: 11px;"><fmt:formatNumber value="${product.price}" pattern="#,###"/>đ</span>
                                </div>
                                <form action="${pageContext.request.contextPath}/addToCart" method="POST" style="margin:0;">
                                    <input type="hidden" name="productId" value="${product.id}">
                                    <input type="hidden" name="inputQuantity" value="1">
                                    <button type="submit" class="btn btn-sm btn-danger px-3 w-100 flash-sale-btn" style="border-radius: 10px; font-weight:600; font-size:12px;">Mua ngay</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </c:if>
            </c:forEach>
        </div>
    </div>
</div>

<!-- SCRIPT JAVASCRIPT ĐIỀU HƯỚNG TỰ ĐỘNG KHÓA KHI HẾT GIỜ VÀNG -->
<script>
    function startCountdown() {
        // Thiết lập đếm ngược thời gian giả định kết thúc vào cuối ngày (hoặc setup giờ cố định tùy bạn)
        const now = new Date();
        const targetTime = new Date();
        targetTime.setHours(23, 59, 59, 0); // Ví dụ tự động đếm ngược đến hết ngày hôm nay

        let duration = Math.floor((targetTime - now) / 1000);

        const timerDisplay = document.getElementById('countdown-timer');
        const flashBtns = document.querySelectorAll('.flash-sale-btn');

        const interval = setInterval(() => {
            if (duration <= 0) {
                clearInterval(interval);
                timerDisplay.innerText = "00:00:00";
                // Tự động vô hiệu hóa nút mua nhanh khi hết khung giờ vàng
                flashBtns.forEach(btn => {
                    btn.disabled = true;
                    btn.classList.replace('btn-danger', 'btn-secondary');
                    btn.innerText = "Đã hết giờ";
                });
                return;
            }

            let hours = Math.floor(duration / 3600);
            let minutes = Math.floor((duration % 3600) / 60);
            let seconds = duration % 60;

            hours = hours < 10 ? "0" + hours : hours;
            minutes = minutes < 10 ? "0" + minutes : minutes;
            seconds = seconds < 10 ? "0" + seconds : seconds;

            timerDisplay.innerText = hours + ":" + minutes + ":" + seconds;
            duration--;
        }, 1000);
    }
    document.addEventListener("DOMContentLoaded", startCountdown);
</script>
<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">

<section class="py-5">
    <div class="container">
        <div class="d-flex align-items-center mb-4">
            <h5 class="fw-bold m-0 text-dark"><i class="bi bi-cup-straw text-primary me-2"></i>Thực đơn hôm nay</h5>
        </div>

        <c:choose>
            <c:when test="${not empty productList}">
                <!-- Thay đổi lưới hiển thị nhỏ gọn hơn: hiển thị 4 hoặc 5 sản phẩm/hàng thay vì quá to như cũ -->
                <div id="content" class="row gx-3 gy-4 row-cols-2 row-cols-md-4 row-cols-xl-5 justify-content-center">
                    <c:forEach var="product" items="${productList}">
                        <div class="col product-count">
                            <div class="card h-100 product-card">
                                <a href="${pageContext.request.contextPath}/product?id=${product.id}">
                                    <div class="product-img-wrapper">
                                        <img src="${pageContext.request.contextPath}/image/product/${not empty product.photo ? product.photo : 'no-sample.png'}"
                                             alt="${product.name}" loading="lazy"
                                             onerror="this.src='${pageContext.request.contextPath}/image/product/no-sample.png';">
                                    </div>
                                </a>

                                <div class="card-body p-3 pt-1 text-center d-flex flex-column justify-content-between">
                                    <div class="mb-2">
                                        <div class="product-title">
                                            <a href="${pageContext.request.contextPath}/product?id=${product.id}" class="text-decoration-none text-dark">
                                                <c:out value="${product.name}"/>
                                            </a>
                                        </div>
                                        <div class="d-flex justify-content-center small text-warning gap-0.5" style="font-size: 11px;">
                                            <i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i>
                                        </div>
                                    </div>
                                    <div class="product-price mb-3">
                                        <fmt:formatNumber value="${product.price != null ? product.price : 0.0}" pattern="#,###" /> đ
                                    </div>
                                    <a class="btn btn-view-options w-100" href="${pageContext.request.contextPath}/product?id=${product.id}">Thêm mua ngay</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <div class="text-center mt-5">
                    <button id="show-more" onclick="loadMore()" class="btn btn-show-more shadow-sm">Xem thêm sản phẩm</button>
                </div>
            </c:when>
            <c:otherwise>
                <div class="col-md-12 text-center py-5">
                    <p class="text-muted">Hiện tại chưa có mặt hàng đồ uống nào.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</section>

<%@ include file="/template/includes/footer.jsp"%>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>
    function loadMore() {
        var amount = document.getElementsByClassName("product-count").length;
        var contextPath = document.getElementById("contextPath").value;
        $.ajax({
            url: contextPath + "/load",
            type: "GET",
            data: { exists: amount },
            success: function(data) {
                var row = document.getElementById("content");
                if (data && data.trim() !== "") {
                    row.innerHTML += data;
                } else {
                    $('#show-more').prop('disabled', true).text('Đã hiển thị toàn bộ đồ uống');
                }
            },
            error: function(xhr) {
                console.error(xhr);
            }
        });
    }
</script>
</body>
</html>