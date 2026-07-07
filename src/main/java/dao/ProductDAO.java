package dao;

import models.Category;
import models.Product;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional
public class ProductDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public Product getProductById(int id) {
        return entityManager.find(Product.class, id);
    }

    public void addProduct(Product product) {
        entityManager.persist(product);
    }

    public List<Product> getAllProducts() {
        return entityManager.createQuery("SELECT p FROM Product p", Product.class).getResultList();
    }

    public void updateProduct(Product product) {
        entityManager.merge(product);
    }

    public void deleteProduct(int id) {
        Product product = entityManager.find(Product.class, id);
        if (product != null) {
            entityManager.remove(product);
        }
    }

    public List<Product> getProductsByCategoryId(int categoryId) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.category.id = :categoryId", Product.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    public List<Product> searchProductByName(String name) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.name LIKE :name", Product.class)
                .setParameter("name", "%" + name + "%")
                .getResultList();
    }

    public List<Product> filteringProductByPrice(double minPrice, double maxPrice) {
        return entityManager.createQuery("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice", Product.class)
                .setParameter("minPrice", minPrice)
                .setParameter("maxPrice", maxPrice)
                .getResultList();
    }

    public List<Product> getTop4() {
        return entityManager.createQuery("SELECT p FROM Product p ORDER BY p.id DESC", Product.class)
                .setMaxResults(4)
                .getResultList();
    }

    public List<Product> getNext4(int amount) {
        return entityManager.createQuery("SELECT p FROM Product p ORDER BY p.id ASC", Product.class)
                .setFirstResult(amount)
                .setMaxResults(3)
                .getResultList();
    }

    public double getTotalRevenue() {
        Double total = entityManager.createQuery("SELECT SUM(p.price) FROM Product p", Double.class).getSingleResult();
        return total != null ? total : 0.0;
    }

    public Category getCategoryById(int categoryId) {
        return entityManager.find(Category.class, categoryId);
    }

    public List<Category> getAllCategories() {
        return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
    }
}