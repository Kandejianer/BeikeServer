package register.parent;

import constant.Constant;
import db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "CheckCode",urlPatterns = "/Register/Parent/CheckCode")
public class CheckCode extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        // 获取字段
        String stuId = request.getParameter("stuId");
        // 响应码
        String code = Constant.FLAG_FAILURE;

        Connection connect = null;
        try {
            connect = DatabaseUtil.getConnection();

            Statement statement = connect.createStatement();
            String sql = "select * from " + Constant.TABLE_STUDENT + " where Account='" + stuId + "'";
            ResultSet result = statement.executeQuery(sql);

            if (result.next()) { // 能查到班级，则邀请码有效
                //邀请码有效
                code = Constant.FLAG_SUCCESS;
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                assert connect != null;
                connect.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        response.getWriter().append(code);
    }
}
