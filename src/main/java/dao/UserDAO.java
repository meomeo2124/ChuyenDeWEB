package dao;

import models.User;
import exception.ResourceNotFoundException;
import exception.DatabaseException;
import exception.ValidationException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import java.util.List;

@Repository
public class UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * ✅ Lấy user theo ID
     */
    @Transactional(readOnly = true)
    public User getUser(int id) {
        if (id <= 0) {
            throw new ValidationException("id", id, "ID must be greater than 0");
        }

        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        return user;
    }

    /**
     * ✅ Tìm user theo ID
     */
    @Transactional(readOnly = true)
    public User findById(int id) {
        return getUser(id); // Reuse getUser logic
    }

    /**
     * ✅ Lấy tất cả user
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        try {
            return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        } catch (Exception e) {
            throw new DatabaseException("SELECT", "User", "Failed to fetch all users", e);
        }
    }

    /**
     * ✅ Kiểm tra login
     */
    @Transactional(readOnly = true)
    public User getLogin(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", email, "Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("password", password, "Password cannot be empty");
        }

        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException("User", "email/password", email);
        } catch (Exception e) {
            throw new DatabaseException("SELECT", "User", "Failed to get login", e);
        }
    }

    /**
     * ✅ Kiểm tra email tồn tại
     */
    @Transactional(readOnly = true)
    public boolean checkEmailExist(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", email, "Email cannot be empty");
        }
        return findByEmail(email) != null;
    }

    /**
     * ✅ Kiểm tra username tồn tại
     */
    @Transactional(readOnly = true)
    public boolean checkUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidationException("username", username, "Username cannot be empty");
        }

        try {
            Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            throw new DatabaseException("COUNT", "User", "Failed to check username", e);
        }
    }

    /**
     * ✅ Tìm user theo email
     */
    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", email, "Email cannot be empty");
        }

        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new ResourceNotFoundException("User", "email", email);
        } catch (Exception e) {
            throw new DatabaseException("SELECT", "User", "Failed to find user by email", e);
        }
    }

    /**
     * ✅ Lấy hình ảnh user
     */
    @Transactional(readOnly = true)
    public String getUserImg(int id) {
        if (id <= 0) {
            throw new ValidationException("id", id, "ID must be greater than 0");
        }

        User user = entityManager.find(User.class, id);
        if (user == null) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        return user.getImg();
    }

    /**
     * ✅ Đăng ký user
     */
    @Transactional
    public boolean registerUser(User user) {
        if (user == null) {
            throw new ValidationException("user", null, "User object cannot be null");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ValidationException("email", user.getEmail(), "Email cannot be empty");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new ValidationException("password", user.getPassword(), "Password cannot be empty");
        }

        try {
            entityManager.persist(user);
            return true;
        } catch (Exception e) {
            throw new DatabaseException("INSERT", "User", "Failed to register user", e);
        }
    }

    /**
     * ✅ Insert user
     */
    @Transactional
    public boolean insertUser(User user) {
        return registerUser(user); // Reuse registerUser logic
    }

    /**
     * ✅ Sửa profile user
     */
    @Transactional
    public boolean editProfile(User user, String name, String email, String phone, String address) {
        if (user == null || user.getId() <= 0) {
            throw new ValidationException("user", user, "Valid user is required");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("name", name, "Name cannot be empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", email, "Email cannot be empty");
        }

        try {
            User existingUser = entityManager.find(User.class, user.getId());
            if (existingUser == null) {
                throw new ResourceNotFoundException("User", "id", user.getId());
            }

            existingUser.setUsername(name);
            existingUser.setEmail(email);
            existingUser.setPhone(phone);
            existingUser.setAddress(address);
            entityManager.merge(existingUser);

            // Sync object
            user.setUsername(name);
            user.setEmail(email);
            user.setPhone(phone);
            user.setAddress(address);
            return true;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("UPDATE", "User", "Failed to edit profile", e);
        }
    }

    /**
     * ✅ Lưu hình ảnh user
     */
    @Transactional
    public void saveImg(String path, int id) {
        if (id <= 0) {
            throw new ValidationException("id", id, "ID must be greater than 0");
        }
        if (path == null || path.trim().isEmpty()) {
            throw new ValidationException("path", path, "Image path cannot be empty");
        }

        try {
            User user = entityManager.find(User.class, id);
            if (user == null) {
                throw new ResourceNotFoundException("User", "id", id);
            }
            user.setImg(path);
            entityManager.merge(user);
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("UPDATE", "User", "Failed to save image", e);
        }
    }

    /**
     * ✅ Thay đổi hình ảnh user
     */
    @Transactional
    public boolean changeImg(int id, String picPath) {
        if (id <= 0) {
            throw new ValidationException("id", id, "ID must be greater than 0");
        }
        if (picPath == null || picPath.trim().isEmpty()) {
            throw new ValidationException("picPath", picPath, "Image path cannot be empty");
        }

        try {
            User user = entityManager.find(User.class, id);
            if (user == null) {
                throw new ResourceNotFoundException("User", "id", id);
            }
            user.setImg(picPath);
            entityManager.merge(user);
            return true;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("UPDATE", "User", "Failed to change image", e);
        }
    }

    /**
     * ✅ Cập nhật password
     */
    @Transactional
    public boolean updatePassword(String email, String password) {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("email", email, "Email cannot be empty");
        }
        if (password == null || password.trim().isEmpty()) {
            throw new ValidationException("password", password, "Password cannot be empty");
        }

        try {
            User user = findByEmail(email);
            if (user == null) {
                throw new ResourceNotFoundException("User", "email", email);
            }
            user.setPassword(password);
            entityManager.merge(user);
            return true;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("UPDATE", "User", "Failed to update password", e);
        }
    }

    /**
     * ✅ Xóa user
     */
    @Transactional
    public boolean deleteUser(int userId) {
        if (userId <= 0) {
            throw new ValidationException("userId", userId, "User ID must be greater than 0");
        }

        try {
            User user = entityManager.find(User.class, userId);
            if (user == null) {
                throw new ResourceNotFoundException("User", "id", userId);
            }
            entityManager.remove(user);
            return true;
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("DELETE", "User", "Failed to delete user", e);
        }
    }

    /**
     * ✅ Cập nhật Google ID
     */
    @Transactional
    public boolean updateUserGoogleId(User user) {
        if (user == null || user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new ValidationException("user", user, "User with valid email is required");
        }
        if (user.getGoogleId() == null || user.getGoogleId().trim().isEmpty()) {
            throw new ValidationException("googleId", user.getGoogleId(), "Google ID cannot be empty");
        }

        try {
            User existingUser = findByEmail(user.getEmail());
            if (existingUser == null) {
                throw new ResourceNotFoundException("User", "email", user.getEmail());
            }

            if (existingUser.getGoogleId() != null && !existingUser.getGoogleId().isEmpty()) {
                throw new ValidationException("googleId", existingUser.getGoogleId(), "Google ID already exists");
            }

            existingUser.setGoogleId(user.getGoogleId());
            entityManager.merge(existingUser);
            return true;
        } catch (ResourceNotFoundException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new DatabaseException("UPDATE", "User", "Failed to update Google ID", e);
        }
    }
}