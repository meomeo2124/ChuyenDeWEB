package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import models.Cart;
import models.CartItem;
import models.Order;
import models.OrderItem;

public class PaymentDAO {
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
            pstmt.setString(3, paymentMethod);
            pstmt.setString(4, "PENDING");
            pstmt.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            pstmt.executeUpdate();

            try (var rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    orderId = rs.getInt(1);
                } else {
                    throw new SQLException("Creating order failed, no ID obtained.");
                }
            }

            conn.commit();
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Error creating order: " + e.getMessage(), e);
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }

        return orderId;
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
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new SQLException("Error saving order details: " + e.getMessage(), e);
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
            String selectOrderSQL = "SELECT id, user_id, total, payment_method FROM `dbo.orders` WHERE id = ?";
            pstmt = conn.prepareStatement(selectOrderSQL);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalPrice(rs.getDouble("total"));
                order.setPaymentMethod(rs.getString("payment_method"));

                // Get order items
                String selectItemsSQL = "SELECT od.product_id, p.name AS product_name, od.quantity, od.price " +
                        "FROM `dbo.order_details` od JOIN `dbo.products` p ON od.product_id = p.id " +
                        "WHERE od.order_id = ?";
                pstmt = conn.prepareStatement(selectItemsSQL);
                pstmt.setInt(1, orderId);
                rs = pstmt.executeQuery();

                List<OrderItem> items = new ArrayList<>();
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setProductId(rs.getInt("product_id"));
                    item.setProductName(rs.getString("product_name"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getDouble("price"));
                    items.add(item);
                }
                order.setItems(items);
            }

            return order;
        } catch (SQLException e) {
            throw new SQLException("Error retrieving order: " + e.getMessage(), e);
        } finally {
            if (rs != null) try { rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
            if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}