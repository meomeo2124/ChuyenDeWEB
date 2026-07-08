package config;

import dao.UserDAO;
import models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDAO userDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // LƯU Ý 1: Sửa lại tên hàm lấy user cho đúng với file UserDAO của nhóm bạn
        // (Ví dụ: userDAO.getUserByEmail(username) hoặc userDAO.findByUsername(username))
        User user = userDAO.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Sai tài khoản hoặc mật khẩu");
        }

        // LƯU Ý 2: Phân quyền. Bạn kiểm tra xem file User.java của bạn lưu quyền (role) như thế nào.
        // Giả sử nếu có hàm user.isAdmin() trả về true/false:
        String role = "ROLE_USER";
        /* Nếu code nhóm bạn có phân biệt Admin thì bỏ comment đoạn này:
        if (user.getRole().equals("Admin")) { // Hoặc user.isAdmin() == true
            role = "ROLE_ADMIN";
        }
        */

        // Trả về đối tượng User chuẩn của Spring Security
        return new org.springframework.security.core.userdetails.User(
                username,
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}