package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import models.CartItem;
import models.Order;
import models.OrderItem;
import models.Product;

@Repository
public class OrderDAO {

    private final DataSource dataSource;

    @Autowired
    public OrderDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean hasUserPurchasedProduct(int userId, int productId) {
        String sql = "SELECT COUNT(*) FROM `dbo.orders` o " +
                "JOIN `dbo.order_details` od ON o.id = od.order_id " +
                "WHERE o.user_id = ? AND od.product_id = ? AND o.status = 'PAID'";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Trả về true nếu người dùng đã từng mua thành công món này
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public int createOrder(int userId, double totalPrice, String paymentMethod) throws SQLException {
        String insertOrderSQL = "INSERT INTO `dbo.orders` (user_id, total, status, created_at) VALUES (?, ?, ?, ?)";
        int orderId = 0;

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, userId);
                pstmt.setDouble(2, totalPrice);
                pstmt.setString(3, "PENDING");
                pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
                pstmt.executeUpdate();

                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException("Tạo hóa đơn thất bại, không lấy được ID.");
                    }
                }
                conn.commit();
                return orderId;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public void saveOrderDetails(int orderId, Map<Integer, CartItem> cartItems) throws SQLException {
        String insertOrderDetailSQL = "INSERT INTO `dbo.order_details` (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderDetailSQL)) {
                for (CartItem item : cartItems.values()) {
                    pstmt.setInt(1, orderId);
                    pstmt.setInt(2, item.getProduct().getId());
                    pstmt.setInt(3, item.getQuantity());
                    pstmt.setDouble(4, item.getProduct().getPrice());
                    pstmt.executeUpdate();
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public List<Order> getOrdersBySubsets(String startDate, String endDate) throws SQLException {
        List<Order> orderList = new ArrayList<>();

        String sql = "SELECT o.id, o.user_id, o.total, o.status, o.created_at, u.username " +
                "FROM `dbo.orders` o " +
                "JOIN `dbo.users` u ON o.user_id = u.id " +
                "WHERE o.created_at BETWEEN ? AND ? " +
                "ORDER BY o.created_at DESC";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, startDate);
            ps.setString(2, endDate);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setId(rs.getInt("id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setTotalPrice(rs.getDouble("total"));
                    order.setStatus(rs.getString("status"));
                    order.setOrderDate(rs.getTimestamp("created_at"));
                    order.setShippingAddress(rs.getString("username"));
                    orderList.add(order);
                }
            }
        }
        return orderList;
    }

    public List<Order> getAllOrdersWithUser() throws SQLException {
        List<Order> orderList = new ArrayList<>();
        String sql = "SELECT o.id, o.user_id, o.total, o.status, o.created_at, u.username FROM `dbo.orders` o JOIN `dbo.users` u ON o.user_id = u.id ORDER BY o.created_at DESC";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Order order = new Order();
                order.setId(rs.getInt("id"));
                order.setUserId(rs.getInt("user_id"));
                order.setTotalPrice(rs.getDouble("total"));
                order.setStatus(rs.getString("status"));
                order.setOrderDate(rs.getTimestamp("created_at"));
                order.setShippingAddress(rs.getString("username"));
                orderList.add(order);
            }
        }
        return orderList;
    }

    public void updateOrderStatusAndPayment(int orderId, String status) throws SQLException {
        String updateOrderSql = "UPDATE `dbo.orders` SET status = ? WHERE id = ?";
        String updatePaymentSql = "UPDATE `dbo.payment` SET status = ? WHERE order_id = ?";
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement psOrder = connection.prepareStatement(updateOrderSql);
                 PreparedStatement psPayment = connection.prepareStatement(updatePaymentSql)) {

                psOrder.setString(1, status);
                psOrder.setInt(2, orderId);
                psOrder.executeUpdate();

                psPayment.setString(1, status);
                psPayment.setInt(2, orderId);
                psPayment.executeUpdate();

                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        }
    }

    public Order getOrderById(int orderId) throws SQLException {
        String selectOrderSQL = "SELECT id, user_id, total, status, created_at FROM `dbo.orders` WHERE id = ?";
        String selectItemsSQL = "SELECT od.product_id, p.product_name AS product_name, od.quantity, od.price " +
                "FROM `dbo.order_details` od JOIN `dbo.product` p ON od.product_id = p.id " +
                "WHERE od.order_id = ?";
        Order order = null;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmtOrder = conn.prepareStatement(selectOrderSQL);
             PreparedStatement pstmtItems = conn.prepareStatement(selectItemsSQL)) {

            pstmtOrder.setInt(1, orderId);
            try (ResultSet rsOrder = pstmtOrder.executeQuery()) {
                if (rsOrder.next()) {
                    order = new Order();
                    order.setId(rsOrder.getInt("id"));
                    order.setUserId(rsOrder.getInt("user_id"));
                    order.setTotalPrice(rsOrder.getDouble("total"));
                    order.setPaymentMethod("CASH");
                    order.setStatus(rsOrder.getString("status"));
                    order.setOrderDate(rsOrder.getTimestamp("created_at"));

                    pstmtItems.setInt(1, orderId);
                    try (ResultSet rsItems = pstmtItems.executeQuery()) {
                        List<OrderItem> items = new ArrayList<>();
                        while (rsItems.next()) {
                            OrderItem item = new OrderItem();
                            Product product = new Product();
                            product.setId(rsItems.getInt("product_id"));
                            product.setName(rsItems.getString("product_name"));
                            item.setProduct(product);
                            item.setProductName(rsItems.getString("product_name"));
                            item.setQuantity(rsItems.getInt("quantity"));
                            item.setPrice(rsItems.getDouble("price"));
                            items.add(item);
                        }
                        order.setItems(items);
                    }
                }
            }
            return order;
        }
    }
}