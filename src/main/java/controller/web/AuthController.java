package controller.web;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory; // Thay thế JacksonFactory cũ
import dao.DBConnectionPool;
import dao.UserDAO;
import jakarta.servlet.http.HttpSession;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Connection;
import java.util.Collections;

@Controller
public class AuthController {

    // 1. Hiển thị trang Login
    @GetMapping("/login")
    public String showLoginPage() {
        return "Login"; // Trỏ đến WEB-INF/views/Login.jsp
    }

    // 2. Gom chung cả xử lý Login truyền thống và Google Login vào chung một Endpoint POST /login
    // Giống hoàn toàn logic cũ trong doPost của Login.java
    @PostMapping("/login")
    public String handleLogin(
            @RequestParam(value = "credential", required = false) String idTokenString,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "password", required = false) String pass,
            HttpSession session,
            Model model) {

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);

            // ================= LUỒNG 1: ĐĂNG NHẬP BẰNG GOOGLE =================
            if (idTokenString != null && !idTokenString.isEmpty()) {

                // Đã tối ưu chuyển JacksonFactory cũ sang GsonFactory để hết cảnh báo vàng
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

                    // SỬA LỖI 1: Gọi hàm chuẩn findByEmail thay vì getUserByEmail
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

                    // Lưu dữ liệu vào Session
                    setSessionUser(session, user);
                    return "redirect:/home"; // Quay về trang chủ
                } else {
                    return "redirect:/login?error=google_token_invalid";
                }
            }

            // ================= LUỒNG 2: ĐĂNG NHẬP TRUYỀN THỐNG (EMAIL/PASS) =================
            else if (email != null && pass != null) {
                User user = userDAO.getLogin(email, pass);
                if (user == null) {
                    model.addAttribute("message", "Sai thông tin tài khoản mật khẩu");
                    return "Login"; // Trả về lại trang Login kèm thông báo lỗi
                } else {
                    setSessionUser(session, user);

                    // SỬA LỖI 2: Dùng hàm isAdmin() kiểm tra quyền thay vì setRole
                    if (user.isAdmin()) {
                        return "redirect:/admin/dashboard.jsp"; // Hoặc endpoint admin của bạn
                    } else {
                        return "redirect:/home";
                    }
                }
            } else {
                return "redirect:/login?error=missing_credentials";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("message", "Có lỗi xảy ra trong quá trình xử lý hệ thống.");
            return "Login";
        }
    }

    // Helper method tái sử dụng logic ghi Session dữ liệu User
    private void setSessionUser(HttpSession session, User user) {
        session.setAttribute("user", user);
        session.setAttribute("userId", user.getId());
        session.setAttribute("img", user.getImg());
    }

    // 3. Xử lý Đăng xuất
    @GetMapping("/logout")
    public String handleLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}