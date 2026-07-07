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

    // hiển thị Form thêm người dùng mới
    @GetMapping("/insert")
    public String showInsertForm(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "admin/insert_user";
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

    // 2. Xử lý thêm người dùng mới (ĐÃ FIX TRIỆT TIÊU LỖI 405 BẰNG CÁCH TRẢ VIEW TRỰC TIẾP KHI CÓ LỖI)
    @PostMapping("/insert")
    public String insertUser(@RequestParam("username") String username,
                             @RequestParam("password") String password,
                             @RequestParam("email") String email,
                             @RequestParam("phone") String phone,
                             @RequestParam("address") String address,
                             Model model) { // Thêm Model để đẩy thông báo lỗi thẳng về giao diện

        // Kiểm tra dữ liệu trống -> Trả trực tiếp về form kèm thông báo
        if (username.trim().isEmpty() || password.trim().isEmpty() ||
                email.trim().isEmpty() || phone.trim().isEmpty() || address.trim().isEmpty()) {
            model.addAttribute("error", "Các trường thông tin không được để trống.");
            return "admin/insert_user";
        }

        try {
            // Kiểm tra trùng lặp email -> Trả trực tiếp về form kèm thông báo
            if (userDAO.findByEmail(email) != null) {
                model.addAttribute("error", "Địa chỉ Email này đã tồn tại trong hệ thống!");
                return "admin/insert_user";
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
                // Thêm thành công -> Chuyển hướng an toàn về trang danh sách chính
                return "redirect:/admin/user/manage?msg=" + URLEncoder.encode("Thêm người dùng mới thành công!", StandardCharsets.UTF_8);
            } else {
                model.addAttribute("error", "Lỗi khi ghi nhận người dùng vào hệ thống.");
                return "admin/insert_user";
            }
        } catch (Exception e) {
            LOGGER.severe("Error inserting user: " + e.getMessage());
            model.addAttribute("error", "Lỗi hệ thống: " + e.getMessage());
            return "admin/insert_user";
        }
    }

    // 3. Hiển thị Form chỉnh sửa người dùng
    @GetMapping("/edit")
    public String showEditForm(@RequestParam("id") int id, Model model) {
        try {
            User user = userDAO.getUser(id);
            if (user != null) {
                model.addAttribute("user", user);
                return "admin/edit_user";
            } else {
                return "redirect:/admin/user/manage?error=" + URLEncoder.encode("Người dùng không tồn tại.", StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            LOGGER.severe("Error fetching user for edit: " + e.getMessage());
            return "redirect:/admin/user/manage?error=" + URLEncoder.encode("Lỗi hệ thống khi tải thông tin.", StandardCharsets.UTF_8);
        }
    }

    // 4. Xử lý cập nhật thông tin người dùng
    @PostMapping("/update")
    public String updateUser(@RequestParam("user_id") int id,
                             @RequestParam("username") String username,
                             @RequestParam("email") String email,
                             @RequestParam("phone") String phoneStr,
                             @RequestParam("address") String address,
                             Model model) {

        try {
            User user = userDAO.getUser(id);
            if (user == null) {
                return "redirect:/admin/user/manage?error=" + URLEncoder.encode("Người dùng không tìm thấy.", StandardCharsets.UTF_8);
            }

            // Nếu điền thiếu thông tin -> Trả thẳng về trang Edit kèm lỗi, tránh văng trang 405
            if (username.trim().isEmpty() || email.trim().isEmpty() ||
                    phoneStr.trim().isEmpty() || address.trim().isEmpty()) {
                model.addAttribute("user", user);
                model.addAttribute("error", "Dữ liệu không được để trống.");
                return "admin/edit_user";
            }

            boolean isUpdated = userDAO.editProfile(user, username, email, phoneStr, address);
            if (isUpdated) {
                return "redirect:/admin/user/manage?msg=" + URLEncoder.encode("Cập nhật thông tin thành công!", StandardCharsets.UTF_8);
            } else {
                model.addAttribute("user", user);
                model.addAttribute("error", "Cập nhật dữ liệu thất bại.");
                return "admin/edit_user";
            }
        } catch (Exception e) {
            LOGGER.severe("Error updating user: " + e.getMessage());
            return "redirect:/admin/user/manage?error=" + URLEncoder.encode("Lỗi hệ thống khi cập nhật.", StandardCharsets.UTF_8);
        }
    }

    // 5. Xử lý xóa người dùng
    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") int userId) {
        try {
            boolean deleted = userDAO.deleteUser(userId);
            if (deleted) {
                return "redirect:/admin/user/manage?msg=" + URLEncoder.encode("Xóa người dùng thành công!", StandardCharsets.UTF_8);
            } else {
                return "redirect:/admin/user/manage?error=" + URLEncoder.encode("Không thể xóa người dùng.", StandardCharsets.UTF_8);
            }
        } catch (Exception e) {
            LOGGER.severe("Error deleting user: " + e.getMessage());
            return "redirect:/admin/user/manage?error=" + URLEncoder.encode("Lỗi hệ thống khi xóa người dùng.", StandardCharsets.UTF_8);
        }
    }
}