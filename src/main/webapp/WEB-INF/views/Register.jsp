<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Registration</title>
  <link rel="icon" type="image/x-icon" href="${pageContext.request.contextPath}/assets/favicon.ico" />
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.5.0/font/bootstrap-icons.css" rel="stylesheet" />
  <link href="${pageContext.request.contextPath}/css/styles.css" rel="stylesheet" />
</head>
<body>

<%@ include file="/template/includes/navbar.jsp"%>

<section class="bg-light p-3 p-md-4 p-xl-5">
  <div class="container">
    <div class="row justify-content-center">
      <div class="col-12 col-md-9 col-lg-7 col-xl-6 col-xxl-5">
        <div class="card border border-light-subtle rounded-4">
          <div class="card-body p-3 p-md-4 p-xl-5">
            <div class="row">
              <div class="col-12">
                <div class="mb-5">
                  <div class="text-center mb-4">
                    <a href="${pageContext.request.contextPath}/home">
                      <img src="${pageContext.request.contextPath}/image/website/logo.png" alt="Boba Station Logo" width="150">
                    </a>
                  </div>
                  <h2 class="fs-6 fw-normal text-center text-secondary mb-4">Enter your details to register</h2>
                </div>
              </div>
            </div>

            <%-- ĐÃ SỬA: Đổi từ Rmessage thành error để khớp với Controller --%>
            <c:if test="${not empty error}">
              <div class="alert alert-danger text-center" role="alert">
                  ${error}
              </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/register" method="post">
              <div class="row gy-3 overflow-hidden">
                <div class="col-12">
                  <div class="form-floating mb-3">
                    <input type="text" class="form-control" name="username" id="username" placeholder="Username" required>
                    <label class="form-label">Username</label>
                  </div>
                </div>
                <div class="col-12">
                  <div class="form-floating mb-3">
                    <input type="email" class="form-control" name="email" id="email" placeholder="name@example.com" required>
                    <label class="form-label">Email</label>
                  </div>
                </div>

                <%-- ĐÃ BỔ SUNG: Ô nhập số điện thoại --%>
                <div class="col-12">
                  <div class="form-floating mb-3">
                    <input type="text" class="form-control" name="phone_number" id="phone_number" placeholder="Phone Number" required>
                    <label class="form-label">Phone Number</label>
                  </div>
                </div>

                <%-- ĐÃ BỔ SUNG: Ô nhập địa chỉ --%>
                <div class="col-12">
                  <div class="form-floating mb-3">
                    <input type="text" class="form-control" name="address" id="address" placeholder="Address" required>
                    <label class="form-label">Address</label>
                  </div>
                </div>

                <div class="col-12">
                  <div class="form-floating mb-3">
                    <input type="password" class="form-control" name="password" id="password" placeholder="Password" required>
                    <label class="form-label">Password</label>
                  </div>
                </div>
                <div class="col-12">
                  <div class="form-floating mb-3">
                    <input type="password" class="form-control" name="repass" id="repass" placeholder="Confirm Password" required>
                    <label class="form-label">Confirm Password</label>
                  </div>
                </div>
                <div class="col-12">
                  <div class="form-check">
                    <input class="form-check-input" type="checkbox" value="" name="iAgree" id="iAgree" required>
                    <label class="form-check-label text-secondary" for="iAgree">
                      I agree to the <a href="#!" class="link-primary text-decoration-none">terms and conditions</a>
                    </label>
                  </div>
                </div>
                <div class="col-12">
                  <div class="d-grid">
                    <button class="btn btn-primary btn-lg" type="submit">Sign up</button>
                  </div>
                </div>
              </div>
            </form>
            <div class="row">
              <div class="col-12">
                <hr class="mt-5 mb-4 border-secondary-subtle">
                <p class="m-0 text-secondary text-center">Already have an account? <a href="${pageContext.request.contextPath}/login" class="link-primary text-decoration-none">Sign in</a></p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

<%@ include file="/template/includes/footer.jsp"%>

</body>
</html>