package register;

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

@WebServlet(name = "Register", urlPatterns = "/Register")
public class Register extends HttpServlet {
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
        String name = request.getParameter("name");
        String gender = request.getParameter("gender");

        // sql插入语句
        String sqlInsert = null;
        // 响应
        String res;

        switch (id) {
            case Constant.ID_TEACHER: {
                // 老师身份独有字段
                String school = request.getParameter("school");
                String classes = request.getParameter("classes");
                sqlInsert = "insert into " + Constant.TABLE_TEACHER
                        + "(Account,Password,Name,Sex,School,Class) "
                        + "values('" + account + "','" + password + "','" + name + "','" + gender + "','" + school + "','" + classes + "')";
                break;
            }
            case Constant.ID_STUDENT: {
                sqlInsert = "insert into " + Constant.TABLE_STUDENT
                        + "(Account,Password,Name,Sex) "
                        + "values('" + account + "','" + password + "','" + name + "','" + gender + "')";
                break;
            }
            case Constant.ID_PARENT: {
                sqlInsert = "insert into " + Constant.TABLE_PARENT
                        + "(Account,Password,Name,Sex) "
                        + "values('" + account + "','" + password + "','" + name + "','" + gender + "')";
                break;
            }
        }
        // 执行注册
        res = register(sqlInsert);

        response.getWriter().append(res);
    }

    /**
     * 注册模块
     *
     * @param sqlInsert
     * @return
     */
    private String register(String sqlInsert) {
        String res = Constant.FLAG_FAILURE;
        try {
            Connection connect = DatabaseUtil.getConnection();
            Statement statement = connect.createStatement();
            int row1 = statement.executeUpdate(sqlInsert);

            if (row1 == 1) {
                res = Constant.FLAG_SUCCESS;
            }
            connect.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }
}
