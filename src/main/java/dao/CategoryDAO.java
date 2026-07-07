package dao;

import models.Category;
import exception.ResourceNotFoundException;
import exception.DatabaseException;
import exception.ValidationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
public class CategoryDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * ✅ Lấy toàn bộ danh sách danh mục
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        try {
            return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
        } catch (Exception e) {
            throw new DatabaseException("SELECT", "Category", "Failed to fetch all categories", e);
        }
    }

    /**
     * ✅ Tìm kiếm danh mục theo ID
     */
    @Transactional(readOnly = true)
    public Category getCategoryById(int id) {
        if (id <= 0) {
            throw new ValidationException("id", id, "ID must be greater than 0");
        }

        Category category = entityManager.find(Category.class, id);
        if (category == null) {
            throw new ResourceNotFoundException("Category", "id", id);
        }
        return category;
    }

    /**
     * ✅ Thêm mới một danh mục
     */
    @Transactional
    public boolean addCategory(Category category) {
        if (category == null) {
            throw new ValidationException("category", null, "Category object cannot be null");
        }
        if (category.getTitle() == null || category.getTitle().trim().isEmpty()) {
            throw new ValidationException("title", category.getTitle(), "Category title cannot be empty");
        }

        try {
            entityManager.persist(category);
            return true;
        } catch (Exception e) {
            throw new DatabaseException("INSERT", "Category", "Failed to add category", e);
        }
    }

    /**
     * ✅ Cập nhật thông tin danh mục
     */
    @Transactional
    public boolean updateCategory(Category category) {
        if (category == null || category.getId() <= 0) {
            throw new ValidationException("id", category != null ? category.getId() : null, "Valid ID is required");
        }

        // Kiểm tra category có tồn tại không
        Category existing = entityManager.find(Category.class, category.getId());
        if (existing == null) {
            throw new ResourceNotFoundException("Category", "id", category.getId());
        }

        try {
            entityManager.merge(category);
            return true;
        } catch (Exception e) {
            throw new DatabaseException("UPDATE", "Category", "Failed to update category", e);
        }
    }

    /**
     * ✅ Xóa danh mục theo ID
     */
    @Transactional
    public boolean deleteCategory(int id) {
        if (id <= 0) {
            throw new ValidationException("id", id, "ID must be greater than 0");
        }

        try {
            Category category = entityManager.find(Category.class, id);
            if (category == null) {
                throw new ResourceNotFoundException("Category", "id", id);
            }
            entityManager.remove(category);
            return true;
        } catch (ResourceNotFoundException e) {
            throw e; // Re-throw custom exception
        } catch (Exception e) {
            throw new DatabaseException("DELETE", "Category", "Failed to delete category", e);
        }
    }

    /**
     * ✅ Đếm tổng số lượng danh mục
     */
    @Transactional(readOnly = true)
    public int getTotalCategories() {
        try {
            Long count = entityManager.createQuery("SELECT COUNT(c) FROM Category c", Long.class).getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            throw new DatabaseException("COUNT", "Category", "Failed to count categories", e);
        }
    }
}