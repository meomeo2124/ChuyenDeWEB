<%@ page pageEncoding="UTF-8" language="java" %>
<%-- CHUYỂN ĐỔI SANG CHUẨN JAKARTA TRÁNH XUNG ĐỘT PREFIX C --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<section class="bg-light py-3 py-md-5">
	<div class="container">
		<div class="row justify-content-center">
			<div class="col-12 col-sm-10 col-md-8 col-lg-6 col-xl-5 col-xxl-4">
				<div class="card border border-light-subtle rounded-3 shadow-sm">
					<div class="card-body p-3 p-md-4 p-xl-5">
						<div class="text-center mb-3">
							<img src="${pageContext.request.contextPath}/image/website/logo.png" alt="Logo" width="100" height="100">
						</div>

						<%-- Đoạn này để debug mã OTP, khi chạy production bạn có thể xóa đi --%>
						<p class="text-muted small text-center">Debug Code: ${sessionScope.authCode}</p>

						<c:choose>
							<c:when test="${empty sessionScope.authCode}">
								<h2 class="fs-6 fw-normal text-center text-secondary mb-4">Enter your Email</h2>
								<div>
										<%-- Điều hướng chuẩn qua Spring Controller --%>
									<form action="${pageContext.request.contextPath}/getAuthCode" method="POST">
										<div class="form-floating mb-3">
											<input type="email" class="form-control" name="email" id="email"
												   placeholder="name@example.com" required>
											<label for="email" class="form-label">Email</label>
										</div>
										<p class="text-danger">${message}</p>
										<div class="d-grid my-3">
											<button class="btn btn-primary btn-lg" type="submit">Get Code</button>
										</div>
									</form>
								</div>
							</c:when>

							<c:otherwise>
								<h2 class="fs-6 fw-normal text-center text-secondary mb-4">Enter Auth Code sent to email</h2>
								<%-- Điều hướng chuẩn qua Spring Controller --%>
								<form action="${pageContext.request.contextPath}/VerifyEmail" method="POST">
									<div class="col-12">
										<div class="form-floating mb-3">
											<input type="number" class="form-control" name="authCode" id="authCode"
												   placeholder="123456" required>
											<label for="authCode" class="form-label">Code</label>
										</div>
									</div>
									<div class="col-12">
										<p class="text-danger">${message}</p>
										<div class="d-grid my-3">
											<button class="btn btn-primary btn-lg" type="submit">Submit</button>
										</div>
									</div>
								</form>
							</c:otherwise>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>