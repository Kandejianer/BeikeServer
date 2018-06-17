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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "GetInfo", urlPatterns = "/Profile/GetInfo")
public class GetInfo extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // 获取参数
        String id = request.getParameter("id");
        String account = request.getParameter("account");

        // 响应数组
        String[] responseArray;
        // 表名
        String tableName;
        // 最终响应结果
        String finalResponse = "";

        // 判断身份
        switch (id) {
            case Constant.ID_TEACHER:
                //表名
                tableName = Constant.TABLE_TEACHER;
                //获取数据
                responseArray = getInfoAsTeacherOrStudent(account, tableName);

                //response的数据格式:标志位/头像url/name/gender/school/classes
                finalResponse = responseArray[0] + "/" + responseArray[1] + "/" + responseArray[2]
                        + "/" + responseArray[3] + "/" + responseArray[4] + "/" + responseArray[5];
                break;
            case Constant.ID_STUDENT:
                //表名
                tableName = Constant.TABLE_STUDENT;
                //获取数据
                responseArray = getInfoAsTeacherOrStudent(account, tableName);

                //response的数据格式:标志位/头像url/name/gender/school/classes
                finalResponse = responseArray[0] + "/" + responseArray[1] + "/" + responseArray[2]
                        + "/" + responseArray[3] + "/" + responseArray[4] + "/" + responseArray[5];
                break;
            case Constant.ID_PARENT:
                //获取数据
                responseArray = getInfoAsParent(account);

                //response的数据格式:标志位/头像url/name/gender
                finalResponse = responseArray[0] + "/" + responseArray[1] + "/" + responseArray[2]
                        + "/" + responseArray[3];
                break;
        }
        response.getWriter().append(finalResponse);
    }

    /**
     * 非家长身份获取信息
     *
     */
    private String[] getInfoAsTeacherOrStudent(String account,String tableName) {
        String[] responseArray = new String[6];
        responseArray[0] = Constant.FLAG_FAILURE;
        try {
            Connection connect = DatabaseUtil.getConnection();
            Statement statement =connect.createStatement();

            //assemble sqlQuery
            String sql = "select Name,Sex,School,Class from " + tableName
                    + " where Account='" + account + "'";

            //execute
            ResultSet result = statement.executeQuery(sql);

            if(result.next()){ //获取列表成功
                responseArray[0] = Constant.FLAG_SUCCESS;
                responseArray[1] = null;
                responseArray[2] = result.getString("Name");
                responseArray[3] = result.getString("Sex");
                responseArray[4] = result.getString("School");
                responseArray[5] = result.getString("Class");
            }

        }catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return responseArray;
    }

    /**
     * 家长身份获取信息
     *
     */
    private String[] getInfoAsParent(String account) {
        String[] responseArray = new String[4];
        responseArray[0] = Constant.FLAG_FAILURE;

        try {
            Connection connect =  DatabaseUtil.getConnection();
            Statement statement = connect.createStatement();

            //assemble sqlQuery
            String sql = "select Name,Sex from " + Constant.TABLE_PARENT
                    + " where Account='" + account + "'";

            //execute
            ResultSet result = statement.executeQuery(sql);

            if(result.next()){ //获取列表成功
                responseArray[0] = Constant.FLAG_SUCCESS;
                responseArray[1] = null;
                responseArray[2] = result.getString("Name");
                responseArray[3] = result.getString("Sex");
            }

        }catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return responseArray;
    }
}
