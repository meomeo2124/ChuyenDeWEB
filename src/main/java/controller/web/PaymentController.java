package controller.web;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import dao.OrderDAO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Cart;
import models.Order;
import models.OrderItem;
import models.User;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.logging.Logger;

@Controller
@RequestMapping("/secure")
public class PaymentController {

    private static final Logger LOGGER = Logger.getLogger(PaymentController.class.getName());
    private final OrderDAO orderDAO = new OrderDAO(); // Giữ nguyên DAO cũ của bạn

    // 1. Hiển thị trang thanh toán (Thay thế doGet cũ)
    @GetMapping("/payment")
    public String showPaymentPage(HttpSession session) {
        User user = (User) session.getAttribute("user");
        Cart cart = (Cart) session.getAttribute("cart");

        if (user == null) {
            String error = URLEncoder.encode("Vui lòng đăng nhập để thanh toán", StandardCharsets.UTF_8);
            return "redirect:/login?error=" + error;
        }

        if (cart == null || cart.getItems().isEmpty()) {
            String error = URLEncoder.encode("Giỏ hàng trống", StandardCharsets.UTF_8);
            return "redirect:/secure/cart?error=" + error;
        }

        return "secure/payment"; // Spring tự map vào /WEB-INF/views/secure/payment.jsp
    }

    // 2. Xử lý submit thanh toán (Thay thế doPost cũ)
    @PostMapping("/payment")
    public String processPayment(HttpSession session,
                                 @RequestParam(value = "action", required = false) String action,
                                 @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                                 Model model) {
        User user = (User) session.getAttribute("user");
        Cart cart = (Cart) session.getAttribute("cart");

        if (user == null) {
            String error = URLEncoder.encode("Vui lòng đăng nhập để thanh toán", StandardCharsets.UTF_8);
            return "redirect:/login?error=" + error;
        }

        if (cart == null || cart.getItems().isEmpty()) {
            String error = URLEncoder.encode("Giỏ hàng trống", StandardCharsets.UTF_8);
            return "redirect:/secure/cart?error=" + error;
        }

        // Trường hợp action từ trang Giỏ hàng chuyển qua
        if ("proceedToPayment".equals(action)) {
            return "secure/payment";
        }

        // Trường hợp nhấn nút "Thanh toán" chính thức
        if ("pay".equals(action)) {
            try {
                if (paymentMethod == null || paymentMethod.isEmpty()) {
                    String error = URLEncoder.encode("Phương thức thanh toán không được để trống", StandardCharsets.UTF_8);
                    return "redirect:/secure/payment?error=" + error;
                }

                // Tính toán tổng tiền giống logic cũ
                double totalPrice = cart.getTotalPrice() + (cart.getTotalPrice() > 0 ? 10.00 : 0.00);
                int orderId = orderDAO.createOrder(user.getId(), totalPrice, paymentMethod);

                if (orderId > 0) {
                    orderDAO.saveOrderDetails(orderId, cart.getItems());

                    // Xóa giỏ hàng sau khi đặt thành công
                    cart.clearCart();
                    session.setAttribute("cart", cart);

                    // Lấy thông tin order vừa tạo để hiển thị lên Invoice
                    Order order = orderDAO.getOrderById(orderId);
                    model.addAttribute("order", order);

                    return "secure/invoice"; // Render /WEB-INF/views/secure/invoice.jsp
                } else {
                    String error = URLEncoder.encode("Thanh toán thất bại", StandardCharsets.UTF_8);
                    return "redirect:/secure/payment?error=" + error;
                }
            } catch (Exception e) {
                LOGGER.severe("Payment error: " + e.getMessage());
                String error = URLEncoder.encode("Thanh toán thất bại: " + e.getMessage(), StandardCharsets.UTF_8);
                return "redirect:/secure/payment?error=" + error;
            }
        }

        String error = URLEncoder.encode("Yêu cầu không hợp lệ", StandardCharsets.UTF_8);
        return "redirect:/secure/cart?error=" + error;
    }

    // 3. Xuất hóa đơn PDF (Thay thế GenerateInvoicePDF.java)
    @GetMapping("/generateInvoicePDF")
    public void generateInvoicePDF(@RequestParam("orderId") String orderIdStr,
                                   HttpServletResponse response,
                                   HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null || orderIdStr == null || orderIdStr.isEmpty()) {
            response.sendRedirect(session.getServletContext().getContextPath() + "/secure/payment?error=Yêu cầu không hợp lệ");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                response.sendRedirect(session.getServletContext().getContextPath() + "/secure/payment?error=Không tìm thấy hóa đơn");
                return;
            }

            // Thiết lập Header tải file PDF
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=hoa_don_" + orderId + ".pdf");

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Nạp font hỗ trợ tiếng Việt
            PdfFont font = PdfFontFactory.createFont("C:/Windows/Fonts/times.ttf", "Identity-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            // Ghi nội dung PDF (Giữ nguyên logic cũ của bạn)
            document.add(new Paragraph("Hóa Đơn").setFontSize(20));
            document.add(new Paragraph("Mã Hóa Đơn: " + order.getId()));

            String paymentMethodDisplay = switch (order.getPaymentMethod() != null ? order.getPaymentMethod() : "UNKNOWN") {
                case "cash_on_delivery" -> "Tiền mặt (Thanh toán khi nhận hàng)";
                case "credit_card" -> "Thẻ tín dụng";
                case "paypal" -> "PayPal";
                case "momo" -> "MoMo";
                case "vnpay" -> "VNPay";
                default -> "Không xác định";
            };
            document.add(new Paragraph("Phương Thức Thanh Toán: " + paymentMethodDisplay));
            document.add(new Paragraph("Tổng Tiền: " + String.format("%,.0f VNĐ", order.getTotalPrice())));

            Table table = new Table(new float[]{3, 1, 2});
            table.addHeaderCell(new Cell().add(new Paragraph("Sản Phẩm").setFont(font)));
            table.addHeaderCell(new Cell().add(new Paragraph("Số Lượng").setFont(font)));
            table.addHeaderCell(new Cell().add(new Paragraph("Giá").setFont(font)));

            for (OrderItem item : order.getItems()) {
                table.addCell(new Cell().add(new Paragraph(item.getProductName()).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.format("%,.0f VNĐ", item.getPrice())).setFont(font)));
            }

            document.add(table);
            document.close();

        } catch (SQLException | NumberFormatException e) {
            LOGGER.severe("PDF generation error: " + e.getMessage());
            response.sendRedirect(session.getServletContext().getContextPath() + "/secure/payment?error=Lỗi hệ thống hóa đơn");
        }
    }
}