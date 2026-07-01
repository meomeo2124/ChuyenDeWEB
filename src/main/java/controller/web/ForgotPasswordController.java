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
import java.util.logging.Logger;

@Controller
public class ForgotPasswordController {

    private static final Logger LOGGER = Logger.getLogger(ForgotPasswordController.class.getName());

    // 1. Hiển thị trang Nhập email / Nhập mã OTP
    @GetMapping("/getAuthCode")
    public String showResetPasswordPage(HttpSession session) {
        String code = String.valueOf(session.getAttribute("authCode"));
        LOGGER.info("Current session authCode: " + code);
        return "ResetPassword";
    }

    // 2. Chặn lỗi 405 khi hệ thống tự động chuyển hướng hoặc người dùng truy cập trực tiếp bằng GET
    @GetMapping("/VerifyEmail")
    public String handleVerifyEmailGet() {
        return "redirect:/getAuthCode";
    }

    // 3. Xử lý nhận Email và gửi mã xác nhận
    @PostMapping("/getAuthCode")
    public String handleGetAuthCode(@RequestParam("email") String email, HttpSession session, Model model) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userdao = new UserDAO(connection);
            User user = userdao.findByEmail(email);

            if (user == null) {
                model.addAttribute("message", "Không tìm thấy người dùng với email này.");
                return "ResetPassword";
            }

            int authCode = HelperClass.generateRandom();
            session.setAttribute("authCode", authCode);
            session.setAttribute("userEmail", user.getEmail());

            new Thread(() -> {
                try {
                    JavaMailUtil.sendEmail(email, authCode);
                    System.out.println("Email đã được gửi đến: " + email);
                } catch (Exception e) {
                    System.err.println("Gửi email thất bại: " + e.getMessage());
                }
            }).start();

            return "ResetPassword";
        } catch (Exception e) {
            LOGGER.severe("Database connection error: " + e.getMessage());
            model.addAttribute("message", "Lỗi kết nối hệ thống dữ liệu.");
            return "ResetPassword";
        }
    }

    // 4. Xử lý xác minh mã OTP người dùng gửi lên bằng POST
    @PostMapping("/VerifyEmail")
    public String handleVerifyEmail(@RequestParam(value = "authCode", required = false) String userInputStr,
                                    HttpSession session, Model model) {
        Integer authCode = (Integer) session.getAttribute("authCode");

        if (userInputStr == null || userInputStr.trim().isEmpty()) {
            model.addAttribute("message", "Mã xác minh không được để trống");
            return "ResetPassword";
        }

        try {
            int userInput = Integer.parseInt(userInputStr);
            if (authCode != null && authCode.equals(userInput)) {

                // KIỂM TRA LUỒNG: ĐĂNG KÝ TÀI KHOẢN MỚI
                User tempUser = (User) session.getAttribute("tempUser");
                if (tempUser != null) {
                    try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
                        UserDAO userdao = new UserDAO(connection);
                        userdao.insertUser(tempUser);

                        // Dọn dẹp session sạch sẽ sau khi đăng ký thành công
                        session.removeAttribute("tempUser");
                        session.removeAttribute("authCode");

                        String successMsg = URLEncoder.encode("Đăng ký thành công! Vui lòng đăng nhập.", StandardCharsets.UTF_8);
                        // ĐÃ SỬA: Đổi từ success= sang msg= để AuthController hứng chuẩn xác
                        return "redirect:/login?msg=" + successMsg;
                    } catch (Exception e) {
                        model.addAttribute("message", "Lỗi khi lưu tài khoản: " + e.getMessage());
                        return "ResetPassword";
                    }
                }

                // KIỂM TRA LUỒNG: QUÊN MẬT KHẨU THÔNG THƯỜNG
                return "ChangePassword";
            } else {
                model.addAttribute("message", "Sai mã xác minh");
                return "ResetPassword";
            }
        } catch (NumberFormatException e) {
            model.addAttribute("message", "Mã xác minh không hợp lệ");
            return "ResetPassword";
        }
    }
}