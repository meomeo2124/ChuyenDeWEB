package dao;

import models.Category;
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
     * ✅ Lấy toàn bộ danh sách danh mục sản phẩm - Read-only
     */
    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        try {
            return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ✅ Tìm kiếm danh mục theo ID - Read-only
     */
    @Transactional(readOnly = true)
    public Category getCategoryById(int id) {
        try {
            return entityManager.find(Category.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ✅ Thêm mới một danh mục - Read-Write
     */
    @Transactional  // hoặc @Transactional(readOnly = false)
    public boolean addCategory(Category category) {
        try {
            entityManager.persist(category);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ✅ Cập nhật thông tin danh mục - Read-Write
     */
    @Transactional
    public boolean updateCategory(Category category) {
        try {
            entityManager.merge(category);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ✅ Xóa danh mục theo ID - Read-Write
     */
    @Transactional
    public boolean deleteCategory(int id) {
        try {
            Category category = entityManager.find(Category.class, id);
            if (category != null) {
                entityManager.remove(category);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ✅ Đếm tổng số lượng danh mục - Read-only
     */
    @Transactional(readOnly = true)
    public int getTotalCategories() {
        try {
            Long count = entityManager.createQuery("SELECT COUNT(c) FROM Category c", Long.class).getSingleResult();
            return count != null ? count.intValue() : 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}