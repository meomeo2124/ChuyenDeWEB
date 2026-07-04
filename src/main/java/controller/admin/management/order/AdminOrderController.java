package controller.admin.management.order;

import dao.DBConnectionPool;
import models.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    // 1. HIỂN THỊ DANH SÁCH ĐƠN HÀNG
    @GetMapping("/manage")
    public String manageOrders(Model model,
                               @RequestParam(value = "success", required = false) String success,
                               @RequestParam(value = "error", required = false) String error) {

        if (success != null) model.addAttribute("success", success);
        if (error != null) model.addAttribute("error", error);

        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT o.id, o.user_id, o.total, o.status, o.created_at, u.username FROM `dbo.orders` o JOIN `dbo.users` u ON o.user_id = u.id ORDER BY o.created_at DESC";

        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalPrice(rs.getDouble("total"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("created_at"));

                // Mượn thuộc tính tạm thời để hiển thị tên khách hàng lên bảng Admin
                order.setShippingAddress(rs.getString("username"));
                orderList.add(order);
            }
            model.addAttribute("orderList", orderList);
            return "forward:/admin/manage_orders.jsp";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống tải danh sách hóa đơn.");
            return "forward:/admin/manage_orders.jsp";
        }
    }

    // =============================================================
    // 2. BỔ SUNG: CẬP NHẬT TRẠNG THÁI ĐƠN HÀNG TỪ ADMIN
    // =============================================================
    @PostMapping("/updateStatus")
    public String updateOrderStatus(@RequestParam("id") int orderId,
                                    @RequestParam("status") String status) {

        String updateOrderSql = "UPDATE `dbo.orders` SET status = ? WHERE id = ?";
        String updatePaymentSql = "UPDATE `dbo.payment` SET status = ? WHERE order_id = ?";

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {

            // 1. Cập nhật bảng Orders
            try (PreparedStatement psOrder = connection.prepareStatement(updateOrderSql)) {
                psOrder.setString(1, status);
                psOrder.setInt(2, orderId);
                psOrder.executeUpdate();
            }

            // 2. Cập nhật đồng bộ bảng Payment (Nếu có tồn tại dòng dữ liệu)
            try (PreparedStatement psPayment = connection.prepareStatement(updatePaymentSql)) {
                psPayment.setString(1, status);
                psPayment.setInt(2, orderId);
                psPayment.executeUpdate();
            }

            String msg = URLEncoder.encode("Cập nhật thành công đơn hàng #" + orderId + " thành " + status, StandardCharsets.UTF_8);
            return "redirect:/admin/order/manage?success=" + msg;

        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = URLEncoder.encode("Lỗi khi cập nhật trạng thái đơn hàng.", StandardCharsets.UTF_8);
            return "redirect:/admin/order/manage?error=" + errorMsg;
        }
    }
}