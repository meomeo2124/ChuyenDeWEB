package controller.web;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import dao.DBConnectionPool;
import dao.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import models.User;

@WebServlet("/secure/edit")
public class EditUserProfileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	// GET: Hiển thị trang chỉnh sửa user
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		request.setAttribute("user", user);
		request.getRequestDispatcher("/secure/editProfile.jsp").forward(request, response);
	}

	// POST: Cập nhật thông tin user
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		User user = (User) session.getAttribute("user");

		if (user == null) {
			response.sendRedirect(request.getContextPath() + "/login");
			return;
		}

		try (Connection connection = DBConnectionPool.getDataSource().getConnection()) {
			UserDAO userDAO = new UserDAO(connection);

			String name = request.getParameter("username");
			String email = request.getParameter("email");
			String phoneStr = request.getParameter("phone");
			String address = request.getParameter("address");

			int phone = Integer.parseInt(phoneStr);

			boolean isUpdated = userDAO.editProfile(user, name, email, phone, address);

			if (isUpdated) {
				session.setAttribute("user", userDAO.findById(user.getId()));
				response.sendRedirect(request.getContextPath() + "/Homepage");
			} else {
				response.sendRedirect(request.getContextPath() + "/secure/edit?error=Cập nhật thất bại");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			response.sendRedirect(request.getContextPath() + "/error.jsp");
		}
	}
}