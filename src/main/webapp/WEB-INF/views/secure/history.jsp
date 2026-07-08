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
    <title>Lịch sử đơn hàng - Boba Station</title>
    <link href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css" rel="stylesheet">

    <style>
        body { font-family: 'Plus Jakarta Sans', sans-serif; background-color: #fcfcfd; color: #1d1d1f; }

        .history-container { max-width: 1150px; margin: 0 auto; padding-top: 40px; padding-bottom: 80px; }

        /* Gmail-style List UI */
        .order-list-container { background: #ffffff; border: 1px solid #e2e2e9; border-radius: 16px; box-shadow: 0 4px 20px rgba(0,0,0,0.02); overflow: hidden; }
        .accordion-button { padding: 16px 20px; transition: background-color 0.2s ease; }
        .accordion-button:not(.collapsed) { background-color: #f8f9fa; color: #1d1d1f; box-shadow: inset 0 -1px 0 #e2e2e9; }
        .accordion-button:hover { background-color: #f8f9fa; }
        .accordion-button::after { display: none; }
        .accordion-item { border: none; border-bottom: 1px solid #e2e2e9; }
        .accordion-item:last-child { border-bottom: none; }

        /* Status Badges */
        .status-badge { padding: 5px 12px; border-radius: 20px; font-weight: 600; font-size: 11.5px; display: inline-flex; align-items: center; justify-content: center; min-width: 110px;}
        .status-paid { background-color: #e3fcef; color: #00b86e; }
        .status-pending { background-color: #fff3e0; color: #ff9800; }
        .status-cancelled { background-color: #ffebee; color: #f44336; }

        /* Expanded Detail Content */
        .order-detail-wrapper { background-color: #fafafc; padding: 25px; border-radius: 0 0 16px 16px; }
        .item-row { display: flex; justify-content: space-between; align-items: center; padding: 12px 0; border-bottom: 1px dashed #e2e2e9; }
        .item-row:last-child { border-bottom: none; }

        .btn-review { background-color: #fff; border: 1px solid #ffb100; color: #ffb100; font-size: 11px; font-weight: 600; border-radius: 8px; padding: 4px 10px; transition: 0.2s; }
        .btn-review:hover { background-color: #ffb100; color: #fff; }

        .btn-pdf { background-color: #6c5ce7; color: white; border-radius: 10px; font-weight: 600; padding: 8px 18px; font-size: 13px; transition: 0.3s; border: none;}
        .btn-pdf:hover { background-color: #4834d4; color: white; }
    </style>
</head>
<body>

<%@ include file="/template/includes/navbar.jsp"%>

<div class="history-container px-3">
    <div class="d-flex align-items-center mb-4 ps-2">
        <h3 class="fw-bold m-0 text-dark"><i class="bi bi-clock-history text-primary me-2"></i>Lịch sử giao dịch</h3>
    </div>

    <c:choose>
        <c:when test="${not empty orders}">
            <div class="order-list-container accordion" id="orderHistoryAccordion">
                <c:forEach var="order" items="${orders}">
                    <div class="accordion-item">
                        <h2 class="accordion-header" id="heading${order.id}">
                            <button class="accordion-button collapsed shadow-none" type="button" data-bs-toggle="collapse" data-bs-target="#collapse${order.id}" aria-expanded="false" aria-controls="collapse${order.id}">
                                <div class="d-flex w-100 justify-content-between align-items-center pe-3">
                                    <div class="d-flex align-items-center" style="width: 40%;">
                                        <div class="bg-light rounded-circle d-flex align-items-center justify-content-center me-3" style="width: 40px; height: 40px;">
                                            <i class="bi bi-receipt text-secondary fs-5"></i>
                                        </div>
                                        <div>
                                            <span class="fw-bold text-dark d-block">Đơn hàng #${order.id}</span>
                                            <span class="text-muted font-monospace" style="font-size: 12px;"><fmt:formatDate value="${order.orderDate}" pattern="HH:mm - dd/MM/yyyy"/></span>
                                        </div>
                                    </div>

                                    <div class="text-end d-none d-sm-block" style="width: 25%;">
                                        <span class="fw-bold text-dark"><fmt:formatNumber value="${order.totalPrice}" pattern="#,###"/> đ</span>
                                    </div>

                                    <div class="text-end" style="width: 35%;">
                                        <c:choose>
                                            <c:when test="${order.status == 'PAID'}"><span class="status-badge status-paid"><i class="bi bi-check2-circle me-1"></i>Đã hoàn thành</span></c:when>
                                            <c:when test="${order.status == 'CANCELLED'}"><span class="status-badge status-cancelled"><i class="bi bi-x-circle me-1"></i>Đã hủy</span></c:when>
                                            <c:otherwise><span class="status-badge status-pending"><i class="bi bi-hourglass-split me-1"></i>Đang xử lý</span></c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </button>
                        </h2>

                        <div id="collapse${order.id}" class="accordion-collapse collapse" aria-labelledby="heading${order.id}" data-bs-parent="#orderHistoryAccordion">
                            <div class="order-detail-wrapper border-top">
                                <h6 class="fw-bold text-secondary mb-3" style="font-size: 13px; text-transform: uppercase; letter-spacing: 0.5px;">Chi tiết sản phẩm</h6>

                                <div class="bg-white border rounded-3 p-3 mb-4 shadow-sm">
                                    <c:forEach var="item" items="${order.items}">
                                        <div class="item-row">
                                            <div>
                                                <span class="badge bg-primary rounded-pill me-2 px-2">${item.quantity}</span>
                                                <span class="fw-semibold text-dark" style="font-size: 14.5px;"><c:out value="${item.productName}"/></span>
                                            </div>

                                            <div class="d-flex align-items-center gap-3">
                                                <span class="fw-bold text-secondary small"><fmt:formatNumber value="${item.price * item.quantity}" pattern="#,###"/> đ</span>

                                                <c:if test="${order.status == 'PAID'}">
                                                    <!-- Đổi thẻ <a> thành thẻ <button> để gọi Modal -->
                                                    <button type="button" class="btn btn-review text-decoration-none shadow-none" data-bs-toggle="modal" data-bs-target="#reviewModal_${order.id}_${item.product.id}">
                                                        <i class="bi bi-star-fill me-1"></i>Đánh giá
                                                    </button>
                                                </c:if>
                                            </div>
                                        </div>

                                        <!-- POPUP MODAL ĐÁNH GIÁ (Nằm ngay bên dưới mỗi item) -->
                                        <c:if test="${order.status == 'PAID'}">
                                            <div class="modal fade" id="reviewModal_${order.id}_${item.product.id}" tabindex="-1" aria-hidden="true">
                                                <div class="modal-dialog modal-dialog-centered">
                                                    <div class="modal-content" style="border-radius: 18px; border: none; box-shadow: 0 15px 35px rgba(0,0,0,0.1);">
                                                        <div class="modal-header border-0 pb-0 mt-2">
                                                            <h6 class="fw-bold m-0"><i class="bi bi-chat-right-quote-fill text-primary me-2"></i>Viết đánh giá sản phẩm</h6>
                                                            <button type="button" class="btn-close shadow-none" data-bs-dismiss="modal" aria-label="Close"></button>
                                                        </div>

                                                        <form action="${pageContext.request.contextPath}/product/review" method="POST" enctype="multipart/form-data">
                                                            <div class="modal-body pt-3 pb-2">
                                                                <input type="hidden" name="productId" value="${item.product.id}">

                                                                <input type="hidden" name="orderId" value="${order.id}">

                                                                <div class="bg-light p-3 rounded-3 mb-3 d-flex align-items-center">
                                                                    <div class="badge bg-primary rounded-circle p-2 me-3"><i class="bi bi-cup-straw"></i></div>
                                                                    <span class="fw-bold text-dark"><c:out value="${item.productName}"/></span>
                                                                </div>

                                                                <div class="mb-3">
                                                                    <label class="form-label text-secondary small fw-medium">Đánh giá sao:</label>
                                                                    <select name="rating" class="form-select form-select-sm rounded-3 shadow-none">
                                                                        <option value="5">⭐⭐⭐⭐⭐ Tuyệt vời</option>
                                                                        <option value="4">⭐⭐⭐⭐ Rất tốt</option>
                                                                        <option value="3">⭐⭐⭐ Bình thường</option>
                                                                        <option value="2">⭐⭐ Tạm được</option>
                                                                        <option value="1">⭐ Tệ</option>
                                                                    </select>
                                                                </div>

                                                                <div class="mb-3">
                                                                    <label class="form-label text-secondary small fw-medium">Ảnh thực tế (Tùy chọn):</label>
                                                                    <input type="file" name="reviewImage" accept="image/*" class="form-control form-control-sm rounded-3 shadow-none">
                                                                </div>

                                                                <div class="mb-2">
                                                                    <label class="form-label text-secondary small fw-medium">Chi tiết cảm nhận:</label>
                                                                    <textarea name="comment" class="form-control rounded-3 shadow-none" rows="3" placeholder="Bạn có cảm nhận như thế nào?" required></textarea>
                                                                </div>
                                                            </div>
                                                            <div class="modal-footer border-0 pt-0 pb-3 justify-content-center">
                                                                <button type="button" class="btn btn-light rounded-pill px-4 fw-medium text-secondary border" data-bs-dismiss="modal">Hủy</button>
                                                                <button type="submit" class="btn btn-pdf rounded-pill px-4 fw-medium">Gửi đánh giá</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </div>
                                            </div>
                                        </c:if>
                                        <!-- END POPUP MODAL -->

                                    </c:forEach>
                                </div>

                                <div class="d-flex flex-wrap justify-content-between align-items-end pt-2">
                                    <div>
                                        <div class="text-muted small mb-1">Phương thức: <span class="fw-medium text-dark">${order.paymentMethod != null ? order.paymentMethod : 'Tiền mặt'}</span></div>
                                        <div class="text-muted small mb-2">Phí vận chuyển: <span class="fw-medium text-dark">15,000 đ</span></div>
                                        <span class="fs-5 fw-bold text-danger">Tổng thu: <fmt:formatNumber value="${order.totalPrice}" pattern="#,###"/> đ</span>
                                    </div>

                                    <c:if test="${order.status == 'PAID'}">
                                        <a href="${pageContext.request.contextPath}/secure/generateInvoicePDF?orderId=${order.id}" class="btn btn-pdf mt-3 mt-sm-0 text-decoration-none shadow-sm">
                                            <i class="bi bi-cloud-arrow-down-fill me-2"></i>Tải biên lai PDF
                                        </a>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>

        <c:otherwise>
            <div class="text-center py-5 bg-white border shadow-sm" style="border-radius: 20px;">
                <img src="https://cdn-icons-png.flaticon.com/512/11329/11329060.png" width="120" class="mb-4 opacity-50" alt="Empty Box">
                <h5 class="fw-bold mt-2 text-dark">Bạn chưa có đơn hàng nào</h5>
                <p class="text-secondary mb-4">Hãy trải nghiệm thử các món đồ uống tuyệt vời tại Boba Station nhé!</p>
                <a href="${pageContext.request.contextPath}/" class="btn btn-pdf px-4 py-2 rounded-pill text-decoration-none">Khám phá menu ngay</a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<%@ include file="/template/includes/footer.jsp"%>
</body>
</html>