package controller.admin.management.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import dao.DBConnectionPool;
import dao.UserDAO;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.User;

@WebServlet("/admin/EditUserServlet")
public class EditUserServlet extends HttpServlet {

    // GET: Lấy thông tin user theo ID để hiển thị form chỉnh sửa
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = -1;
        try {
            id = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ.");
            return;
        }

        try (Connection connection = DBConnectionPool.getConnection()) {
            UserDAO userDAO = new UserDAO(connection);
            User user = userDAO.getUser(id);
            if (user != null) {
                request.setAttribute("user", user);
                RequestDispatcher dispatcher = request.getRequestDispatcher("/admin/edit_user.jsp");
                dispatcher.forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Người dùng không tìm thấy.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi kết nối cơ sở dữ liệu.");
        }
    }

    // POST: Cập nhật thông tin user
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int id = -1; // Khởi tạo giá trị mặc định
        try {
            id = Integer.parseInt(request.getParameter("user_id")); // Đọc ID từ form
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID không hợp lệ.");
            return;
        }

        // Lấy dữ liệu từ form
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String phoneStr = request.getParameter("phone");
        String address = request.getParameter("address");

        // Kiểm tra dữ liệu không được null hoặc rỗng
        if (username == null || username.trim().isEmpty() ||
                email == null || email.trim().isEmpty() ||
                phoneStr == null || phoneStr.trim().isEmpty() ||
                address == null || address.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Dữ liệu không hợp lệ hoặc thiếu.");
            return;
        }

        int phone = 0;
        try {
            phone = Integer.parseInt(phoneStr);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Số điện thoại không hợp lệ.");
            return;
        }

        try (Connection connection = DBConnectionPool.getConnection()) {
            UserDAO userDAO = new UserDAO(connection);
            User user = userDAO.getUser(id);
            if (user != null) {
                // Cập nhật thông tin user, giả sử editProfile có cú pháp như vậy
                boolean isUpdated = userDAO.editProfile(user, username, email, phone, address);
                if (isUpdated) {
                    // Chuyển hướng về trang quản lý người dùng (đường dẫn chính xác tuỳ dự án)
                    response.sendRedirect(request.getContextPath() + "/admin/ManageUserServlet");
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Cập nhật thất bại.");
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Người dùng không tìm thấy.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi truy vấn dữ liệu.");
        }
    }
}
