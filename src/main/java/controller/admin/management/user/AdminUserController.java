package controller.admin.management.user;

import dao.DBConnectionPool;
import dao.UserDAO;
import jakarta.servlet.http.HttpSession;
import models.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

    private static final Logger LOGGER = Logger.getLogger(AdminUserController.class.getName());

    // 1. Hiển thị danh sách người dùng
    @GetMapping("/manage")
    public String manageUsers(Model model,
                              @RequestParam(value = "msg", required = false) String msg,
                              @RequestParam(value = "error", required = false) String error) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);
            List<User> userList = userDAO.getAllUsers();

            model.addAttribute("userList", userList);
            if (msg != null) model.addAttribute("msg", msg);
            if (error != null) model.addAttribute("error", error);

            // SỬA: Ép Spring forward thẳng ra thư mục /webapp/admin/
            return "forward:/admin/manage_users.jsp";
        } catch (Exception e) {
            LOGGER.severe("Error getting all users: " + e.getMessage());
            model.addAttribute("error", "Lỗi kết nối cơ sở dữ liệu khi tải danh sách.");
            return "forward:/admin/manage_users.jsp";
        }
    }

    // 2. Xử lý thêm người dùng mới (Đã tối ưu luồng Redirect báo lỗi)
    @PostMapping("/insert")
    public String insertUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("email") String email,
                             @RequestParam("phone") String phone,
                             @RequestParam("address") String address) {

        // Kiểm tra dữ liệu trống
        if (username.trim().isEmpty() || password.trim().isEmpty() ||
                email.trim().isEmpty() || phone.trim().isEmpty() || address.trim().isEmpty()) {
            String error = URLEncoder.encode("Các trường không được để trống.", StandardCharsets.UTF_8);
            // SỬA: Redirect thẳng về Endpoint GET chứ không dùng forward
            return "redirect:/admin/user/manage?error=" + error;
        }

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);

            // Kiểm tra trùng email trước khi chèn vào DB để tránh crash SQL
            if (userDAO.findByEmail(email) != null) {
                String error = URLEncoder.encode("Email này đã tồn tại trong hệ thống!", StandardCharsets.UTF_8);
                return "redirect:/admin/user/manage?error=" + error;
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setPassword(password);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setAddress(address);
            newUser.setImg("image/avatars/default-avatar.png");
            newUser.setIsAdmin(false);

            boolean inserted = userDAO.insertUser(newUser);
            if (inserted) {
                String msg = URLEncoder.encode("Thêm người dùng thành công!", StandardCharsets.UTF_8);
                return "redirect:/admin/user/manage?msg=" + msg;
            } else {
                String error = URLEncoder.encode("Lỗi khi ghi nhận người dùng vào DB.", StandardCharsets.UTF_8);
                return "redirect:/admin/user/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error inserting user: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8);
            // SỬA: Khi có lỗi kết nối hoặc SQL, đẩy lỗi qua tham số URL và redirect về trang GET an toàn
            return "redirect:/admin/user/manage?error=" + error;
        }
    }

    // 3. Hiển thị Form chỉnh sửa người dùng
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") int id, Model model) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);
            User user = userDAO.getUser(id);

            if (user != null) {
                model.addAttribute("user", user);
                // SỬA: Ép Spring forward thẳng ra thư mục /webapp/admin/
                return "forward:/admin/edit_user.jsp";
            } else {
                String error = URLEncoder.encode("Người dùng không tồn tại.", StandardCharsets.UTF_8);
                return "redirect:/admin/user/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error fetching user for edit: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi kết nối khi tải thông tin người dùng.", StandardCharsets.UTF_8);
            return "redirect:/admin/user/manage?error=" + error;
        }
    }

    // 4. Xử lý cập nhật thông tin người dùng (Thay thế EditUserServlet - doPost)
    @PostMapping("/update")
    public String updateUser(@RequestParam("user_id") int id,
                             @RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @RequestParam("phone") String phoneStr,
                             @RequestParam("address") String address) {

        if (username.trim().isEmpty() || email.trim().isEmpty() ||
                phoneStr.trim().isEmpty() || address.trim().isEmpty()) {
            String error = URLEncoder.encode("Dữ liệu không được để trống.", StandardCharsets.UTF_8);
            return "redirect:/admin/user/manage?error=" + error;
        }

        int phoneInt = 0;
        try {
            phoneInt = Integer.parseInt(phoneStr);
        } catch (NumberFormatException e) {
            String error = URLEncoder.encode("Số điện thoại không hợp lệ (Phải là số).", StandardCharsets.UTF_8);
            return "redirect:/admin/user/manage?error=" + error;
        }

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);
            User user = userDAO.getUser(id);

            if (user != null) {
                // Giữ nguyên logic hàm editProfile cũ của bạn (nhận phone dạng số nguyên)
                boolean isUpdated = userDAO.editProfile(user, username, email, phoneInt, address);
                if (isUpdated) {
                    String msg = URLEncoder.encode("Cập nhật thông tin thành công!", StandardCharsets.UTF_8);
                    return "redirect:/admin/user/manage?msg=" + msg;
                } else {
                    String error = URLEncoder.encode("Cập nhật thất bại.", StandardCharsets.UTF_8);
                    return "redirect:/admin/user/manage?error=" + error;
                }
            } else {
                String error = URLEncoder.encode("Người dùng không tìm thấy.", StandardCharsets.UTF_8);
                return "redirect:/admin/user/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error updating user: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi hệ thống khi cập nhật.", StandardCharsets.UTF_8);
            return "redirect:/admin/user/manage?error=" + error;
        }
    }

    // 5. Xử lý xóa người dùng (Thay thế DeleteUserServlet)
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") int userId) {
        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
            UserDAO userDAO = new UserDAO(connection);
            boolean deleted = userDAO.deleteUser(userId);

            if (deleted) {
                String msg = URLEncoder.encode("Xóa người dùng thành công!", StandardCharsets.UTF_8);
                return "redirect:/admin/user/manage?msg=" + msg;
            } else {
                String error = URLEncoder.encode("Không thể xóa người dùng.", StandardCharsets.UTF_8);
                return "redirect:/admin/user/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error deleting user: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi kết nối cơ sở dữ liệu khi xóa.", StandardCharsets.UTF_8);
            return "redirect:/admin/user/manage?error=" + error;
        }
    }
}