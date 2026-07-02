package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Category;
import models.Product;

public class ProductDAO {
    private Connection con;

    public ProductDAO(Connection connection) {
        this.con = connection;
    }

    public ProductDAO() {
    }

    // 1. Lấy sản phẩm theo ID
    public Product getProductById(int id) {
        Product product = null;
        String sql = "SELECT * FROM `dbo.product` WHERE id = ?";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("product_name"));
                product.setDescription(resultSet.getString("description"));
                product.setPhoto(resultSet.getString("images"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));

                int categoryId = resultSet.getInt("category_id");
                Category category = getCategoryById(categoryId);
                product.setCategory(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy sản phẩm theo ID: " + e.getMessage(), e);
        }
        return product;
    }

    // 2. Thêm sản phẩm mới (ĐÃ BỔ SUNG CỘT category_id)
    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO `dbo.product` (product_name, description, images, price, stock, category_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getPhoto() != null ? product.getPhoto() : "default.jpg");
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStock());

            // Tham số thứ 6: category_id
            if (product.getCategory() != null) {
                statement.setInt(6, product.getCategory().getId());
            } else {
                statement.setNull(6, java.sql.Types.INTEGER);
            }

            statement.executeUpdate();
        }
    }

    // 3. Lấy tất cả sản phẩm
    public List<Product> getAllProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product`";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("product_name"));
                product.setDescription(resultSet.getString("description"));
                product.setPhoto(resultSet.getString("images"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));

                int categoryId = resultSet.getInt("category_id");
                Category category = getCategoryById(categoryId);
                product.setCategory(category);

                products.add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy danh sách sản phẩm: " + e.getMessage(), e);
        }
        return products;
    }

    // 4. Cập nhật thông tin sản phẩm (ĐÃ ĐỒNG BỘ CỘT stock VÀ category_id)
    public void updateProduct(Product product) {
        String sql = "UPDATE `dbo.product` SET product_name = ?, description = ?, images = ?, price = ?, stock = ?, category_id = ? WHERE id = ?";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getPhoto());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStock());

            // Tham số thứ 6: category_id
            if (product.getCategory() != null) {
                statement.setInt(6, product.getCategory().getId());
            } else {
                statement.setNull(6, java.sql.Types.INTEGER);
            }

            // Tham số thứ 7: id điều kiện WHERE
            statement.setInt(7, product.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật sản phẩm: " + e.getMessage(), e);
        }
    }

    // 5. Xóa sản phẩm
    public void deleteProduct(int id) {
        String sql = "DELETE FROM `dbo.product` WHERE id = ?";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa sản phẩm: " + e.getMessage(), e);
        }
    }

    // Helper method để lấy thông tin Category theo ID
    public Category getCategoryById(int categoryId) {
        Category category = null;
        String sql = "SELECT * FROM `dbo.product_categories` WHERE id = ?";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                category = new Category();
                category.setId(resultSet.getShort("id")); // Hoặc getInt tùy thuộc kiểu dữ liệu Model của bạn
                category.setTitle(resultSet.getString("category_name"));
                category.setDescription(resultSet.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh mục theo ID: " + e.getMessage(), e);
        }
        return category;
    }

    // Lấy tất cả danh mục (dùng cho form thêm/sửa sản phẩm)
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product_categories`";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Category category = new Category();
                category.setId(resultSet.getInt("id"));
                category.setTitle(resultSet.getString("category_name"));
                category.setDescription(resultSet.getString("description"));
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh sách danh mục: " + e.getMessage(), e);
        }
        return categories;
    }

    // Lấy sản phẩm theo Category ID
    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product` WHERE category_id = ?";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, categoryId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Product product = new Product();
                product.setId(rs.getInt("id"));
                product.setName(rs.getString("product_name"));
                product.setDescription(rs.getString("description"));
                product.setPhoto(rs.getString("images"));
                product.setPrice(rs.getDouble("price"));
                product.setStock(rs.getInt("stock"));

                Category category = getCategoryById(categoryId);
                product.setCategory(category);
                productList.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy sản phẩm theo danh mục: " + e.getMessage(), e);
        }
        return productList;
    }

    // Tìm kiếm sản phẩm theo tên
    public List<Product> searchProductByName(String name) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product` WHERE product_name LIKE ?";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "%" + name + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("product_name"));
                product.setDescription(resultSet.getString("description"));
                product.setPhoto(resultSet.getString("images"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));

                int categoryId = resultSet.getInt("category_id");
                Category category = getCategoryById(categoryId);
                product.setCategory(category);

                list.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tìm kiếm sản phẩm: " + e.getMessage(), e);
        }
        return list;
    }

    // Lọc sản phẩm theo giá
    public List<Product> filteringProductByPrice(double minPrice, double maxPrice) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product` WHERE price BETWEEN ? AND ?";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, minPrice);
            statement.setDouble(2, maxPrice);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("product_name"));
                product.setDescription(resultSet.getString("description"));
                product.setPhoto(resultSet.getString("images"));
                product.setPrice(resultSet.getDouble("price"));
                product.setStock(resultSet.getInt("stock"));

                int categoryId = resultSet.getInt("category_id");
                Category category = getCategoryById(categoryId);
                product.setCategory(category);

                list.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lọc sản phẩm theo giá: " + e.getMessage(), e);
        }
        return list;
    }

    // Lấy top 4 sản phẩm
    public List<Product> getTop4() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT TOP 4 * FROM `dbo.product`";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("product_name"));
                product.setDescription(resultSet.getString("description"));
                product.setPhoto(resultSet.getString("images"));
                product.setStock(resultSet.getInt("stock"));
                product.setPrice(resultSet.getDouble("price"));

                int categoryId = resultSet.getInt("category_id");
                Category category = getCategoryById(categoryId);
                product.setCategory(category);

                list.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy top 4 sản phẩm: " + e.getMessage(), e);
        }
        return list;
    }

    // Lấy sản phẩm tiếp theo (phân trang)
    public List<Product> getNext4(int amount) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product` ORDER BY id OFFSET ? ROWS FETCH NEXT 3 ROWS ONLY";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, amount);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Product product = new Product();
                product.setId(resultSet.getInt("id"));
                product.setName(resultSet.getString("product_name"));
                product.setDescription(resultSet.getString("description"));
                product.setPhoto(resultSet.getString("images"));
                product.setStock(resultSet.getInt("stock"));
                product.setPrice(resultSet.getDouble("price"));

                int categoryId = resultSet.getInt("category_id");
                Category category = getCategoryById(categoryId);
                product.setCategory(category);

                list.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy sản phẩm phân trang: " + e.getMessage(), e);
        }
        return list;
    }

    // Tính tổng doanh thu
    public double getTotalRevenue() {
        double totalRevenue = 0;
        String query = "SELECT SUM(price) AS totalRevenue FROM `dbo.product`";
        try (Connection connection = DBConnectionPool.getDataSource().getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery(query)) {
            if (rs.next()) {
                totalRevenue = rs.getDouble("totalRevenue");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tính tổng doanh thu: " + e.getMessage(), e);
        }
        return totalRevenue;
    }
}