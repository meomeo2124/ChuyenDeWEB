
package controller.web;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.Cart;
import models.User;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

import dao.CartDAO;
import dao.DBConnectionPool;
import dao.UserDAO;

/**
 * Servlet implementation class login
 */
@WebServlet("/login")
public class login extends HttpServlet {
	private static final long serialVersionUID = 1L;


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
			UserDAO userDAO = new UserDAO(connection);
			HttpSession session = req.getSession();

			String idTokenString = req.getParameter("credential"); //Nhận token từ Google Identity Services
			String email = req.getParameter("email");
			String pass = req.getParameter("password");

			if (idTokenString != null && !idTokenString.isEmpty()) {
				//Xác thực token Google
				GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
						.setAudience(Collections.singletonList("564628514231-g4733rfvad9m98vffpn5iofj3ht90u1t.apps.googleusercontent.com"))
						.build();

				GoogleIdToken idToken = verifier.verify(idTokenString);
				if (idToken != null) {
					GoogleIdToken.Payload payload = idToken.getPayload();
					String googleId = payload.getSubject();
					String googleEmail = payload.getEmail();
					String name = (String) payload.get("name");
					String img = (String) payload.get("picture");

					//Kiểm tra user trong database
					User user = userDAO.findByEmail(googleEmail);
					if (user == null) {
						// Nếu chưa có user Google, tạo mới
						user = new User();
						user.setGoogleId(googleId);
						user.setEmail(googleEmail);
						user.setName(name);
						user.setImg(img);
						userDAO.insertUser(user);
					} else if (user.getGoogleId() == null) {
						// Nếu user có email nhưng chưa có Google ID, cập nhật
						user.setGoogleId(googleId);
						userDAO.updateUserGoogleId(user);
					}


					//Lưu vào session
					session.setAttribute("user", user);
					session.setAttribute("userId", user.getId());
					session.setAttribute("img", user.getImg());

					//Chuyển hướng đến homepage
					resp.sendRedirect(req.getContextPath() + "/Homepage");
					return;
				} else {
					resp.sendRedirect(req.getContextPath() + "/login?error=google_token_invalid");
					return;
				}
			} else if (email != null && pass != null) {
				//Xử lý đăng nhập bằng email/mật khẩu
				User user = userDAO.getLogin(email, pass);
				if (user == null) {
					req.setAttribute("message", "Sai thông tin tài khoản mật khẩu");
					req.getRequestDispatcher("/Login.jsp").forward(req, resp);
				} else {
					// 🔹 Lưu vào session
					session.setAttribute("user", user);
					session.setAttribute("userId", user.getId());
					session.setAttribute("img", user.getImg());

					if (user.isAdmin()) {
						resp.sendRedirect(req.getContextPath() + "/admin/dashboard.jsp");
					} else {
						resp.sendRedirect(req.getContextPath() + "/Homepage");
					}
				}
			} else {
				resp.sendRedirect(req.getContextPath() + "/login?error=missing_credentials");
			}
		} catch (Exception e) {
			throw new ServletException("Error processing login", e);
		}
	}
}