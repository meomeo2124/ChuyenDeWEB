package controller.web;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import config.VnPayConfig;
import dao.OrderDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Cart;
import models.Order;
import models.OrderItem;
import models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

@Controller
@RequestMapping("/secure")
public class PaymentController {

    private static final Logger LOGGER = Logger.getLogger(PaymentController.class.getName());
    private final OrderDAO orderDAO;
    private final DataSource dataSource;

    @Autowired
    public PaymentController(OrderDAO orderDAO, DataSource dataSource) {
        this.orderDAO = orderDAO;
        this.dataSource = dataSource;
    }

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

        return "secure/payment";
    }

    @PostMapping("/payment")
    public String processPayment(HttpServletRequest request,
                                 HttpSession session,
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

        if ("proceedToPayment".equals(action)) {
            return "secure/payment";
        }

        if ("pay".equals(action)) {
            try {
                if (paymentMethod == null || paymentMethod.isEmpty()) {
                    String error = URLEncoder.encode("Phương thức thanh toán không được để trống", StandardCharsets.UTF_8);
                    return "redirect:/secure/payment?error=" + error;
                }

                // Cộng thêm 15,000đ phí ship chuẩn vào tổng hóa đơn
                double totalPrice = cart.getTotalPrice() + (cart.getTotalPrice() > 0 ? 15000.00 : 0.00);

                if ("vietqr".equalsIgnoreCase(paymentMethod)) {
                    int orderId = orderDAO.createOrder(user.getId(), totalPrice, "VIETQR");
                    if (orderId > 0) {
                        orderDAO.saveOrderDetails(orderId, cart.getItems());

                        cart.clearCart();
                        session.setAttribute("cart", cart);

                        long amountVnd = (long) totalPrice;
                        String bankId = "MB";
                        String accountNo = "0941660744";
                        String accountName = URLEncoder.encode("NGUYEN HUYNH GIAO", StandardCharsets.UTF_8.toString());
                        String memo = URLEncoder.encode("BobaStation chuyen khoan don " + orderId, StandardCharsets.UTF_8.toString());

                        String vietQrUrl = String.format("https://img.vietqr.io/image/%s-%s-compact2.jpg?amount=%d&addInfo=%s&accountName=%s",
                                bankId, accountNo, amountVnd, memo, accountName);

                        model.addAttribute("vietQrUrl", vietQrUrl);
                        model.addAttribute("orderId", orderId);
                        model.addAttribute("totalPriceVnd", amountVnd);

                        return "secure/vietqr_pay";
                    }
                }

                if ("vnpay".equalsIgnoreCase(paymentMethod)) {
                    int orderId = orderDAO.createOrder(user.getId(), totalPrice, "VNPAY");
                    if (orderId > 0) {
                        orderDAO.saveOrderDetails(orderId, cart.getItems());
                        long amount = Math.round(totalPrice * 100);

                        Map<String, String> vnp_Params = new HashMap<>();
                        vnp_Params.put("vnp_Version", VnPayConfig.vnp_Version);
                        vnp_Params.put("vnp_Command", VnPayConfig.vnp_Command);
                        vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
                        vnp_Params.put("vnp_Amount", String.valueOf(amount));
                        vnp_Params.put("vnp_CurrCode", "VND");
                        vnp_Params.put("vnp_TxnRef", String.valueOf(orderId));
                        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang BobaStation " + orderId);
                        vnp_Params.put("vnp_OrderType", "other");
                        vnp_Params.put("vnp_Locale", "vn");
                        vnp_Params.put("vnp_ReturnUrl", VnPayConfig.vnp_ReturnUrl);

                        String ipAddr = VnPayConfig.getIpAddress(request);
                        if (ipAddr == null || ipAddr.contains(":") || ipAddr.equals("127.0.0.1")) {
                            ipAddr = "192.168.1.1";
                        }
                        vnp_Params.put("vnp_IpAddr", ipAddr);

                        TimeZone tz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");
                        Calendar cld = Calendar.getInstance(tz);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                        formatter.setTimeZone(tz);

                        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
                        cld.add(Calendar.MINUTE, 15);
                        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

                        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
                        Collections.sort(fieldNames);

                        StringBuilder hashData = new StringBuilder();
                        StringBuilder query = new StringBuilder();
                        Iterator<String> itr = fieldNames.iterator();

                        while (itr.hasNext()) {
                            String fieldName = itr.next();
                            String fieldValue = vnp_Params.get(fieldName);
                            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());
                                String encodedName = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString());

                                hashData.append(fieldName).append('=').append(encodedValue);
                                query.append(encodedName).append('=').append(encodedValue);

                                if (itr.hasNext()) {
                                    query.append('&');
                                    hashData.append('&');
                                }
                            }
                        }

                        String queryUrl = query.toString();
                        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, hashData.toString());
                        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

                        return "redirect:" + VnPayConfig.vnp_PayUrl + "?" + queryUrl;
                    }
                }

                // Luồng xử lý cho TIỀN MẶT / Các phương thức offline
                int orderId = orderDAO.createOrder(user.getId(), totalPrice, paymentMethod);
                if (orderId > 0) {
                    orderDAO.saveOrderDetails(orderId, cart.getItems());

                    cart.clearCart();
                    session.setAttribute("cart", cart);
                    return "redirect:/secure/order-success?orderId=" + orderId;
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

    @GetMapping("/order-success")
    public String showOrderSuccess(@RequestParam("orderId") int orderId, Model model) {
        try {
            Order order = orderDAO.getOrderById(orderId);
            model.addAttribute("order", order);
            return "secure/invoice";
        } catch (Exception e) {
            return "redirect:/secure/cart?error=" + URLEncoder.encode("Lỗi hiển thị hóa đơn.", StandardCharsets.UTF_8);
        }
    }

    @PostMapping("/vietqr-confirm")
    public String vietqrConfirm(@RequestParam("orderId") String orderIdStr, Model model) {
        try {
            int orderId = Integer.parseInt(orderIdStr);
            Order order = orderDAO.getOrderById(orderId);

            if (order != null) {
                model.addAttribute("order", order);
                model.addAttribute("msg", "Don hang #" + orderId + " dang o trang thai CHO XAC NHAN. Chung toi dang kiem tra giao dich chuyen khoan cua ban!");
                return "secure/invoice";
            }
        } catch (Exception e) {
            LOGGER.severe("VietQR confirm error: " + e.getMessage());
        }
        return "redirect:/secure/cart?error=" + URLEncoder.encode("Khong tim thay don hang de xac nhan.", StandardCharsets.UTF_8);
    }

    @GetMapping("/vnpay-callback")
    public String vnpayCallback(HttpServletRequest request, HttpSession session, Model model) {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_Amount = request.getParameter("vnp_Amount");
        String vnp_SecureHashReceived = request.getParameter("vnp_SecureHash");

        if (vnp_TxnRef == null || vnp_TxnRef.isEmpty()) {
            return "redirect:/secure/cart?error=" + URLEncoder.encode("Tham so phan hoi khong hop le.", StandardCharsets.UTF_8);
        }

        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0) && fieldName.startsWith("vnp_")) {
                if (!fieldName.equals("vnp_SecureHash")) {
                    fields.put(fieldName, fieldValue);
                }
            }
        }

        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = fieldNames.iterator();
        while (iterator.hasNext()) {
            String fieldName = iterator.next();
            String fieldValue = fields.get(fieldName);
            sb.append(fieldName).append("=").append(fieldValue);
            if (iterator.hasNext()) {
                sb.append("&");
            }
        }

        String signValue = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, sb.toString());

        if (vnp_SecureHashReceived == null || !signValue.equalsIgnoreCase(vnp_SecureHashReceived)) {
            return "redirect:/secure/payment?error=" + URLEncoder.encode("Loi du lieu phan hoi: Chu ky khong hop le!", StandardCharsets.UTF_8);
        }

        int orderId = Integer.parseInt(vnp_TxnRef);
        double amountReal = Double.parseDouble(vnp_Amount) / 100.0;

        try (Connection connection = dataSource.getConnection()) {
            String paymentStatus = "FAILED";

            if ("00".equals(vnp_ResponseCode)) {
                paymentStatus = "PAID";

                String updateOrderSql = "UPDATE `dbo.orders` SET status = 'PAID' WHERE id = ?";
                try (PreparedStatement ps = connection.prepareStatement(updateOrderSql)) {
                    ps.setInt(1, orderId);
                    ps.executeUpdate();
                }

                Cart cart = (Cart) session.getAttribute("cart");
                if (cart != null) {
                    cart.clearCart();
                    session.setAttribute("cart", cart);
                }

                model.addAttribute("msg", "Thanh toan qua cong VNPay thanh cong!");
            } else {
                paymentStatus = "CANCELLED";
                String updateOrderSql = "UPDATE `dbo.orders` SET status = 'CANCELLED' WHERE id = ?";
                try (PreparedStatement ps = connection.prepareStatement(updateOrderSql)) {
                    ps.setInt(1, orderId);
                    ps.executeUpdate();
                }
            }

            String insertPaymentSql = "INSERT INTO `dbo.payment` (order_id, amount, payment_method, status) VALUES (?, ?, ?, ?)";
            try (PreparedStatement psPayment = connection.prepareStatement(insertPaymentSql)) {
                psPayment.setInt(1, orderId);
                psPayment.setDouble(2, amountReal);
                psPayment.setString(3, "VNPAY");
                psPayment.setString(4, paymentStatus);
                psPayment.executeUpdate();
            }

            if ("PAID".equals(paymentStatus)) {
                Order order = orderDAO.getOrderById(orderId);
                model.addAttribute("order", order);
                return "secure/invoice";
            } else {
                return "redirect:/secure/payment?error=" + URLEncoder.encode("Giao dich thanh toan that bai.", StandardCharsets.UTF_8);
            }

        } catch (Exception e) {
            LOGGER.severe("VNPay callback error: " + e.getMessage());
            return "redirect:/secure/payment?error=" + URLEncoder.encode("Loi xu ly giao dich co so du lieu.", StandardCharsets.UTF_8);
        }
    }

    @GetMapping("/generateInvoicePDF")
    public void generateInvoicePDF(@RequestParam("orderId") String orderIdStr,
                                   HttpServletResponse response,
                                   HttpSession session) throws IOException {
        User user = (User) session.getAttribute("user");
        if (user == null || orderIdStr == null || orderIdStr.isEmpty()) {
            response.sendRedirect(session.getServletContext().getContextPath() + "/secure/payment?error=Yeu cau khong hop le");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                response.sendRedirect(session.getServletContext().getContextPath() + "/secure/payment?error=Khong tim thay hoa don");
                return;
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=BobaStation_HoaDon_" + orderId + ".pdf");

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont font = PdfFontFactory.createFont("C:/Windows/Fonts/times.ttf", "Identity-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            document.add(new Paragraph("BOBA STATION").setFontSize(16).setBold().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("Địa chỉ: Khu Phố 33, Phường Linh Xuân, Thành Phố Hồ Chí Minh").setFontSize(10).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER).setItalic());
            document.add(new Paragraph("------------------------------------------------------------------------------------------------------------------------").setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            document.add(new Paragraph("MÃ HÓA ĐƠN: #" + order.getId()).setBold());
            document.add(new Paragraph("Khách hàng đặt mua: " + user.getUsername()));
            document.add(new Paragraph("Số điện thoại liên hệ: " + (user.getPhone() != null ? user.getPhone() : "Chưa cập nhật")));

            String pMethod = switch (order.getPaymentMethod() != null ? order.getPaymentMethod() : "UNKNOWN") {
                case "cash_on_delivery", "CASH" -> "Tiền mặt (Thanh toán khi nhận hàng)";
                case "VNPAY" -> "Cổng thanh toán điện tử VNPay";
                case "VIETQR" -> "Chuyển khoản nhanh mã VietQR";
                default -> "Thanh toán bằng thẻ";
            };
            document.add(new Paragraph("Phương thức thanh toán: " + pMethod));
            document.add(new Paragraph("Thời gian khởi tạo hệ thống: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(order.getOrderDate() != null ? order.getOrderDate() : new Date())));
            document.add(new Paragraph(" "));
            Table table = new Table(new float[]{4, 1, 2});
            table.setWidth(com.itextpdf.layout.properties.UnitValue.createPercentValue(100));

            // Định dạng tiêu đề bảng (Header)
            table.addHeaderCell(new Cell().add(new Paragraph("Tên đồ uống / Topping").setBold().setFont(font)).setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("SL").setBold().setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)).setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY));
            table.addHeaderCell(new Cell().add(new Paragraph("Thành tiền").setBold().setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)).setBackgroundColor(com.itextpdf.kernel.colors.ColorConstants.LIGHT_GRAY));

            double subTotal = 0.0;
            for (OrderItem item : order.getItems()) {
                double itemExtPrice = item.getPrice() * item.getQuantity();
                subTotal += itemExtPrice;

                table.addCell(new Cell().add(new Paragraph(item.getProductName()).setFont(font)));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getQuantity())).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER)));
                table.addCell(new Cell().add(new Paragraph(String.format("%,.0f đ", itemExtPrice)).setFont(font).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT)));
            }
            document.add(table);
            document.add(new Paragraph(" "));

            double shipFee = (subTotal > 0) ? 15000.00 : 0.00;
            document.add(new Paragraph("Tạm tính tiền hàng: " + String.format("%,.0f VNĐ", subTotal)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            document.add(new Paragraph("Phí vận chuyển (Giao hàng tận nơi): " + String.format("%,.0f VNĐ", shipFee)).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));
            document.add(new Paragraph("TỔNG CỘNG THANH TOÁN: " + String.format("%,.0f VNĐ", order.getTotalPrice())).setFontSize(13).setBold().setFontColor(com.itextpdf.kernel.colors.ColorConstants.RED).setTextAlignment(com.itextpdf.layout.properties.TextAlignment.RIGHT));

            document.add(new Paragraph(" "));
            document.add(new Paragraph("Cảm ơn quý khách đã tin tưởng lựa chọn Boba Station!").setItalic().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));
            document.add(new Paragraph("Chúc quý khách ngon miệng!").setItalic().setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            document.close();

        } catch (SQLException | NumberFormatException e) {
            LOGGER.severe("PDF generation error: " + e.getMessage());
            response.sendRedirect(session.getServletContext().getContextPath() + "/secure/payment?error=Loi he thong hoa don");
        }
    }
}