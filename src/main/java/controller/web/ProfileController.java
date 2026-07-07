package controller.web;

import dao.UserDAO;
import jakarta.servlet.http.HttpSession;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;

@Controller
public class ProfileController {

    private final UserDAO userDAO;

    @Autowired
    public ProfileController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/secure/edit")
    public String showEditProfilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "secure/editProfile";
    }

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

        try {
            String cleanedPhone = phone.trim();
            if (!cleanedPhone.matches("\\d{10,11}")) {
                model.addAttribute("message", "Số điện thoại không hợp lệ, vui lòng nhập từ 10 đến 11 chữ số!");
                model.addAttribute("user", user);
                return "secure/editProfile";
            }

            boolean isUpdated = userDAO.editProfile(user, username, email, cleanedPhone, address);

            if (isUpdated) {
                session.setAttribute("user", userDAO.findById(user.getId()));
                return "redirect:/";
            } else {
                String errorMsg = URLEncoder.encode("Cập nhật thất bại", StandardCharsets.UTF_8);
                return "redirect:/secure/edit?error=" + errorMsg;
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Có lỗi xảy ra: " + e.getMessage());
            model.addAttribute("user", user);
            return "secure/editProfile";
        }
    }

    @GetMapping("/ChangePassword")
    public String showChangePasswordPage(HttpSession session) {
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }
        return "ChangePassword";
    }

    @PostMapping("/ChangePassword")
    public String handleChangePassword(
            @RequestParam("password1") String p1,
            @RequestParam("password2") String p2,
            HttpSession session,
            Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        String email = user.getEmail();

        if (p1 != null && p1.equals(p2)) {
            try {
                userDAO.updatePassword(email, p1);
                session.invalidate();
                return "redirect:/login?msg=" + URLEncoder.encode("Đổi mật khẩu thành công! Vui lòng đăng nhập lại.", StandardCharsets.UTF_8);
            } catch (SQLException e) {
                e.printStackTrace();
                model.addAttribute("message", "Có lỗi xảy ra khi cập nhật mật khẩu. Vui lòng thử lại.");
                return "ChangePassword";
            }
        } else {
            model.addAttribute("message", "Mật khẩu nhập lại không trùng khớp!");
            return "ChangePassword";
        }
    }
}