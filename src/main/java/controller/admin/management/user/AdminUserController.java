package controller.admin.management.user;

import dao.UserDAO;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/admin/user")
public class AdminUserController {

    private static final Logger LOGGER = Logger.getLogger(AdminUserController.class.getName());
    private final UserDAO userDAO;

    @Autowired
    public AdminUserController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    // 1. Hiển thị danh sách người dùng
    @GetMapping("/manage")
    public String manageUsers(Model model,
                              @RequestParam(value = "msg", required = false) String msg,
                              @RequestParam(value = "error", required = false) String error) {
        try {
            List<User> userList = userDAO.getAllUsers();

            model.addAttribute("userList", userList);
            if (msg != null) model.addAttribute("msg", msg);
            if (error != null) model.addAttribute("error", error);
            return "admin/manage_users";
        } catch (Exception e) {
            LOGGER.severe("Error getting all users: " + e.getMessage());
            model.addAttribute("error", "Lỗi xử lý hệ thống khi tải danh sách người dùng.");
            return "admin/manage_users";
        }
    }

    // 2. Xử lý thêm người dùng mới
    @PostMapping("/insert")
    public String insertUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("email") String email,
                             @RequestParam("phone") String phone,
                             @RequestParam("address") String address) {

        if (username.trim().isEmpty() || password.trim().isEmpty() ||
                email.trim().isEmpty() || phone.trim().isEmpty() || address.trim().isEmpty()) {
            String error = URLEncoder.encode("Các trường không được để trống.", StandardCharsets.UTF_8);
            return "redirect:/admin/user/manage?error=" + error;
        }

        try {
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
                String error = URLEncoder.encode("Lỗi khi ghi nhận người dùng vào hệ thống.", StandardCharsets.UTF_8);
                return "redirect:/admin/user/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error inserting user: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi hệ thống: " + e.getMessage(), StandardCharsets.UTF_8);
            return "redirect:/admin/user/manage?error=" + error;
        }
    }

    // 3. Hiển thị Form chỉnh sửa người dùng
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") int id, Model model) {
        try {
            User user = userDAO.getUser(id);

            if (user != null) {
                model.addAttribute("user", user);
                // ✅ ĐÃ SỬA: Bỏ đuôi .jsp
                return "admin/edit_user";
            } else {
                String error = URLEncoder.encode("Người dùng không tồn tại.", StandardCharsets.UTF_8);
                return "redirect:/admin/user/manage?error=" + error;
            }
        } catch (Exception e) {
            LOGGER.severe("Error fetching user for edit: " + e.getMessage());
            String error = URLEncoder.encode("Lỗi hệ thống khi tải thông tin người dùng.", StandardCharsets.UTF_8);
            return "redirect:/admin/user/manage?error=" + error;
        }
    }

    // 4. Xử lý cập nhật thông tin người dùng
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

        try {
            User user = userDAO.getUser(id);

            if (user != null) {
                boolean isUpdated = userDAO.editProfile(user, username, email, phoneStr, address);
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

    // 5. Xử lý xóa người dùng
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") int userId) {
        try {
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
            String error = URLEncoder.encode("Lỗi hệ thống khi xóa người dùng.", StandardCharsets.UTF_8);
            return "redirect:/admin/user/manage?error=" + error;
        }
    }
}