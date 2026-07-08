package dao;

import models.Category;
import models.Product;
import models.Review;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Date;
import java.util.ArrayList;

@Repository
public class ProductDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // ==========================================
    // CÁC HÀM XỬ LÝ SẢN PHẨM (JPA MỚI)
    // ==========================================

    @Transactional(readOnly = true)
    public Product getProductById(int id) {
        return entityManager.find(Product.class, id);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return entityManager.createQuery("SELECT p FROM Product p LEFT JOIN FETCH p.category ORDER BY p.id DESC", Product.class).getResultList();
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoryId(int categoryId) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.category.id = :categoryId", Product.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Product> searchProductByName(String name) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.name LIKE :name", Product.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Product> filteringProductByPrice(double minPrice, double maxPrice) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice", Product.class)
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Product> getTop4() {
        return entityManager.createQuery("SELECT p FROM Product p ORDER BY p.id DESC", Product.class)
                .setMaxResults(4)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Product> getNext4(int amount) {
        return entityManager.createQuery("SELECT p FROM Product p ORDER BY p.id ASC", Product.class)
                .setFirstResult(amount)
                .setMaxResults(3)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public double getTotalRevenue() {
        Double total = entityManager.createQuery("SELECT SUM(p.price) FROM Product p", Double.class).getSingleResult();
        return total != null ? total : 0.0;
    }

    @Transactional
    public void addProduct(Product product) {
        entityManager.persist(product);
    }

    @Transactional
    public void updateProduct(Product product) {
        entityManager.merge(product);
    }

    @Transactional
    public void deleteProduct(int id) {
        Product product = entityManager.find(Product.class, id);
        if (product != null) {
            entityManager.remove(product);
        }
    }

    // ==========================================
    // TÍNH NĂNG MỚI (BẠN CỦA BẠN CODE) DÙNG JPA
    // ==========================================

    @Transactional
    public boolean updateFlashSaleStatus(int productId, int isFlashSale, int discount) {
        Product p = entityManager.find(Product.class, productId);
        if (p != null) {
            p.setIsFlashSale(isFlashSale);
            p.setFlashSaleDiscount(discount);
            entityManager.merge(p);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByProductId(int productId) {
        return entityManager.createQuery("SELECT r FROM Review r WHERE r.productId = :productId ORDER BY r.createdAt DESC", Review.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    @Transactional
    public boolean insertReview(Review review) {
        try {
            if(review.getCreatedAt() == null) {
                review.setCreatedAt(new Date());
            }
            entityManager.persist(review);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public boolean deleteReviewById(int reviewId) {
        Review review = entityManager.find(Review.class, reviewId);
        if (review != null) {
            entityManager.remove(review);
            return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        // Dùng JPQL JOIN để lấy tên Product cho trang Admin thay vì query thuần
        String jpql = "SELECT r, p.name FROM Review r JOIN Product p ON r.productId = p.id ORDER BY r.createdAt DESC";
        List<Object[]> results = entityManager.createQuery(jpql, Object[].class).getResultList();

        List<Review> list = new ArrayList<>();
        for (Object[] row : results) {
            Review rev = (Review) row[0];
            String productName = (String) row[1];

            // Set thông tin hiển thị lên trang admin (giống code cũ của bạn của bạn)
            rev.setUsername(rev.getUsername() + " (Món: " + productName + ")");
            rev.setProductName(productName);
            list.add(rev);
        }
        return list;
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ CATEGORY
    // ==========================================

    @Transactional(readOnly = true)
    public Category getCategoryById(int categoryId) {
        return entityManager.find(Category.class, categoryId);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }
}