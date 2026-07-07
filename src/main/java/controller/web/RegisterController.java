package controller.web;

import dao.UserDAO;
import dto.UserRegisterDTO;
import models.User;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid; // Nhớ import thư viện này
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import utool.HelperClass;
import utool.JavaMailUtil;

@Controller
public class RegisterController {

    private final UserDAO userDAO;

    @Autowired
    public RegisterController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // Trải ra 1 object rỗng để Spring Form Tag có thể bind dữ liệu
        model.addAttribute("userDTO", new UserRegisterDTO());
        return "Register";
    }

    @PostMapping("/register")
    public String handleRegister(@Valid @ModelAttribute("userDTO") UserRegisterDTO userDTO,
                                 BindingResult bindingResult,
                                 HttpSession session) {

        // 1. Kiểm tra validation cơ bản từ DTO (trống, sai format...)
        if (bindingResult.hasErrors()) {
            return "Register"; // Trả về lại trang đăng ký, Spring sẽ tự động binding lỗi lên JSP
        }

        // 2. Kiểm tra nghiệp vụ: repass có khớp password không?
        if (!userDTO.getPassword().equals(userDTO.getRepass())) {
            bindingResult.rejectValue("repass", "error.userDTO", "Mật khẩu nhập lại không khớp!");
            return "Register";
        }

        // 3. Kiểm tra nghiệp vụ: Email đã tồn tại chưa?
        try {
            if (userDAO.findByEmail(userDTO.getEmail()) != null) {
                bindingResult.rejectValue("email", "error.userDTO", "Email này đã được đăng ký!");
                return "Register";
            }

            // 4. Nếu qua hết các lỗi, map DTO sang Entity User
            User tempUser = new User();
            tempUser.setUsername(userDTO.getUsername());
            tempUser.setPassword(userDTO.getPassword());
            tempUser.setEmail(userDTO.getEmail());
            tempUser.setAddress(userDTO.getAddress());
            tempUser.setPhone(userDTO.getPhone_number());
            tempUser.setIsAdmin(false);
            tempUser.setImg("image/avatars/default-avatar.png");

            int authCode = HelperClass.generateRandom();
            session.setAttribute("authCode", authCode);
            session.setAttribute("tempUser", tempUser);

            new Thread(() -> {
                try {
                    JavaMailUtil.sendEmail(userDTO.getEmail(), authCode);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            return "redirect:/getAuthCode";

        } catch (Exception e) {
            // Lỗi hệ thống chung (Database rớt, v.v.)
            bindingResult.reject("globalError", "Lỗi hệ thống: " + e.getMessage());
            return "Register";
        }
    }
}