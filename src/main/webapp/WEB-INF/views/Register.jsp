<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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

            <%-- Hiển thị lỗi tổng quan hệ thống nếu có --%>
            <form:errors element="div" cssClass="alert alert-danger text-center" />

            <%-- Khởi tạo Spring Form liên kết với Object userDTO --%>
            <form:form action="${pageContext.request.contextPath}/register" method="post" modelAttribute="userDTO">
              <div class="row gy-3 overflow-hidden">

                <div class="col-12">
                  <div class="form-floating mb-3">
                    <form:input path="username" class="form-control" placeholder="Username" />
                    <label class="form-label">Username</label>
                    <form:errors path="username" cssClass="text-danger small" />
                  </div>
                </div>

                <div class="col-12">
                  <div class="form-floating mb-3">
                    <form:input path="email" type="email" class="form-control" id="email" placeholder="name@example.com" />
                    <label class="form-label">Email</label>
                      <%-- Lỗi từ Backend Validation --%>
                    <form:errors path="email" cssClass="text-danger small d-block" />
                      <%-- Thông báo từ AJAX Frontend --%>
                    <div id="emailAjaxMsg" class="small mt-1 fw-bold"></div>
                  </div>
                </div>

                <div class="col-12">
                  <div class="form-floating mb-3">
                    <form:input path="phone_number" class="form-control" placeholder="Phone Number" />
                    <label class="form-label">Phone Number</label>
                    <form:errors path="phone_number" cssClass="text-danger small" />
                  </div>
                </div>

                <div class="col-12">
                  <div class="form-floating mb-3">
                    <form:input path="address" class="form-control" placeholder="Address" />
                    <label class="form-label">Address</label>
                    <form:errors path="address" cssClass="text-danger small" />
                  </div>
                </div>

                <div class="col-12">
                  <div class="form-floating mb-3">
                    <form:password path="password" class="form-control" placeholder="Password" />
                    <label class="form-label">Password</label>
                    <form:errors path="password" cssClass="text-danger small" />
                  </div>
                </div>

                <div class="col-12">
                  <div class="form-floating mb-3">
                    <form:password path="repass" class="form-control" placeholder="Confirm Password" />
                    <label class="form-label">Confirm Password</label>
                    <form:errors path="repass" cssClass="text-danger small" />
                  </div>
                </div>

                <div class="col-12">
                  <div class="form-check">
                    <form:checkbox path="iAgree" cssClass="form-check-input" id="iAgree" />
                    <label class="form-check-label text-secondary" for="iAgree">
                      I agree to the <a href="#!" class="link-primary text-decoration-none">terms and conditions</a>
                    </label>
                  </div>
                  <form:errors path="iAgree" cssClass="text-danger small d-block" />
                </div>

                <div class="col-12">
                  <div class="d-grid">
                    <button class="btn btn-primary btn-lg" type="submit">Sign up</button>
                  </div>
                </div>
              </div>
            </form:form>

            <div class="row">
              <div class="col-12">
                <hr class="mt-5 mb-4 border-secondary-subtle">
                <p class="m-0 text-secondary text-center">Already have an account?
                  <a href="${pageContext.request.contextPath}/login" class="link-primary text-decoration-none">Sign in</a>
                </p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</section>

<%@ include file="/template/includes/footer.jsp"%>
<script>
  document.addEventListener("DOMContentLoaded", function() {
    const emailInput = document.getElementById("email");
    const emailMsg = document.getElementById("emailAjaxMsg");

    // Lắng nghe sự kiện 'blur' (khi người dùng click chuột ra ngoài ô nhập email)
    emailInput.addEventListener("blur", function() {
      const emailValue = emailInput.value.trim();

      // Nếu có nhập liệu thì mới gọi AJAX
      if (emailValue !== "") {
        // Hiển thị trạng thái đang kiểm tra
        emailMsg.innerHTML = "<span class='text-secondary'><i class='bi bi-hourglass-split'></i> Đang kiểm tra...</span>";
        emailInput.classList.remove("is-invalid", "is-valid");

        // Gọi API bằng công nghệ Fetch (AJAX)
        fetch('${pageContext.request.contextPath}/api/check-email?email=' + encodeURIComponent(emailValue))
                .then(response => response.json())
                .then(data => {
                  if (data.exists) {
                    // Nếu email ĐÃ TỒN TẠI
                    emailInput.classList.add("is-invalid"); // Viền đỏ của Bootstrap
                    emailMsg.innerHTML = "<span class='text-danger'><i class='bi bi-x-circle'></i> Email này đã được đăng ký!</span>";
                  } else {
                    // Nếu email CHƯA TỒN TẠI (Có thể dùng)
                    emailInput.classList.add("is-valid"); // Viền xanh của Bootstrap
                    emailMsg.innerHTML = "<span class='text-success'><i class='bi bi-check-circle'></i> Email hợp lệ.</span>";
                  }
                })
                .catch(error => {
                  console.error("Lỗi AJAX:", error);
                  emailMsg.innerHTML = "";
                });
      } else {
        // Reset nếu ô trống
        emailInput.classList.remove("is-invalid", "is-valid");
        emailMsg.innerHTML = "";
      }
    });
  });
</script>


</body>
</html>