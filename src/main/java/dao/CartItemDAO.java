package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import models.Cart;
import models.CartItem;
import models.Product;

@Repository
public class CartItemDAO {

    private final DataSource dataSource;
    private final ProductDAO productDAO;

    @Autowired
    public CartItemDAO(DataSource dataSource, ProductDAO productDAO) {
        this.dataSource = dataSource;
        this.productDAO = productDAO;
    }

    public boolean addCartItem(int cartId, int productId, int quantity) throws SQLException {
        if (cartId <= 0) {
            throw new SQLException("Invalid CartId: " + cartId);
        }
        String sql = "INSERT INTO `dbo.cartitem` (CartId, ProductId, Quantity) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than 0");
            Product product = productDAO.getProductById(productId);
            if (product == null || quantity > product.getStock()) throw new IllegalArgumentException("Not enough stock");

            stmt.setInt(1, cartId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean addCartItem(Cart cart, Product product, int quantity) throws SQLException {
        if (cart.getCartId() <= 0) {
            throw new SQLException("Invalid CartId: " + cart.getCartId());
        }
        String sql = "INSERT INTO `dbo.cartitem` (CartId, ProductId, Quantity) VALUES (?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (quantity <= 0 || quantity > product.getStock()) {
                throw new IllegalArgumentException("Invalid quantity or not enough stock");
            }
            stmt.setInt(1, cart.getCartId());
            stmt.setInt(2, product.getId());
            stmt.setInt(3, quantity);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                cart.getItems().put(product.getId(), new CartItem(product, quantity));
                return true;
            }
            return false;
        }
    }

    public void setQuantity(Cart cart, Product product, int quantity) throws SQLException {
        if (cart.getCartId() <= 0) {
            throw new SQLException("Invalid CartId: " + cart.getCartId());
        }
        String sqlSelect = "SELECT Quantity FROM `dbo.cartitem` WHERE CartId = ? AND ProductId = ?";
        String sqlUpdate = "UPDATE `dbo.cartitem` SET Quantity = ? WHERE CartId = ? AND ProductId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmtSelect = connection.prepareStatement(sqlSelect);
             PreparedStatement stmtUpdate = connection.prepareStatement(sqlUpdate)) {
            if (quantity < 0) throw new IllegalArgumentException("Quantity cannot be negative");
            stmtSelect.setInt(1, cart.getCartId());
            stmtSelect.setInt(2, product.getId());
            int currentQuantity = 0;
            try (ResultSet rs = stmtSelect.executeQuery()) {
                if (rs.next()) currentQuantity = rs.getInt("Quantity");
            }
            if (quantity > product.getStock()) throw new IllegalArgumentException("Not enough stock");
            stmtUpdate.setInt(1, quantity);
            stmtUpdate.setInt(2, cart.getCartId());
            stmtUpdate.setInt(3, product.getId());
            int rowsAffected = stmtUpdate.executeUpdate();
            if (rowsAffected > 0 && cart.getItems().containsKey(product.getId())) {
                cart.getItems().get(product.getId()).setQuantity(quantity);
            }
        }
    }

    public int getQuantity(Cart cart, Product product) throws SQLException {
        if (cart.getCartId() <= 0) {
            throw new SQLException("Invalid CartId: " + cart.getCartId());
        }
        String sql = "SELECT Quantity FROM `dbo.cartitem` WHERE CartId = ? AND ProductId = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, cart.getCartId());
            stmt.setInt(2, product.getId());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getInt("Quantity") : 0;
            }
        }
    }
}