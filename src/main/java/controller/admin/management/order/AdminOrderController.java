package controller.admin.management.order;

import dao.OrderDAO;
import models.Order;
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

@Controller
@RequestMapping("/admin/order")
public class AdminOrderController {

    private final OrderDAO orderDAO;

    @Autowired
    public AdminOrderController(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    // 1. HIỂN THỊ DANH SÁCH ĐƠN HÀNG
    @GetMapping("/manage")
    public String manageOrders(Model model,
                               @RequestParam(value = "success", required = false) String success,
                               @RequestParam(value = "error", required = false) String error) {

        if (success != null) model.addAttribute("success", success);
        if (error != null) model.addAttribute("error", error);

        try {
            List<Order> orderList = orderDAO.getAllOrdersWithUser();
            model.addAttribute("orderList", orderList);
            return "admin/manage_orders";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Lỗi hệ thống tải danh sách hóa đơn.");
            return "admin/manage_orders";
        }
    }

    @PostMapping("/updateStatus")
    public String updateOrderStatus(@RequestParam("id") int orderId,
                                    @RequestParam("status") String status) {
        try {
            orderDAO.updateOrderStatusAndPayment(orderId, status);

            String msg = URLEncoder.encode("Cập nhật thành công đơn hàng #" + orderId + " thành " + status, StandardCharsets.UTF_8);
            return "redirect:/admin/order/manage?success=" + msg;

        } catch (Exception e) {
            e.printStackTrace();
            String errorMsg = URLEncoder.encode("Lỗi khi cập nhật trạng thái đơn hàng.", StandardCharsets.UTF_8);
            return "redirect:/admin/order/manage?error=" + errorMsg;
        }
    }
}