package controller.web;

import dao.DBConnectionPool;
import dao.UserDAO;
import models.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;

@Controller
public class ProfileController {

    // =========================================================================
    // 1. HIỂN THỊ TRANG CHỈNH SỬA THÔNG TIN CÁ NHÂN (Thay thế doGet của EditUserProfileServlet)
    // =========================================================================
    @GetMapping("/secure/edit")
    public String showEditProfilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Đưa thuộc tính user vào model để hiển thị ngoài giao diện giống Servlet cũ
        model.addAttribute("user", user);

        // Trỏ tới file WEB-INF/views/secure/editProfile.jsp
        return "secure/editProfile";
    }

    // =========================================================================
    // 2. XỬ LÝ CẬP NHẬT THÔNG TIN CÁ NHÂN (Thay thế doPost của EditUserProfileServlet)
    // =========================================================================
    @PostMapping("/secure/edit")
    public String handleUpdateProfile(
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone,
            @RequestParam("address") String address,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);

            // Ép kiểu chuỗi phone sang int để truyền vào hàm editProfile gốc trong UserDAO
            int phoneInt = Integer.parseInt(phone.trim());

            // Gọi đúng hàm editProfile trả về boolean theo đúng logic gốc của bạn
            boolean isUpdated = userDAO.editProfile(user, username, email, phoneInt, address);

            if (isUpdated) {
                // Đọc lại dữ liệu mới nhất từ DB thông qua hàm findById để làm mới Session
                session.setAttribute("user", userDAO.findById(user.getId()));

                // Đồng bộ chuyển hướng về trang chủ sau khi lưu thành công giống Servlet gốc
                return "redirect:/";
            } else {
                String errorMsg = URLEncoder.encode("Cập nhật thất bại", StandardCharsets.UTF_8);
                return "redirect:/secure/edit?error=" + errorMsg;
            }

        } catch (NumberFormatException e) {
            model.addAttribute("message", "Số điện thoại không hợp lệ, vui lòng chỉ nhập số!");
            return "secure/editProfile";
        } catch (SQLException e) {
            e.printStackTrace();
            // Điều hướng về trang báo lỗi hệ thống nếu dính lỗi SQL
            return "error";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            return "secure/editProfile";
        }
    }

    // =========================================================================
    // 3. HIỂN THỊ TRANG ĐỔI MẬT KHẨU (Thay thế cho doGet của ChangePassword)
    // =========================================================================
    @GetMapping("/ChangePassword")
    public String showChangePasswordPage() {
        return "ChangePassword"; // Trỏ tới WEB-INF/views/ChangePassword.jsp
    }

    // =========================================================================
    // 4. XỬ LÝ NGHIỆP VỤ ĐỔI MẬT KHẨU (Thay thế cho doPost của ChangePassword)
    // =========================================================================
    @PostMapping("/ChangePassword")
    public String handleChangePassword(
            @RequestParam("password1") String p1,
            @RequestParam("password2") String p2,
            HttpSession session,
            Model model) {

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);

// Thay vì lấy "userEmail", hãy lấy trực tiếp từ đối tượng user trong session
            User user = (User) session.getAttribute("user");
            String email = (user != null) ? user.getEmail() : null;            System.out.println("mail la " + email);
            System.out.println("mail la " + p1);

            if (p1 != null && p1.equals(p2)) {
                try {
                    userDAO.updatePassword(email, p1);
                    session.invalidate(); // Xóa phiên làm việc cũ

                    // Chuyển hướng về trang đăng nhập của Spring MVC
                    return "redirect:/login";
                } catch (SQLException e) {
                    e.printStackTrace();
                    model.addAttribute("message", "Có lỗi xảy ra khi cập nhật mật khẩu. Vui lòng thử lại.");
                    return "ChangePassword";
                }
            } else {
                model.addAttribute("message", "mật khẩu không giống nhau");
                return "ChangePassword";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Lỗi kết nối cơ sở dữ liệu hệ thống.");
            return "ChangePassword";
        }
    }
}