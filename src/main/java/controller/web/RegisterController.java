package controller.web;

import dao.UserDAO;
import models.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import utool.HelperClass;
import utool.JavaMailUtil;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
public class RegisterController {

    private final UserDAO userDAO;

    @Autowired
    public RegisterController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/register")
    public String showRegisterPage(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "Register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam("username") String username,
                                 @RequestParam("password") String password,
                                 @RequestParam("email") String email,
                                 @RequestParam("address") String address,
                                 @RequestParam("phone_number") String phoneNumber,
                                 HttpSession session) {

        try {
            if (userDAO.findByEmail(email) != null) {
                String errMsg = URLEncoder.encode("Email này đã được đăng ký!", StandardCharsets.UTF_8);
                return "redirect:/register?error=" + errMsg;
            }

            User tempUser = new User();
            tempUser.setUsername(username);
            tempUser.setPassword(password);
            tempUser.setEmail(email);
            tempUser.setAddress(address);
            tempUser.setPhone(phoneNumber);
            tempUser.setIsAdmin(false);
            tempUser.setImg("image/avatars/default-avatar.png");

            int authCode = HelperClass.generateRandom();
            session.setAttribute("authCode", authCode);
            session.setAttribute("tempUser", tempUser);

            new Thread(() -> {
                try {
                    JavaMailUtil.sendEmail(email, authCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            return "redirect:/getAuthCode";

        } catch (Exception e) {
            String errMsg = URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/register?error=" + errMsg;
        }
    }
}