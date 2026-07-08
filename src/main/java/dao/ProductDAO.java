package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import models.Category;
import models.Product;

@Repository
public class ProductDAO {

    private final DataSource dataSource;

    @Autowired
    public ProductDAO(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public boolean updateFlashSaleStatus(int productId, int isFlashSale, int discount) {
        String sql = "UPDATE `dbo.product` SET is_flash_sale = ?, flash_sale_discount = ? WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, isFlashSale);
            ps.setInt(2, discount);
            ps.setInt(3, productId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 1. Lấy sản phẩm theo ID
    public Product getProductById(int id) {
        Product product = null;
        String sql = "SELECT * FROM `dbo.product` WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
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

                product.setIsFlashSale(resultSet.getInt("is_flash_sale"));
                product.setFlashSaleDiscount(resultSet.getInt("flash_sale_discount"));

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

    // 2. Thêm sản phẩm mới
    public void addProduct(Product product) throws SQLException {
        String query = "INSERT INTO `dbo.product` (product_name, description, images, price, stock, category_id, is_flash_sale, flash_sale_discount) VALUES (?, ?, ?, ?, ?, ?, 0, 0)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getPhoto() != null ? product.getPhoto() : "default.jpg");
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStock());

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
        try (Connection connection = dataSource.getConnection();
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

                product.setIsFlashSale(resultSet.getInt("is_flash_sale"));
                product.setFlashSaleDiscount(resultSet.getInt("flash_sale_discount"));

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

    // 4. Cập nhật thông tin sản phẩm
    public void updateProduct(Product product) {
        String sql = "UPDATE `dbo.product` SET product_name = ?, description = ?, images = ?, price = ?, stock = ?, category_id = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, product.getName());
            statement.setString(2, product.getDescription());
            statement.setString(3, product.getPhoto());
            statement.setDouble(4, product.getPrice());
            statement.setInt(5, product.getStock());

            if (product.getCategory() != null) {
                statement.setInt(6, product.getCategory().getId());
            } else {
                statement.setNull(6, java.sql.Types.INTEGER);
            }
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
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa sản phẩm: " + e.getMessage(), e);
        }
    }

    public Category getCategoryById(int categoryId) {
        Category category = null;
        String sql = "SELECT * FROM `dbo.product_categories` WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, categoryId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                category = new Category();
                category.setId(resultSet.getShort("id"));
                category.setTitle(resultSet.getString("category_name"));
                category.setDescription(resultSet.getString("description"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi lấy danh mục theo ID: " + e.getMessage(), e);
        }
        return category;
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product_categories`";
        try (Connection connection = dataSource.getConnection();
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

    public List<Product> getProductsByCategoryId(int categoryId) {
        List<Product> productList = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product` WHERE category_id = ?";
        try (Connection connection = dataSource.getConnection();
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

                product.setIsFlashSale(rs.getInt("is_flash_sale"));
                product.setFlashSaleDiscount(rs.getInt("flash_sale_discount"));

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

    public List<Product> searchProductByName(String name) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product` WHERE product_name LIKE ?";
        try (Connection connection = dataSource.getConnection();
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

                product.setIsFlashSale(resultSet.getInt("is_flash_sale"));
                product.setFlashSaleDiscount(resultSet.getInt("flash_sale_discount"));

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

    public List<Product> filteringProductByPrice(double minPrice, double maxPrice) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product` WHERE price BETWEEN ? AND ?";
        try (Connection connection = dataSource.getConnection();
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

                product.setIsFlashSale(resultSet.getInt("is_flash_sale"));
                product.setFlashSaleDiscount(resultSet.getInt("flash_sale_discount"));

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

    public List<Product> getTop4() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product` LIMIT 4";
        try (Connection connection = dataSource.getConnection();
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

                product.setIsFlashSale(resultSet.getInt("is_flash_sale"));
                product.setFlashSaleDiscount(resultSet.getInt("flash_sale_discount"));

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

    public List<Product> getNext4(int amount) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.product` ORDER BY id LIMIT 3 OFFSET ?";
        try (Connection connection = dataSource.getConnection();
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

                product.setIsFlashSale(resultSet.getInt("is_flash_sale"));
                product.setFlashSaleDiscount(resultSet.getInt("flash_sale_discount"));

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

    public double getTotalRevenue() {
        double totalRevenue = 0;
        String query = "SELECT SUM(price) AS totalRevenue FROM `dbo.product`";
        try (Connection connection = dataSource.getConnection();
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

    public List<models.Review> getReviewsByProductId(int productId) {
        List<models.Review> list = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.reviews` WHERE product_id = ? ORDER BY created_at DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    models.Review rev = new models.Review();
                    rev.setId(rs.getInt("id"));
                    rev.setProductId(rs.getInt("product_id"));
                    rev.setOrderId(rs.getInt("order_id"));
                    rev.setUsername(rs.getString("username"));
                    rev.setRating(rs.getInt("rating"));
                    rev.setComment(rs.getString("comment"));
                    rev.setImagePath(rs.getString("image_path"));
                    rev.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(rev);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insertReview(models.Review review) {
        String createTableSql = "CREATE TABLE IF NOT EXISTS `dbo.reviews` (" +
                "`id` INT AUTO_INCREMENT PRIMARY KEY, " +
                "`product_id` INT NOT NULL, " +
                "`order_id` INT DEFAULT 0, " +
                "`username` VARCHAR(100) NOT NULL, " +
                "`rating` INT NOT NULL, " +
                "`comment` TEXT, " +
                "`image_path` VARCHAR(500), " +
                "`created_at` DATETIME DEFAULT CURRENT_TIMESTAMP)";

        String sql = "INSERT INTO `dbo.reviews` (product_id, order_id, username, rating, comment, image_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = dataSource.getConnection()) {
            try (Statement st = con.createStatement()) { st.execute(createTableSql); }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, review.getProductId());
                ps.setInt(2, review.getOrderId());
                ps.setString(3, review.getUsername());
                ps.setInt(4, review.getRating());
                ps.setString(5, review.getComment());
                ps.setString(6, review.getImagePath());

                return ps.executeUpdate() > 0;
            } // Đã bổ sung ngoặc nhọn đóng khối PreparedStatement bị thiếu
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteReviewById(int reviewId) {
        String sql = "DELETE FROM `dbo.reviews` WHERE id = ?";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reviewId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<models.Review> getAllReviews() {
        List<models.Review> list = new ArrayList<>();
        String sql = "SELECT r.*, p.product_name FROM `dbo.reviews` r " +
                "JOIN `dbo.product` p ON r.product_id = p.id ORDER BY r.created_at DESC";
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                models.Review rev = new models.Review();
                rev.setId(rs.getInt("id"));
                rev.setProductId(rs.getInt("product_id"));
                rev.setOrderId(rs.getInt("order_id"));
                rev.setUsername(rs.getString("username"));
                rev.setRating(rs.getInt("rating"));
                rev.setComment(rs.getString("comment"));
                rev.setImagePath(rs.getString("image_path"));
                rev.setCreatedAt(rs.getTimestamp("created_at"));
                rev.setUsername(rs.getString("username") + " (Món: " + rs.getString("product_name") + ")");
                list.add(rev);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}