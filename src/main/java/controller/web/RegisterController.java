package controller.web;

import dao.DBConnectionPool;
import dao.UserDAO;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;

@Controller
public class RegisterController {

    // 1. Hiển thị trang Đăng ký
    @GetMapping("/register")
    public String showRegisterPage() {
        return "Register"; // Tìm file WEB-INF/views/Register.jsp
    }

    // 2. Tiếp nhận dữ liệu từ Form Đăng ký
    @PostMapping("/register")
    public String handleRegister(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String pass,
            @RequestParam("repass") String repass,
            Model model) {

        // Kiểm tra mật khẩu nhập lại có khớp không
        if (!pass.equals(repass)) {
            model.addAttribute("Rmessage", "Mật khẩu xác nhận không trùng khớp!");
            return "Register";
        }

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);

            // Kiểm tra xem email này đã tồn tại chưa
            User existingUser = userDAO.findByEmail(email);
            if (existingUser != null) {
                model.addAttribute("Rmessage", "Email này đã tồn tại trong hệ thống!");
                return "Register";
            }

            // Tiến hành tạo mới tài khoản
            User newUser = new User();
            newUser.setName(username); // Gán trường username vào cột Name của đối tượng User
            newUser.setEmail(email);

            // LƯU Ý: Nếu hệ thống cũ của bạn cần setPassword trực tiếp, hãy uncomment dòng dưới:
            // newUser.setPassword(pass);

            // Gọi hàm lưu của dự án cũ
            userDAO.insertUser(newUser);

            // Đăng ký thành công -> Đưa qua trang login kèm thông báo
            model.addAttribute("message", "Đăng ký thành công! Vui lòng đăng nhập.");
            return "Login";

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("Rmessage", "Có lỗi xảy ra trong quá trình xử lý hệ thống.");
            return "Register";
        }
    }
}