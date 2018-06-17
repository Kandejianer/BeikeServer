package profile;

import constant.Constant;
import db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "ChangeInfo", urlPatterns = "/Profile/ChangeInfo")
public class ChangeInfo extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取参数
        String id = request.getParameter("id");
        String account = request.getParameter("account");
        String columnName = request.getParameter("columnName");
        String data = request.getParameter("data");

        // 响应
        String code = "";

        // 表名
        String tableName = "";
        switch (id) {
            case Constant.ID_TEACHER:
                tableName = Constant.TABLE_TEACHER;
                break;

            case Constant.ID_STUDENT:
                tableName = Constant.TABLE_STUDENT;
                break;

            case Constant.ID_PARENT:
                tableName = Constant.TABLE_PARENT;
                break;
        }

        try {
            Connection connect = DatabaseUtil.getConnection();
            Statement statement = connect.createStatement();

            String sqlUpdate = "update " + tableName
                    + " set " + columnName + "='" + data + "'"
                    + " where Account='" + account + "'";
            int row = statement.executeUpdate(sqlUpdate);

            if (row == 1) {
                code = Constant.FLAG_SUCCESS;
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        response.getWriter().append(code);
    }
}
