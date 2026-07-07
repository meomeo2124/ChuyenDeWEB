package dao;

import models.Category;
import models.Product;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ProductDAO {

    @PersistenceContext
    private EntityManager entityManager;

    // ✅ Query-only methods
    @Transactional(readOnly = true)
    public Product getProductById(int id) {
        return entityManager.find(Product.class, id);
    }

    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
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

    @Transactional(readOnly = true)
    public Category getCategoryById(int categoryId) {
        return entityManager.find(Category.class, categoryId);
    }

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }

    // ✅ Write operations - Read-Write
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
}