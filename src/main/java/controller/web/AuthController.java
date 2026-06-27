package controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.SQLException;

import models.User;
import dao.UserDAO;
import dao.DBConnectionPool;

@Controller
public class AuthController {

    // ================= 1. ĐĂNG NHẬP =================
    @GetMapping("/login")
    public String showLoginPage() {
        return "Login";
    }

    @PostMapping("/login")
    public String processLogin(
            @RequestParam("username") String username, // Ở form Login, ô này sẽ chứa Email của người dùng
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        // Phải mở connection (try-with-resources) để truyền vào UserDAO
        try (Connection conn = DBConnectionPool.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);

            // Hàm getLogin của bạn nhận tham số là (email, password)
            User user = userDAO.getLogin(username, password);

            if (user != null) {
                session.setAttribute("acc", user);
                return "redirect:/home";
            } else {
                model.addAttribute("message", "Sai email hoặc mật khẩu!");
                return "Login";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("message", "Lỗi kết nối cơ sở dữ liệu!");
            return "Login";
        }
    }

    // ================= 2. ĐĂNG KÝ =================
    @GetMapping("/register")
    public String showRegisterPage() {
        return "Register";
    }

    @PostMapping("/register")
    public String processRegister(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("repass") String repass,
            @RequestParam(value = "email", required = false) String email,
            Model model) {

        if (!password.equals(repass)) {
            model.addAttribute("Rmessage", "Mật khẩu xác nhận không khớp!");
            return "Register";
        }

        try (Connection conn = DBConnectionPool.getConnection()) {
            UserDAO userDAO = new UserDAO(conn);

            // 1. Kiểm tra xem Username đã bị trùng chưa
            if (userDAO.checkUsername(username)) {
                model.addAttribute("Rmessage", "Tên đăng nhập đã tồn tại!");
                return "Register";
            }

            // 2. Kiểm tra xem Email đã bị trùng chưa
            if (email != null && userDAO.checkEmailExist(email)) {
                model.addAttribute("Rmessage", "Email này đã được sử dụng!");
                return "Register";
            }

            // 3. Tạo đối tượng User mới và gọi hàm registerUser()
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setPhone(""); // Tạm thời để rỗng vì form đăng ký không có trường số điện thoại

            userDAO.registerUser(newUser);

            model.addAttribute("message", "Đăng ký thành công! Hãy đăng nhập.");
            return "Login";

        } catch (SQLException e) {
            e.printStackTrace();
            model.addAttribute("Rmessage", "Đã xảy ra lỗi hệ thống khi đăng ký!");
            return "Register";
        }
    }

    // ================= 3. ĐĂNG XUẤT =================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("acc");
        return "redirect:/home";
    }
}