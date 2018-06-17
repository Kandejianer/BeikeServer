package login;

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

@WebServlet(name = "Login", urlPatterns = "/Login")
public class Login extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取参数
        String id = request.getParameter("id");
        String account = request.getParameter("account");
        String password = request.getParameter("password");

        // 表名
        String tableName = null;
        switch (id) {
            case Constant.ID_TEACHER: {
                tableName = Constant.TABLE_TEACHER;
                break;
            }
            case Constant.ID_STUDENT: {
                tableName = Constant.TABLE_STUDENT;
                break;
            }
            case Constant.ID_PARENT: {
                tableName = Constant.TABLE_PARENT;
                break;
            }
        }

        // 响应
        String res = null;
        try {
            // 登陆
            res = login(account, password, tableName);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        response.getWriter().append(res);
    }

    /**
     * 登陆模块
     */
    private String login(String account, String password, String tableName) throws SQLException, ClassNotFoundException {
        Connection connect = DatabaseUtil.getConnection();
        String code = Constant.FLAG_FAILURE;

        Statement statement = connect.createStatement();

        String sqlQuery = "select * from " + tableName
                + " where Account='" + account + "'" + "AND Password='" + password + "'";

        ResultSet result = statement.executeQuery(sqlQuery);

        if (result.next()) {
            code = Constant.FLAG_SUCCESS;
        }
        return code;
    }
}
