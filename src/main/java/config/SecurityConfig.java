package config;

import dao.UserDAO;
import models.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // Đổi sang BCrypt
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDAO userDAO;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Giữ nguyên disable và chuẩn bị câu trả lời vấn đáp
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasRole("ADMIN")
                        .requestMatchers(new AntPathRequestMatcher("/secure/**")).hasAnyRole("USER", "ADMIN")
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/doLogin")
                        .successHandler((HttpServletRequest request, HttpServletResponse response, Authentication authentication) -> {
                            String username = authentication.getName();
                            User user = userDAO.findByEmail(username);

                            // Phòng chống lỗi NullPointerException nếu không tìm thấy User
                            if (user != null) {
                                HttpSession session = request.getSession();
                                session.setAttribute("user", user);
                                session.setAttribute("userId", user.getId());
                                session.setAttribute("img", user.getImg());

                                // Phân hướng trang sau khi login dựa vào vai trò
                                if (user.getIsAdmin()) {
                                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                                } else {
                                    response.sendRedirect(request.getContextPath() + "/");
                                }
                            } else {
                                response.sendRedirect(request.getContextPath() + "/login?error=true");
                            }
                        })
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}