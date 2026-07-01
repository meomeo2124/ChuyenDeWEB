package controller.web;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import dao.DBConnectionPool;
import dao.UserDAO;
import jakarta.servlet.http.HttpSession;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.Collections;

@Controller
public class AuthController {

    // 1. Hiển thị trang Login (Nhận thêm tham số báo lỗi truyền từ redirect qua)
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "msg", required = false) String msg,
                                Model model) {
        if ("google_token_invalid".equals(error)) {
            model.addAttribute("message", "Tài khoản Google xác thực không hợp lệ.");
        } else if ("missing_credentials".equals(error)) {
            model.addAttribute("message", "Vui lòng nhập đầy đủ thông tin đăng nhập.");
        } else if (msg != null) {
            model.addAttribute("message", msg); // Hiển thị thông báo sai mật khẩu hoặc thông báo đăng ký thành công
        }
        return "Login";
    }

    // Dự phòng: Nếu có bất kỳ luồng nào cố tình gọi GET/Forward nhầm phương thức, ép trả về giao diện gốc
    @RequestMapping(value = "/login", method = {RequestMethod.HEAD})
    public String fallbackLogin() {
        return "redirect:/login";
    }

    // 2. Xử lý dữ liệu Đăng nhập bằng phương thức POST
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam(value = "credential", required = false) String idTokenString,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String pass,
            HttpSession session) {

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);

            // ================= LUỒNG 1: ĐĂNG NHẬP BẰNG GOOGLE =================
            if (idTokenString != null && !idTokenString.isEmpty()) {
                GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                        .setAudience(Collections.singletonList("564628514231-g4733rfvad9m98vffpn5iofj3ht90u1t.apps.googleusercontent.com"))
                        .build();

                GoogleIdToken idToken = verifier.verify(idTokenString);
                if (idToken != null) {
                    GoogleIdToken.Payload payload = idToken.getPayload();
                    String googleId = payload.getSubject();
                    String googleEmail = payload.getEmail();
                    String name = (String) payload.get("name");
                    String img = (String) payload.get("picture");

                    User user = userDAO.findByEmail(googleEmail);
                    if (user == null) {
                        user = new User();
                        user.setGoogleId(googleId);
                        user.setEmail(googleEmail);
                        user.setName(name);
                        user.setImg(img);
                        userDAO.insertUser(user);
                    } else if (user.getGoogleId() == null) {
                        user.setGoogleId(googleId);
                        userDAO.updateUserGoogleId(user);
                    }

                    setSessionUser(session, user);
                    return "redirect:/home";
                } else {
                    return "redirect:/login?error=google_token_invalid";
                }
            }

            // ================= LUỒNG 2: ĐĂNG NHẬP TRUYỀN THỐNG (EMAIL/PASS) =================
            else if (email != null && pass != null) {
                User user = userDAO.getLogin(email, pass);
                if (user == null) {
                    // ĐÃ SỬA: Không return "Login" trực tiếp nữa. Tiến hành redirect về GET để làm sạch URL.
                    String flashMsg = URLEncoder.encode("Sai thông tin tài khoản hoặc mật khẩu", StandardCharsets.UTF_8);
                    return "redirect:/login?msg=" + flashMsg;
                } else {
                    setSessionUser(session, user);

                    if (user.isAdmin()) {
                        return "redirect:/admin/dashboard.jsp";
                    } else {
                        return "redirect:/home";
                    }
                }
            } else {
                return "redirect:/login?error=missing_credentials";
            }

        } catch (Exception e) {
            e.printStackTrace();
            String systemError = URLEncoder.encode("Có lỗi hệ thống xảy ra.", StandardCharsets.UTF_8);
            return "redirect:/login?msg=" + systemError;
        }
    }

    private void setSessionUser(HttpSession session, User user) {
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("img", user.getImg());
    }

    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}