package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.CartItem;
import models.Order;
import models.OrderItem;
import models.Product;

public class OrderDAO {
    public int createOrder(int userId, double totalPrice, String paymentMethod) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        int orderId = 0;

        try {
            conn = DBConnectionPool.getConnection();
            conn.setAutoCommit(false);

            String insertOrderSQL = "INSERT INTO `dbo.orders` (user_id, total, payment_method, status, created_at) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertOrderSQL, PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, userId);
            pstmt.setDouble(2, totalPrice);
            pstmt.setString(3, paymentMethod != null ? paymentMethod : "UNKNOWN");
            pstmt.setString(4, "PENDING");
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();

            try (var rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    orderId = rs.getInt(1);
                } else {
                    throw new SQLException("Tạo hóa đơn thất bại, không lấy được ID.");
                }
            }

            conn.commit();
            return orderId;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Lỗi khi tạo hóa đơn: " + e.getMessage(), e);
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public void saveOrderDetails(int orderId, Map<Integer, CartItem> cartItems) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DBConnectionPool.getConnection();
            conn.setAutoCommit(false);

            String insertOrderDetailSQL = "INSERT INTO `dbo.order_details` (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertOrderDetailSQL);
            for (CartItem item : cartItems.values()) {
                pstmt.setInt(1, orderId);
                pstmt.setInt(2, item.getProduct().getId());
                pstmt.setInt(3, item.getQuantity());
                pstmt.setDouble(4, item.getProduct().getPrice());
                pstmt.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Lỗi khi lưu chi tiết hóa đơn: " + e.getMessage(), e);
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }

    public Order getOrderById(int orderId) throws SQLException {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Order order = null;

        try {
            conn = DBConnectionPool.getConnection();
            String selectOrderSQL = "SELECT id, user_id, total, payment_method, status, created_at FROM `dbo.orders` WHERE id = ?";
            pstmt = conn.prepareStatement(selectOrderSQL);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalPrice(rs.getDouble("total"));
                order.setPaymentMethod(rs.getString("payment_method") != null ? rs.getString("payment_method") : "UNKNOWN");
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("created_at"));

                String selectItemsSQL = "SELECT od.product_id, p.product_name AS product_name, od.quantity, od.price " +
                        "FROM `dbo.order_details` od JOIN `dbo.product` p ON od.product_id = p.id " +
                        "WHERE od.order_id = ?";
                pstmt = conn.prepareStatement(selectItemsSQL);
                pstmt.setInt(1, orderId);
                rs = pstmt.executeQuery();

                List<OrderItem> items = new ArrayList<>();
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    Product product = new Product();
                    product.setId(rs.getInt("product_id"));
                    product.setName(rs.getString("product_name"));
                    item.setProduct(product);
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("price"));
                    items.add(item);
                }
                order.setItems(items);
            }

            return order;
        } catch (SQLException e) {
            throw new SQLException("Lỗi khi lấy hóa đơn: " + e.getMessage(), e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}