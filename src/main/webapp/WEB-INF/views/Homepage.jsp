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