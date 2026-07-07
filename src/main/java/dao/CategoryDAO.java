package dao;

import models.Category;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional // Bắt buộc phải có để Spring quản lý đóng/mở Transaction tự động
public class CategoryDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Lấy toàn bộ danh sách danh mục sản phẩm
     */
    public List<Category> getAllCategories() {
        try {
            return entityManager.createQuery("SELECT c FROM Category c", Category.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tìm kiếm danh mục theo ID (Khóa chính)
     */
    public Category getCategoryById(int id) {
        try {
            return entityManager.find(Category.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Thêm mới một danh mục
     * Lưu ý: Do thuộc tính id đã được cấu hình tự động tăng (IDENTITY) ở class Entity,
     * bạn không cần đoạn code tính toán SELECT MAX(id) + 1 như JDBC cũ nữa.
     * Hibernate sẽ tự động xử lý việc này khi insert vào DB.
     */
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
     * Cập nhật thông tin danh mục
     */
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
     * Xóa danh mục theo ID
     */
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
     * Đếm tổng số lượng danh mục đang có trong cơ sở dữ liệu
     */
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