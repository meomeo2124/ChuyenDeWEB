package controller.web;

import dao.UserDAO;
import dao.OrderDAO; // 🌟 BỔ SUNG: Import OrderDAO để lấy lịch sử đơn hàng
import jakarta.servlet.http.HttpSession;
import models.User;
import models.Order; // 🌟 BỔ SUNG: Import Model Order
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ProfileController {

    private final UserDAO userDAO;
    private final OrderDAO orderDAO;

    @Autowired
    public ProfileController(UserDAO userDAO, OrderDAO orderDAO) {
        this.userDAO = userDAO;
        this.orderDAO = orderDAO;
    }

    @GetMapping("/secure/user/history")
    public String showOrderHistory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        try {
            List<Order> allOrders = orderDAO.getAllOrdersWithUser();

            List<Integer> userOrderIds = allOrders.stream()
                    .filter(o -> o.getUserId() == user.getId())
                    .map(Order::getId)
                    .toList();

            List<Order> fullUserOrders = new ArrayList<>();
            for (int orderId : userOrderIds) {
                Order fullOrder = orderDAO.getOrderById(orderId); // Hàm này có nạp đầy đủ List<OrderItem>
                if (fullOrder != null) {
                    fullUserOrders.add(fullOrder);
                }
            }

            // Đẩy danh sách đơn hàng đã được điền đầy đủ dữ liệu ra ngoài giao diện
            model.addAttribute("orders", fullUserOrders);
            model.addAttribute("user", user);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "secure/history";
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
            } catch (Exception e) {
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