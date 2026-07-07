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
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body {
            font-family: 'Inter', sans-serif;
            background-color: #f8f9fa;
        }
        .detail-img-card {
            border: none;
            border-radius: 16px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.03);
            overflow: hidden;
            background: #ffffff;
        }
        .quantity-control {
            max-width: 130px;
            border-radius: 20px;
            font-weight: 600;
        }
        .btn-add-cart {
            background-color: #6c5ce7;
            border-color: #6c5ce7;
            color: #ffffff;
            border-radius: 30px;
            padding: 10px 30px;
            font-weight: 600;
            transition: all 0.3s ease;
        }
        .btn-add-cart:hover {
            background-color: #4834d4;
            border-color: #4834d4;
            color: #ffffff;
            box-shadow: 0 4px 15px rgba(108, 92, 231, 0.3);
        }
        .related-card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.02);
            transition: all 0.25s ease;
            overflow: hidden;
        }
        .related-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 10px 25px rgba(108, 92, 231, 0.08);
        }
        .related-img-wrapper {
            position: relative;
            padding-top: 100%;
            overflow: hidden;
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
    <div class="container px-4 px-lg-5 my-4">
        <div class="row gx-4 gx-lg-5 align-items-center">
            <div class="col-md-6 mb-4 mb-md-0">
                <div class="detail-img-card border p-2">
                    <img class="img-fluid rounded-3 w-100" style="object-fit: cover; max-height: 500px;"
                         src="${pageContext.request.contextPath}/image/product/${not empty product.photo ? product.photo : 'no-sample.png'}"
                         alt="${product.name}"
                         onerror="this.src='${pageContext.request.contextPath}/image/product/no-sample.png';">
                </div>
            </div>

            <div class="col-md-6">
                <div class="badge bg-light text-secondary border mb-2 px-2 py-1.5 fw-medium">
                    <i class="bi bi-hash me-0.5"></i>Mã sản phẩm: ${product.id}
                </div>
                <h1 class="fw-bold text-dark display-6 mb-3"><c:out value="${product.name}"/></h1>

                <div class="mb-4">
                    <span class="fs-2 fw-bold text-danger">
                        <fmt:formatNumber value="${product.price != null ? product.price : 0.0}" pattern="#,###" /> VNĐ
                    </span>
                </div>

                <h6 class="fw-bold text-secondary mb-2">Mô tả hương vị:</h6>
                <p class="text-muted leading-relaxed mb-4">${not empty product.description ? product.description : 'Món uống thơm ngon độc quyền chuẩn công thức Boba Station.'}</p>

                <form action="${pageContext.request.contextPath}/addToCart" method="POST" class="pt-2">
                    <div class="d-flex flex-wrap align-items-center gap-3">
                        <div class="input-group quantity-control border" style="max-width: 130px;">
                            <button class="btn btn-outline-secondary border-0" type="button" onclick="adjustQty(-1)">-</button>
                            <input class="form-control text-center border-0 fw-bold bg-white" id="inputQuantity" name="inputQuantity"
                                   type="number" value="1" min="1" readonly />
                            <button class="btn btn-outline-secondary border-0" type="button" onclick="adjustQty(1)">+</button>
                        </div>

                        <input name="productId" type="hidden" value="${product.id}" />
                        <button class="btn btn-add-cart shadow-sm" type="submit">
                            <i class="bi bi-cart-plus-fill me-2"></i>Thêm vào giỏ hàng
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</section>

<section class="py-5 bg-light border-top">
    <div class="container px-4 px-lg-5 mt-3">
        <h4 class="fw-bold text-dark mb-4"><i class="bi bi-stars text-warning me-2"></i>Gợi ý đồ uống khác</h4>

        <c:choose>
            <c:when test="${not empty productList}">
                <div id="content" class="row gx-4 gx-lg-5 row-cols-2 row-cols-md-3 row-cols-xl-4 justify-content-center">
                    <c:forEach var="relatedProduct" items="${productList}" varStatus="status">
                        <c:if test="${status.index < 4}">
                            <div class="col mb-4 product-count">
                                <div class="card h-100 related-card">
                                    <a href="${pageContext.request.contextPath}/product?id=${relatedProduct.id}">
                                        <div class="related-img-wrapper">
                                            <img src="${pageContext.request.contextPath}/image/product/${not empty relatedProduct.photo ? relatedProduct.photo : 'no-sample.png'}"
                                                 alt="${relatedProduct.name}"
                                                 onerror="this.src='${pageContext.request.contextPath}/image/product/no-sample.png';">
                                        </div>
                                    </a>
                                    <div class="card-body p-4 text-center d-flex flex-column justify-content-between">
                                        <div>
                                            <h6 class="fw-bold mb-1">
                                                <a href="${pageContext.request.contextPath}/product?id=${relatedProduct.id}" class="text-decoration-none text-dark">
                                                    <c:out value="${relatedProduct.name}"/>
                                                </a>
                                            </h6>
                                            <div class="d-flex justify-content-center small text-warning mb-3 gap-0.5">
                                                <i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i><i class="bi bi-star-fill"></i>
                                            </div>
                                        </div>
                                        <span class="fw-bold text-primary border-top pt-2 d-inline-block w-100">
                                            <fmt:formatNumber value="${relatedProduct.price != null ? relatedProduct.price : 0.0}" pattern="#,###" /> VNĐ
                                        </span>
                                    </div>
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="col-md-12 text-center text-muted">
                    <p>Không có sản phẩm gợi ý tương thích.</p>
                </div>
            </c:otherwise>
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