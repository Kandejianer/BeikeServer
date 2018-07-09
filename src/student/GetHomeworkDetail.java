package student;

import constant.Constant;
import db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.HTMLWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "GetHomeDetail", urlPatterns = "/Student/GetHomeworkDetail")
public class GetHomeworkDetail extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取参数
        String hwId = request.getParameter("hwId");

        response.getWriter().append(getNotify(hwId));
    }

    /**
     * 获取通知信息
     */
    private String getNotify(String hwId) {

        StringBuilder sb = new StringBuilder();
        try {
            Connection connect = DatabaseUtil.getConnection();
            Statement statement = connect.createStatement();

            //assemble sqlQuery
            String sql = "select * from " + Constant.TABLE_HOMEWORK_DETAIL_STUDENT
                    + " where hwId='" + hwId + "'";
            //execute
            ResultSet result = statement.executeQuery(sql);

            //初始化变量
            String hw_subject, optionA, optionB, optionC, optionD, hw_key;

            while (result.next()) {

                hw_subject = result.getString("hw_subject");
                optionA = result.getString("optionA");
                optionB = result.getString("optionB");
                optionC = result.getString("optionC");
                optionD = result.getString("optionD");
                hw_key = result.getString("hw_key");

                sb.append("<hw>")
                        .append("<hw_subject>").append(hw_subject).append("</hw_subject>")
                        .append("<optionA>").append(optionA).append("</optionA>")
                        .append("<optionB>").append(optionB).append("</optionB>")
                        .append("<optionA>").append(optionA).append("</optionA>")
                        .append("<optionC>").append(optionC).append("</optionC>")
                        .append("<optionD>").append(optionD).append("</optionD>")
                        .append("<hw_key>").append(hw_key).append("</hw_key>")
                    .append("</hw>");

            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
