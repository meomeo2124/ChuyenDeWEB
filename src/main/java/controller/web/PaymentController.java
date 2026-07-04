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
import dao.DBConnectionPool;
import jakarta.servlet.http.HttpServletRequest;
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
    private final OrderDAO orderDAO = new OrderDAO();

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

                double totalPrice = cart.getTotalPrice() + (cart.getTotalPrice() > 0 ? 10.00 : 0.00);

                // =============================================================
                // ROUTER 1: XỬ LÝ VIETQR
                // =============================================================
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
                // =============================================================
                // ROUTER 2: XỬ LÝ VNPAY (ĐÃ ĐỒNG BỘ CHUẨN MẪU AJAXSERVLET)
                // =============================================================
                if ("vnpay".equalsIgnoreCase(paymentMethod)) {
                    int orderId = orderDAO.createOrder(user.getId(), totalPrice, "VNPAY");
                    if (orderId > 0) {
                        orderDAO.saveOrderDetails(orderId, cart.getItems());

                        // Tính số tiền theo đơn vị đồng cấu trúc VNPay (Nhân 100)
                        long amount = (long) (totalPrice * 100);

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

                        TimeZone tz = TimeZone.getTimeZone("Asia/Ho_Chi_Minh"); // Chuẩn múi giờ Việt Nam (GMT+7)
                        Calendar cld = Calendar.getInstance(tz);
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                        formatter.setTimeZone(tz);

                        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
                        cld.add(Calendar.MINUTE, 15);
                        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

                        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));
                        cld.add(Calendar.MINUTE, 15);
                        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

                        // Sắp xếp các tham số alphabet
                        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
                        Collections.sort(fieldNames);

                        StringBuilder hashData = new StringBuilder();
                        StringBuilder query = new StringBuilder();
                        Iterator<String> itr = fieldNames.iterator();

                        // KHÔI PHỤC VÒNG LẶP NGUYÊN BẢN CỦA VNPAY DEMO SDK
                        while (itr.hasNext()) {
                            String fieldName = itr.next();
                            String fieldValue = vnp_Params.get(fieldName);
                            if ((fieldValue != null) && (fieldValue.length() > 0)) {

                                // Bộ mã mẫu bắt buộc sử dụng US_ASCII tên chuỗi cấu trúc mã hóa
                                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString());
                                String encodedName = URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString());

                                // Build hash data: Tên tham số thô không mã hóa (Xem dòng 84 ajaxServlet.java)
                                hashData.append(fieldName);
                                hashData.append('=');
                                hashData.append(encodedValue);

                                // Build query: Mã hóa cả tên lẫn giá trị tham số điều hướng URL
                                query.append(encodedName);
                                query.append('=');
                                query.append(encodedValue);

                                if (itr.hasNext()) {
                                    query.append('&');
                                    hashData.append('&');
                                }
                            }
                        }

                        String queryUrl = query.toString();
                        // Thực hiện ký mã hash chuẩn xác
                        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, hashData.toString());
                        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;

                        return "redirect:" + VnPayConfig.vnp_PayUrl + "?" + queryUrl;
                    }
                }

                // COD TRUYỀN THỐNG
                int orderId = orderDAO.createOrder(user.getId(), totalPrice, paymentMethod);
                if (orderId > 0) {
                    orderDAO.saveOrderDetails(orderId, cart.getItems());

                    cart.clearCart();
                    session.setAttribute("cart", cart);

                    Order order = orderDAO.getOrderById(orderId);
                    model.addAttribute("order", order);

                    return "secure/invoice";
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
    // =============================================================
    // ROUTER: XỬ LÝ KHÁCH HÀNG BẤM "TÔI ĐÃ CHUYỂN KHOẢN" (VIETQR)
    // =============================================================
    @PostMapping("/vietqr-confirm")
    public String vietqrConfirm(@RequestParam("orderId") String orderIdStr, Model model) {
        try {
            int orderId = Integer.parseInt(orderIdStr);

            // Lấy thông tin đơn hàng từ Database
            Order order = orderDAO.getOrderById(orderId);

            if (order != null) {
                // Truyền đơn hàng ra giao diện Invoice
                model.addAttribute("order", order);

                // Hiển thị thông báo trạng thái PENDING chờ duyệt
                model.addAttribute("msg", "Đơn hàng #" + orderId + " đang ở trạng thái CHỜ XÁC NHẬN. Chúng tôi đang kiểm tra giao dịch chuyển khoản của bạn!");

                return "secure/invoice";
            }
        } catch (Exception e) {
            LOGGER.severe("VietQR confirm error: " + e.getMessage());
        }

        return "redirect:/secure/cart?error=" + URLEncoder.encode("Không tìm thấy đơn hàng để xác nhận.", StandardCharsets.UTF_8);
    }
    // =============================================================
    // ROUTER CALLBACK: ĐÃ SỬA THÊM KHỐI KIỂM TRA CHỮ KÝ BẢO MẬT PHẢN HỒI
    // =============================================================
    @GetMapping("/vnpay-callback")
    public String vnpayCallback(HttpServletRequest request, HttpSession session, Model model) {
        String vnp_ResponseCode = request.getParameter("vnp_ResponseCode");
        String vnp_TxnRef = request.getParameter("vnp_TxnRef");
        String vnp_Amount = request.getParameter("vnp_Amount");
        String vnp_SecureHashReceived = request.getParameter("vnp_SecureHash");

        if (vnp_TxnRef == null || vnp_TxnRef.isEmpty()) {
            return "redirect:/secure/cart?error=" + URLEncoder.encode("Tham số phản hồi không hợp lệ.", StandardCharsets.UTF_8);
        }

        // BẮT BUỘC: Lấy toàn bộ tham số trả về để kiểm tra tính toàn vẹn (Chữ ký phản hồi)
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = params.nextElement();
            String fieldValue = request.getParameter(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0) && fieldName.startsWith("vnp_")) {
                if (!fieldName.equals("vnp_SecureHash")) {
                    fields.put(fieldName, fieldValue);
                }
            }
        }

        // Sắp xếp dữ liệu nhận về theo bảng chữ cái alphabet giống như hàm hashAllFields trong Config.java mẫu
        List<String> fieldNames = new ArrayList<>(fields.keySet());
        Collections.sort(fieldNames);
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = fieldNames.iterator();
        while (iterator.hasNext()) {
            String fieldName = iterator.next();
            String fieldValue = fields.get(fieldName);
            sb.append(fieldName);
            sb.append("=");
            sb.append(fieldValue); // Dữ liệu nhận về từ request.getParameter đã tự decode, lấy trực tiếp không URL Encode nữa
            if (iterator.hasNext()) {
                sb.append("&");
            }
        }

        // Thực hiện tự tính toán chuỗi Hash phản hồi đầu cuối dựa vào HashSecret của bạn
        String signValue = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, sb.toString());

        // Đối soát chữ ký nhận được vs Chữ ký hệ thống tự tính toán xem khớp nhau hay không
        if (vnp_SecureHashReceived == null || !signValue.equalsIgnoreCase(vnp_SecureHashReceived)) {
            return "redirect:/secure/payment?error=" + URLEncoder.encode("Lỗi dữ liệu phản hồi: Chữ ký không hợp lệ!", StandardCharsets.UTF_8);
        }

        int orderId = Integer.parseInt(vnp_TxnRef);
        double amountReal = Double.parseDouble(vnp_Amount) / 100;

        try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
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

                model.addAttribute("msg", "Thanh toán qua cổng VNPay thành công!");
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
                return "redirect:/secure/payment?error=" + URLEncoder.encode("Giao dịch thanh toán thất bại.", StandardCharsets.UTF_8);
            }

        } catch (Exception e) {
            LOGGER.severe("VNPay callback error: " + e.getMessage());
            return "redirect:/secure/payment?error=" + URLEncoder.encode("Lỗi xử lý giao dịch cơ sở dữ liệu.", StandardCharsets.UTF_8);
        }
    }


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

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=hoa_don_" + orderId + ".pdf");

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            PdfFont font = PdfFontFactory.createFont("C:/Windows/Fonts/times.ttf", "Identity-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            document.setFont(font);

            document.add(new Paragraph("Hóa Đơn").setFontSize(20));
            document.add(new Paragraph("Mã Hóa Đơn: " + order.getId()));

            String paymentMethodDisplay = switch (order.getPaymentMethod() != null ? order.getPaymentMethod() : "UNKNOWN") {
                case "cash_on_delivery" -> "Tiền mặt (Thanh toán khi nhận hàng)";
                case "VNPAY" -> "Cổng thanh toán điện tử VNPay";
                case "credit_card" -> "Thẻ tín dụng";
                case "paypal" -> "PayPal";
                case "momo" -> "MoMo";
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