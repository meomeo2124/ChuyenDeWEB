<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Login into Boba Station</title>
	<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css" rel="stylesheet" />
	<link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet" />
	<script src="https://accounts.google.com/gsi/client" async defer></script>
</head>
<body>

<%@ include file="/template/includes/navbar.jsp"%>

<section class="bg-light py-3 py-md-5">
	<div class="container">
		<div class="row justify-content-center">
			<div class="col-12 col-sm-10 col-md-8 col-lg-6 col-xl-5 col-xxl-4">
				<div class="card border border-light-subtle rounded-3 shadow-sm">
					<div class="card-body p-3 p-md-4 p-xl-5">
						<div class="text-center mb-3">
							<a href="${pageContext.request.contextPath}/home">
								<img src="${pageContext.request.contextPath}/image/website/logo.png" alt="Logo" width="100" height="100">
							</a>
						</div>
						<h2 class="fs-6 fw-normal text-center text-secondary mb-4">Sign in to your account</h2>

						<c:if test="${not empty message}">
							<div class="alert alert-danger text-center" role="alert">
									${message}
							</div>
						</c:if>

						<form action="${pageContext.request.contextPath}/login" method="post">
							<div class="row gy-3 overflow-hidden">
								<div class="col-12">
									<div class="form-floating mb-3">
										<input type="email" class="form-control" name="email" id="email" placeholder="name@example.com" required>
										<label for="email" class="form-label">Email Address</label>
									</div>
								</div>
								<div class="col-12">
									<div class="form-floating mb-3">
										<input type="password" class="form-control" name="password" id="password" placeholder="Password" required>
										<label class="form-label">Password</label>
									</div>
								</div>
								<div class="col-12">
									<div class="d-grid my-3">
										<button class="btn btn-primary btn-lg" type="submit">Log in</button>
									</div>
								</div>
								<div class="col-12">
									<p class="m-0 text-secondary text-center">
										Don't have an account? <a href="${pageContext.request.contextPath}/register" class="link-primary text-decoration-none">Sign up</a>
									</p>
								</div>

								<div class="col-12 text-center mt-3">
									<div id="g_id_onload"
									     data-client_id="564628514231-g4733rfvad9m98vffpn5iofj3ht90u1t.apps.googleusercontent.com"
									     data-login_uri="http://localhost:8080${pageContext.request.contextPath}/login"
									     data-auto_prompt="false">
									</div>
									<div class="g_id_signin"
									     data-type="standard"
									     data-shape="rectangular"
									     data-theme="outline"
									     data-text="sign_in_with"
									     data-size="large"
									     data-logo_alignment="left">
									</div>
								</div>
							</div>
						</form>
					</div>
				</div>
			</div>
		</div>
	</div>
</section>

<%@ include file="/template/includes/footer.jsp"%>

</body>
</html>