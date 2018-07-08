package register;

import constant.Constant;
import db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "TestExistence", urlPatterns = "/Register/TestExistence")
public class TestExistence extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取字段
        String id = request.getParameter("id");
        String account = request.getParameter("account");

        // 表名
        String tableName = null;

        // 判断身份
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
            }
        }

        // 查重
        String resMsg = testExistence(account, tableName);

        PrintWriter pw = response.getWriter();
        pw.write(resMsg);
    }

    /**
     * 数据库账号查重
     * @param account 账号
     * @param tableName 表名
     * @return 查重结果
     */
    private String testExistence(String account, String tableName) {

        String res = Constant.FLAG_NO;
        try {
            Connection connect = DatabaseUtil.getConnection();
            Statement statement = connect.createStatement();

            String sqlQuery = "select * from " + tableName + " where Account='" + account + "'";
            ResultSet result = statement.executeQuery(sqlQuery);

            if (result.next()) { // 若存在，则该账号已被注册
                res = Constant.FLAG_YES;
            }
            connect.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }
}
