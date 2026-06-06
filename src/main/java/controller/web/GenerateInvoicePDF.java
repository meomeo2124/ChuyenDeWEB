package controller.web;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import dao.OrderDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Order;
import models.OrderItem;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/secure/generateInvoicePDF")
public class GenerateInvoicePDF extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private OrderDAO orderDAO;

    @Override
    public void init() throws ServletException {
        orderDAO = new OrderDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/secure/invoice.jsp?error=Mã hóa đơn không hợp lệ");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdStr);
            Order order = orderDAO.getOrderById(orderId);
            if (order == null) {
                response.sendRedirect(request.getContextPath() + "/secure/invoice.jsp?error=Không tìm thấy hóa đơn");
                return;
            }

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=hoa_don_" + orderId + ".pdf");

            PdfWriter writer = new PdfWriter(response.getOutputStream());
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            // Nhúng font Times New Roman hoặc DejaVu Sans
            PdfFont font = PdfFontFactory.createFont("C:/Windows/Fonts/times.ttf", "Identity-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
            // PdfFont font = PdfFontFactory.createFont("fonts/DejaVuSans.ttf", "Identity-H", PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);

            // Đặt font cho toàn bộ văn bản
            document.setFont(font);

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

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/secure/invoice.jsp?error=Lỗi khi tạo PDF: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/secure/invoice.jsp?error=Lỗi khi tạo font: " + e.getMessage());
        }
    }
}