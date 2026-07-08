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
        // Mặc dù Spring Security gọi biến này là "username",
        // nhưng thực tế hệ thống của bạn đang tìm kiếm bằng EMAIL.
        User user = userDAO.findByEmail(username);

        if (user == null) {
            throw new UsernameNotFoundException("Sai tài khoản hoặc mật khẩu");
        }

        // 1. Mặc định cấp quyền USER
        String role = "ROLE_USER";

        // 2. 🔥 ĐÃ FIX: Mở khóa cấp quyền ADMIN.
        // Nếu là Admin thì gán ROLE_ADMIN để vào được trang Dashboard
        if (user.getIsAdmin()) {
            role = "ROLE_ADMIN";
        }

        // Trả về đối tượng User chuẩn của Spring Security
        // LƯU Ý: Phải truyền user.getEmail() vào đây để SecurityConfig lấy đúng Email xử lý Session
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(role))
        );
    }
}