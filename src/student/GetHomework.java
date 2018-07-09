package student;

import constant.Constant;
import db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "GetHomework", urlPatterns = "/Student/GetHomework")
public class GetHomework {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取参数
        String classId = request.getParameter("classId");

        response.getWriter().append(getNotify(classId));
    }

    /**
     * 获取通知信息
     */
    private String getNotify(String classId) {

        StringBuilder sb = new StringBuilder();
        try {
            Connection connect = DatabaseUtil.getConnection();
            Statement statement = connect.createStatement();

            //assemble sqlQuery
            String sql = "select * from " + Constant.TABLE_HOMEWORK_STUDENT
                    + " where classId='" + classId + "'";
            //execute
            ResultSet result = statement.executeQuery(sql);

            //初始化变量
            String title, name, time, size, hwId;

            while(result.next()){

                title = result.getString("title");
                name = result.getString("name");
                time = result.getString("time");
                size = result.getString("size");
                hwId = result.getString("hwId");

                sb.append("<homework>")
                        .append("<title>").append(title).append("</title>")
                        .append("<name>").append(name).append("</name>")
                        .append("<time>").append(time).append("</time>")
                        .append("<name>").append(name).append("</name>")
                        .append("<size>").append(size).append("</size>")
                        .append("<hwId>").append(hwId).append("</hwId>")
                   .append("</homework>");

            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

}
