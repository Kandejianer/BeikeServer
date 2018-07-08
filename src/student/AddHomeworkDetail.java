package student;

import constant.Constant;
import db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


@WebServlet(name = "AddHomeworkDetail", urlPatterns = "/Student/AddHomeworkDetail")
public class AddHomeworkDetail {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取参数
        String hwId = request.getParameter("hwId");
        String subject = request.getParameter("subject");
        String optionA = request.getParameter("optionA");
        String optionB = request.getParameter("optionB");
        String optionC = request.getParameter("optionC");
        String optionD = request.getParameter("optionD");
        String key = request.getParameter("key");


        // sql插入语句
        String sqlInsert = null;
        // 响应
        String res;

        sqlInsert = "insert into " + Constant.TABLE_HOMEWORK_DETAIL_STUDENT
                + "(hwID,subject,optionA,optionB,optionC,optionD,key) "
                + "values('" + hwId + "','" + subject + "','" + optionA + "','" + optionB + "','" + optionC + "','" + optionD + "','" + key + "')";

        // 执行插入
        res = add(sqlInsert);

        response.getWriter().append(res);
    }

    /**
     * 作业细节入库模块
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
