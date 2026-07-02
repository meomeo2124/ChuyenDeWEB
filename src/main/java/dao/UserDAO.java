package dao;

import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final Connection con;

    public UserDAO(Connection connection) {
        this.con = connection;
    }

    public User getUserRs(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setAddress(rs.getString("address"));
        user.setIsAdmin(rs.getBoolean("is_admin"));
        user.setPhone(rs.getString("phone_number"));
        user.setImg(rs.getString("images"));
        return user;
    }

    public boolean editProfile(User user, String name, String email, int phone, String address) {
        String sql = "UPDATE `dbo.users` SET username = ?, email = ?, phone_number = ?, address = ? WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, phone);
            ps.setString(4, address);
            ps.setInt(5, user.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User editProfileUser(User user, String name, String email, int phone, String address) {
        String sql = "UPDATE `dbo.users` SET username = ?, email = ?, phone_number = ?, address = ? WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setInt(3, phone);
            ps.setString(4, address);
            ps.setInt(5, user.getId());

            if (ps.executeUpdate() > 0) {
                user.setUsername(name);
                user.setEmail(email);
                user.setPhone(String.valueOf(phone));
                user.setAddress(address);
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void saveImg(String path, int id) {
        String sql = "UPDATE `dbo.users` SET images = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, path);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String getUserImg(int id) throws SQLException {
        String sql = "SELECT images FROM `dbo.users` WHERE id = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, id + "");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                return rs.getString(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public User getUser(int id) {
        String sql = "SELECT * FROM `dbo.users` WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return getUserRs(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updatePassword(String email, String password) throws SQLException {
        String query = "UPDATE `dbo.users` SET password=? WHERE email=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, password);
        ps.setString(2, email);
        int row = ps.executeUpdate();
        System.out.println(row);
        return row > 0;
    }

    public boolean registerUser(User user) throws SQLException {
        String sql = "insert into `dbo.users` (username, password, email, phone_number) VALUES (?,?,?,?)";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPhone());
            ps.executeUpdate();
        } catch (Exception e) {
            throw new SQLException(e);
        }
        return true;
    }

    public boolean checkEmailExist(String email) {
        String sql = "SELECT email FROM `dbo.users` WHERE email=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User getLogin(String email, String password) throws SQLException {
        PreparedStatement ps = con.prepareStatement("select * from `dbo.users` where email = ? and password = ?");
        ps.setString(1, email);
        ps.setString(2, password);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return getUserRs(rs);
            }
        }
        return null;
    }

    public boolean checkUsername(String username) {
        String sql = "select username from `dbo.users` where username=?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("username").equals(username)) {
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public User findByEmail(String email) {
        try {
            PreparedStatement ps = con.prepareStatement("select * from `dbo.users` where email = ?");
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getUserRs(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public User findById(int id) {
        try {
            PreparedStatement ps = con.prepareStatement("select * from `dbo.users` where id = ?");
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getUserRs(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean changeImg(int id, String picPath) throws SQLException {
        String query = "UPDATE `dbo.users` SET images=? WHERE id=?";
        PreparedStatement ps = con.prepareStatement(query);
        ps.setString(1, picPath);
        ps.setInt(2, id);
        int row = ps.executeUpdate();
        return row > 0;
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM `dbo.users`";

        try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                users.add(getUserRs(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Danh sách người dùng từ DB: " + users);
        return users;
    }

    public boolean deleteUser(int userId) throws SQLException {
        String sql = "DELETE FROM `dbo.users` WHERE id = ?";
        try (PreparedStatement stmt = this.con.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            int rowsDeleted = stmt.executeUpdate();
            return rowsDeleted > 0;
        }
    }

    // ĐÃ CHỈNH SỬA: Khắc phục triệt để lỗi Parameter index 5 và đồng bộ biến 'con' cùng tên bảng
    public boolean insertUser(User user) throws SQLException {
        String sql = "INSERT INTO `dbo.users` (username, password, email, address, is_admin, images, phone_number) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = this.con.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getAddress());

            // Tham số thứ 5: Khớp với cột is_admin
            pstmt.setBoolean(5, user.getIsAdmin());

            // Tham số thứ 6: Khớp với cột images
            pstmt.setString(6, user.getImg());

            // Tham số thứ 7: Khớp với cột phone_number
            pstmt.setString(7, user.getPhone());

            int rowAffected = pstmt.executeUpdate();
            return rowAffected > 0;
        }
    }

    public boolean updateUserGoogleId(User user) {
        String sqlCheck = "SELECT google_id FROM `dbo.users` WHERE email = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(sqlCheck)) {
            checkStmt.setString(1, user.getEmail());
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next() && rs.getString("google_id") != null) {
                System.out.println("Google ID đã tồn tại, không cần cập nhật!");
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sqlUpdate = "UPDATE `dbo.users` SET google_id = ? WHERE email = ?";
        try (PreparedStatement stmt = con.prepareStatement(sqlUpdate)) {
            stmt.setString(1, user.getGoogleId());
            stmt.setString(2, user.getEmail());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}