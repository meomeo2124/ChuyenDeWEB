package controller.web;

import dao.UserDAO;
import dao.DBConnectionPool;
import jakarta.servlet.http.HttpSession;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import utool.HelperClass;
import utool.JavaMailUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;

@Controller
public class RegisterController {

    // 1. BỔ SUNG: Hiển thị trang đăng ký (Xử lý request GET khi click từ Login sang)
    @GetMapping("/register")
    public String showRegisterPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error); // Hứng thông báo lỗi nếu có
        }
        return "Register"; // Trỏ đến /WEB-INF/views/Register.jsp
    }

    // 2. Xử lý dữ liệu form đăng ký gửi lên bằng POST
    @PostMapping("/register")
    public String handleRegister(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 @RequestParam("email") String email,
                                 @RequestParam("address") String address,
                                 @RequestParam("phone_number") String phoneNumber,
                                 HttpSession session) {

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userdao = new UserDAO(connection);

            // Kiểm tra email tồn tại
            if (userdao.findByEmail(email) != null) {
                // ĐÃ SỬA: Redirect về GET kèm thông báo lỗi để tránh kẹt trạng thái POST
                String errMsg = URLEncoder.encode("Email này đã được đăng ký!", StandardCharsets.UTF_8);
                return "redirect:/register?error=" + errMsg;
            }

            // Map dữ liệu vào User tạm
            User tempUser = new User();
            tempUser.setUsername(username);
            tempUser.setPassword(password);
            tempUser.setEmail(email);
            tempUser.setAddress(address);
            tempUser.setPhone(phoneNumber);
            tempUser.setIsAdmin(false);
            tempUser.setImg("image/avatars/default-avatar.png");

            // Sinh mã OTP và lưu Session
            int authCode = HelperClass.generateRandom();
            session.setAttribute("authCode", authCode);
            session.setAttribute("tempUser", tempUser);

            // Gửi email bất đồng bộ
            new Thread(() -> {
                try {
                    JavaMailUtil.sendEmail(email, authCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            // ĐÃ SỬA: Redirect chuẩn sang trang nhập mã OTP thay vì return View trực tiếp
            return "redirect:/getAuthCode";

        } catch (Exception e) {
            String errMsg = URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/register?error=" + errMsg;
        }
    }
}