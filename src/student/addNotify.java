package student;


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

@WebServlet(name = "addNotify", urlPatterns = "/Student/AddNotify")
public class addNotify extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取参数
        String stuId = request.getParameter("stuId");
        String title = request.getParameter("title");
        String name = request.getParameter("name");
        String content = request.getParameter("content");
        String time = request.getParameter("time");

        // sql插入语句
        String sqlInsert = null;
        // 响应
        String res;

        sqlInsert = "insert into " + Constant.TABLE_NOTIFY_STUDENT
                + "(stuId,title,name,content,time) "
                + "values('" + stuId + "','" + title + "','" + name + "','" + content + "','" + time + "')";

        // 执行注册
        res = add(sqlInsert);

        response.getWriter().append(res);
    }

    /**
     * 通知入库模块
     *
     * @param sqlInsert
     * @return
     */
    private String add(String sqlInsert) {
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
