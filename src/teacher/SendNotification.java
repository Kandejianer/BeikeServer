package teacher;

import com.xiaomi.push.sdk.ErrorCode;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "SendNotification", urlPatterns = "/Teacher/SendNotification")
public class SendNotification extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        //get params
        String account = request.getParameter("account");
        String title = request.getParameter("title");
        String content = request.getParameter("content");
        String userAccountStr = request.getParameter("to");
        ArrayList<String> userAl = new ArrayList<>();
        String name = null;

        // 获取老师名字
        try {
            Connection connect = DatabaseUtil.getConnection();
            Statement statement = connect.createStatement();
            ResultSet result;

            String sqlQuery = "select Name from " + Constant.TABLE_TEACHER
                    + " where Account='" + account + "'";

            result = statement.executeQuery(sqlQuery);
            if (result.next()) {
                name = result.getString("Name");
            } else {
                return;
            }

        } catch (SQLException | ClassNotFoundException e1) {
            e1.printStackTrace();
        }

        // 设置接受用户
        if (userAccountStr.contains(",")) {
            String[] userAccountArr = userAccountStr.split(",");
            Collections.addAll(userAl, userAccountArr);
        } else {
            userAl.add(userAccountStr);
        }

        // 发送结果
        String res = null;
        try {
            // 发送通知
            res = sendMessageToUserAccounts(name, title, content, userAl);
        } catch (Exception e) {
            e.printStackTrace();
        }


        PrintWriter pw = response.getWriter();
        pw.write(res);
    }

    /**
     * 发送通知给指定UserAccounts
     * @param name 老师的名字
     * @param title 通知名称
     * @param content 通知内容
     * @param userAccountList 接受通知对象列表
     * @return
     * @throws Exception
     */
    private String sendMessageToUserAccounts(String name, String title, String content, List<String> userAccountList) throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender("dQrTRKGpbYyOkJXi13sfIA==");
        String description = name + "老师向您发送了通知";
        Message message = new Message.Builder()
                .title(title)
                .description(description)
                .payload(content)
                .passThrough(0)
                .restrictedPackageName("com.example.beikeapp")
                .notifyType(1)     // 使用默认提示音提示
                .build();

        Result result = sender.sendToUserAccount(message, userAccountList, 0); //根据userAccountList，发送消息到指定设备上，不重试
        if (result.getErrorCode().equals(ErrorCode.Success)) {
            return Constant.FLAG_SUCCESS;
        }
        return Constant.FLAG_FAILURE;
    }
}
