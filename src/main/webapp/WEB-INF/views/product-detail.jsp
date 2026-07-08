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
    <title>Boba Station - <c:out value="${product.name}"/></title>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            font-family: 'Plus Jakarta Sans', sans-serif;
            background-color: #fcfcfd;
            color: #1d1d1f;
            font-size: 14px;
        }
        .detail-container {
            max-width: 950px;
            margin: 0 auto;
        }
        .detail-img-card {
            border: 1px solid #f1f1f5;
            border-radius: 24px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.02);
            overflow: hidden;
            background: #ffffff;
            padding: 15px;
        }
        .detail-img-card img {
            border-radius: 16px;
            object-fit: cover;
            width: 100%;
        }
        .quantity-control {
            max-width: 120px;
            border-radius: 12px;
            background-color: #f5f5f7;
            padding: 4px;
        }
        .quantity-control button {
            width: 32px;
            height: 32px;
            border-radius: 8px;
            background: #ffffff;
            border: 1px solid #e2e2e9;
            font-weight: 600;
            transition: all 0.2s;
        }
        .quantity-control button:hover {
            background: #6c5ce7;
            color: #ffffff;
            border-color: #6c5ce7;
        }
        .btn-add-cart {
            background: #6c5ce7;
            border: none;
            color: #ffffff;
            border-radius: 14px;
            padding: 12px 28px;
            font-weight: 600;
            font-size: 14px;
            transition: all 0.3s cubic-bezier(0.16, 1, 0.3, 1);
        }
        .btn-add-cart:hover {
            background: #4834d4;
            box-shadow: 0 8px 20px rgba(108, 92, 231, 0.25);
        }
        .related-card {
            border: 1px solid #f1f1f5;
            border-radius: 16px;
            overflow: hidden;
            background: #ffffff;
            transition: all 0.3s ease;
        }
        .related-card:hover {
            transform: translateY(-4px);
            box-shadow: 0 12px 25px rgba(0,0,0,0.04);
        }
        .related-img-wrapper {
            position: relative;
            padding-top: 100%;
            overflow: hidden;
            background: #f7f7f9;
            margin: 8px;
            border-radius: 10px;
        }
        .related-img-wrapper img {
            position: absolute;
            top: 0; left: 0; width: 100%; height: 100%;
            object-fit: cover;
        }
    </style>
</head>
<body>

<%@ include file="/template/includes/navbar.jsp"%>

<input type="hidden" id="contextPath" value="${pageContext.request.contextPath}">

<section class="py-5">
    <div class="container detail-container">
        <div class="row gx-4 gx-lg-5 align-items-center">
            <div class="col-md-5 mb-4 mb-md-0">
                <div class="detail-img-card">
                    <img class="img-fluid"
                         src="${pageContext.request.contextPath}/image/product/${not empty product.photo ? product.photo : 'no-sample.png'}"
                         alt="${product.name}"
                         onerror="this.src='${pageContext.request.contextPath}/image/product/no-sample.png';">
                </div>
            </div>

            <div class="col-md-7 ps-md-5">
                <span class="text-muted small fw-medium d-block mb-1">Mã sản phẩm: #${product.id}</span>
                <h2 class="fw-bold text-dark mb-2"><c:out value="${product.name}"/></h2>

                <div class="mb-3">
                    <span class="fs-4 fw-bold text-danger">
                        <fmt:formatNumber value="${product.price != null ? product.price : 0.0}" pattern="#,###" /> đ
                    </span>
                </div>

                <h6 class="fw-bold text-secondary mb-1" style="font-size: 13px;">Mô tả hương vị:</h6>
                <p class="text-secondary mb-4" style="font-size: 13.5px; line-height: 1.6;">
                    ${not empty product.description ? product.description : 'Món uống signature thơm ngon đậm vị độc quyền chuẩn công thức tại Boba Station.'}
                </p>

                <form action="${pageContext.request.contextPath}/addToCart" method="POST" class="mt-4">
                    <div class="d-flex align-items-center gap-3">
                        <div class="d-flex align-items-center quantity-control">
                            <button type="button" onclick="adjustQty(-1)">-</button>
                            <input class="form-control text-center border-0 fw-bold bg-transparent p-0" id="inputQuantity" name="inputQuantity"
                                   type="number" value="1" min="1" readonly style="font-size: 14px;" />
                            <button type="button" onclick="adjustQty(1)">+</button>
                        </div>

                        <input name="productId" type="hidden" value="${product.id}" />
                        <button class="btn btn-add-cart" type="submit">
                            <i class="bi bi-bag-plus-fill me-2"></i>Thêm vào giỏ mua
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<!-- KHU VỰC CHỈ HIỂN THỊ ĐÁNH GIÁ (ĐÃ BỎ FORM NHẬP LƯU ĐỂ TRÁNH SPAM) -->
<div class="container mb-5 detail-container">
    <div class="card p-4 border-0 shadow-sm" style="border-radius: 20px; background: #fff;">
        <h6 class="fw-bold text-dark mb-4"><i class="bi bi-chat-heart-fill text-danger me-2"></i>Khách hàng đánh giá thực tế</h6>

        <div class="review-list" style="max-height: 400px; overflow-y: auto;">
            <c:forEach var="rev" items="${reviews}">
                <div class="d-flex align-items-start pb-3 mb-3 border-bottom border-light">
                    <img src="${pageContext.request.contextPath}/image/avatars/default-avatar.png" class="rounded-circle border" style="width: 40px; height: 40px; object-fit: cover;">
                    <div class="ms-3 flex-grow-1">
                        <div class="d-flex justify-content-between align-items-center">
                            <span class="fw-bold text-dark text-sm"><c:out value="${rev.username}"/></span>
                            <span class="text-muted font-monospace" style="font-size: 11px;"><fmt:formatDate value="${rev.createdAt}" pattern="dd/MM/yyyy HH:mm"/></span>
                        </div>
                        <div class="text-warning mb-1" style="font-size: 11px;">
                            <c:forEach begin="1" end="${rev.rating}"><i class="bi bi-star-fill"></i></c:forEach>
                        </div>
                        <p class="text-secondary m-0 mb-2" style="font-size: 13px;"><c:out value="${rev.comment}"/></p>

                        <c:if test="${not empty rev.imagePath}">
                            <div class="mt-1">
                                <img src="${pageContext.request.contextPath}/uploads/${rev.imagePath}" class="rounded-3 border img-thumbnail" style="max-width: 120px; max-height: 120px; object-fit: cover; cursor: pointer;" onclick="window.open(this.src)">
                            </div>
                        </c:if>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty reviews}">
                <div class="text-center py-4 text-muted small border rounded-3 bg-light">Chưa có đánh giá nào cho ly đồ uống này. Hãy là người đầu tiên trải nghiệm!</div>
            </c:if>
        </div>
    </div>
</div>

<section class="py-5 bg-light border-top">
    <div class="container detail-container">
        <h6 class="fw-bold text-dark mb-4"><i class="bi bi-stars text-warning me-2"></i>Đồ uống gợi ý thêm</h6>

        <c:choose>
            <c:when test="${not empty productList}">
                <div id="content" class="row gx-3 row-cols-2 row-cols-md-4 justify-content-center">
                    <c:forEach var="relatedProduct" items="${productList}" varStatus="status">
                        <c:if test="${status.index < 4}">
                            <div class="col">
                                <div class="card h-100 related-card">
                                    <a href="${pageContext.request.contextPath}/product?id=${relatedProduct.id}">
                                        <div class="related-img-wrapper">
                                            <img src="${pageContext.request.contextPath}/image/product/${not empty relatedProduct.photo ? relatedProduct.photo : 'no-sample.png'}"
                                                 alt="${relatedProduct.name}"
                                                 onerror="this.src='${pageContext.request.contextPath}/image/product/no-sample.png';">
                                        </div>
                                    </a>
                                    <div class="card-body p-3 text-center d-flex flex-column justify-content-between">
                                        <div class="fw-semibold text-dark text-truncate mb-1" style="font-size: 13px;">
                                            <c:out value="${relatedProduct.name}"/>
                                        </div>
                                        <span class="fw-bold text-muted small">
                                            <fmt:formatNumber value="${relatedProduct.price != null ? relatedProduct.price : 0.0}" pattern="#,###" /> đ
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </c:when>
        </c:choose>
    </div>
</section>

<%@ include file="/template/includes/footer.jsp"%>

<script>
    function adjustQty(delta) {
        const input = document.getElementById('inputQuantity');
        let currentVal = parseInt(input.value) || 1;
        currentVal += delta;
        if(currentVal < 1) currentVal = 1;
        input.value = currentVal;
    }
</script>
</body>
</html>