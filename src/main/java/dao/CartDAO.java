package dao;

import models.Cart;
import models.CartItem;
import models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Repository
public class CartDAO {

    private final DataSource dataSource;
    private final ProductDAO productDAO;

    @Autowired
    public CartDAO(DataSource dataSource, ProductDAO productDAO) {
        this.dataSource = dataSource;
        this.productDAO = productDAO;
    }

    public Cart getCartByUserId(int userId) throws SQLException {
        String sql = "SELECT * FROM `dbo.cart` WHERE UserId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int cartId = rs.getInt("CartId");
                    Cart cart = new Cart(cartId, userId);
                    List<CartItem> items = getCartItems(cartId);
                    for (CartItem item : items) {
                        cart.getItems().put(item.getProductId(), item);
                    }
                    return cart;
                }
            }
        }
        return null;
    }

    public void createCart(Cart cart) throws SQLException {
        String query = "INSERT INTO `dbo.cart` (UserId) VALUES (?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setLong(1, cart.getUserId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        cart.setCartId(rs.getInt(1));
                        System.out.println("Created cart with CartId: " + cart.getCartId() + " for UserId: " + cart.getUserId());
                    } else {
                        throw new SQLException("Failed to retrieve generated CartId for userId: " + cart.getUserId());
                    }
                }
            } else {
                throw new SQLException("Failed to create cart for userId: " + cart.getUserId());
            }
        }
    }

    public void updateCart(Cart cart) throws SQLException {
        String deleteItems = "DELETE FROM `dbo.cartitem` WHERE CartId = ?";
        String insertItem = "INSERT INTO `dbo.cartitem` (CartId, ProductId, Quantity) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement deleteStmt = connection.prepareStatement(deleteItems);
             PreparedStatement insertStmt = connection.prepareStatement(insertItem)) {

            deleteStmt.setInt(1, cart.getCartId());
            deleteStmt.executeUpdate();

            for (CartItem item : cart.getItems().values()) {
                insertStmt.setInt(1, cart.getCartId());
                insertStmt.setInt(2, item.getProductId());
                insertStmt.setInt(3, item.getQuantity());
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    public List<CartItem> getCartItems(int cartId) throws SQLException {
        String query = "SELECT * FROM `dbo.cartitem` WHERE CartId = ?";
        List<CartItem> items = new ArrayList<>();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cartId);
            try (ResultSet rs = stmt.executeQuery()) {
                // 🌟 5. XÓA BỎ 'new ProductDAO()' cũ, tận dụng luôn thuộc tính 'productDAO' được Spring tiêm ở trên
                while (rs.next()) {
                    int productId = rs.getInt("ProductId");
                    int quantity = rs.getInt("Quantity");
                    Product product = productDAO.getProductById(productId);
                    if (product != null) {
                        items.add(new CartItem(product, quantity));
                    }
                }
            }
        }
        return items;
    }

    public void clearCart(int cartId) throws SQLException {
        String query = "DELETE FROM `dbo.cartitem` WHERE CartId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cartId);
            stmt.executeUpdate();
        }
    }

    public boolean removeCartItem(int cartId, int productId) throws SQLException {
        String query = "DELETE FROM `dbo.cartitem` WHERE CartId = ? AND ProductId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cartId);
            stmt.setInt(2, productId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}