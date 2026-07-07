package dao;

import models.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.NoResultException;
import java.util.List;

@Repository
@Transactional
public class UserDAO {

    @PersistenceContext
    private EntityManager entityManager;

    public User getUser(int id) {
        return entityManager.find(User.class, id);
    }

    public User findById(int id) {
        return entityManager.find(User.class, id);
    }

    public List<User> getAllUsers() {
        try {
            return entityManager.createQuery("SELECT u FROM User u", User.class).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean registerUser(User user) {
        try {
            entityManager.persist(user);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean insertUser(User user) {
        return registerUser(user);
    }

    public boolean editProfile(User user, String name, String email, String phone, String address) {
        try {
            User existingUser = entityManager.find(User.class, user.getId());
            if (existingUser != null) {
                existingUser.setUsername(name);
                existingUser.setEmail(email);
                existingUser.setPhone(phone);
                existingUser.setAddress(address);
                entityManager.merge(existingUser);

                // Đồng bộ hóa dữ liệu với object tham chiếu truyền vào
                user.setUsername(name);
                user.setEmail(email);
                user.setPhone(phone);
                user.setAddress(address);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void saveImg(String path, int id) {
        try {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                user.setImg(path);
                entityManager.merge(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean changeImg(int id, String picPath) {
        try {
            User user = entityManager.find(User.class, id);
            if (user != null) {
                user.setImg(picPath);
                entityManager.merge(user);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getUserImg(int id) {
        User user = entityManager.find(User.class, id);
        return user != null ? user.getImg() : null;
    }

    public boolean updatePassword(String email, String password) {
        try {
            User user = findByEmail(email);
            if (user != null) {
                user.setPassword(password);
                entityManager.merge(user);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkEmailExist(String email) {
        return findByEmail(email) != null;
    }

    public User getLogin(String email, String password) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email AND u.password = :password", User.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean checkUsername(String username) {
        try {
            Long count = entityManager.createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return count > 0;
        } catch (Exception e) {
            return false;
        }
    }

    public User findByEmail(String email) {
        try {
            return entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean deleteUser(int userId) {
        try {
            User user = entityManager.find(User.class, userId);
            if (user != null) {
                entityManager.remove(user);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateUserGoogleId(User user) {
        try {
            User existingUser = findByEmail(user.getEmail());
            if (existingUser != null) {
                if (existingUser.getGoogleId() != null) {
                    return false;
                }
                existingUser.setGoogleId(user.getGoogleId());
                entityManager.merge(existingUser);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}