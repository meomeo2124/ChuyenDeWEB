package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Category;

public class CategoryDAO {
    private Connection connection;

    public CategoryDAO(Connection connection) {
        this.connection = connection;
    }

    public CategoryDAO() {}

    public List<Category> getAllCategories() {
        List<Category> categoriesList = new ArrayList<>();
        String query = "SELECT * FROM `dbo.product_categories`";
        try (Connection conn = DBConnectionPool.getDataSource().getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            while (rs.next()) {
                Category category = new Category();
                category.setId(rs.getInt("id"));
                category.setTitle(rs.getString("category_name"));
                category.setDescription(rs.getString("description"));
                categoriesList.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categoriesList;
    }

    public boolean addCategory(Category category) {
        String query = "SELECT MAX(id) FROM `dbo.product_categories`";
        int newId = 0;

        try (Connection conn = DBConnectionPool.getDataSource().getConnection();
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery(query)) {

            if (rs.next()) {
                newId = rs.getInt(1) + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        query = "INSERT INTO `dbo.product_categories` (id, category_name, description) VALUES (?, ?, ?)";

        try (Connection conn = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {

            statement.setInt(1, newId);
            statement.setString(2, category.getTitle());
            statement.setString(3, category.getDescription());

            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateCategory(Category category) {
        String query = "UPDATE `dbo.product_categories` SET category_name = ?, description = ? WHERE id = ?";
        try (Connection conn = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, category.getTitle());
            stmt.setString(2, category.getDescription());
            stmt.setInt(3, category.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCategory(int id) {
        String query = "DELETE FROM `dbo.product_categories` WHERE id = ?";
        try (Connection conn = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ĐÃ SỬA: Lấy connection trực tiếp từ DataSource để tránh dính lỗi NullPointerException trên Dashboard
    public int getTotalCategories() {
        String query = "SELECT COUNT(*) FROM `dbo.product_categories`";
        try (Connection conn = DBConnectionPool.getDataSource().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Category getCategoryById(int id) {
        Category category = null;
        String query = "SELECT * FROM `dbo.product_categories` WHERE id = ?";
        try (Connection conn = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    category = new Category();
                    category.setId(rs.getInt("id"));
                    category.setTitle(rs.getString("category_name"));
                    category.setDescription(rs.getString("description"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }
}