package teacher;

import com.xiaomi.push.sdk.ErrorCode;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;
import constant.Constant;
import db.DatabaseUtil;
import org.apache.commons.lang3.StringUtils;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@WebServlet(name = "SendHomework", urlPatterns = "/Teacher/SendHomework")
public class SendHomework extends HttpServlet {

    // 老师名字
    private String name;

    private String title = "您有新的作业";

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        // get params
        String account = request.getParameter("account");
        String userAccountStr = request.getParameter("to");
        String size = request.getParameter("size");
        String[] subjects = request.getParameterValues("subject");
        String[] optionAs = request.getParameterValues("optionA");
        String[] optionBs = request.getParameterValues("optionB");
        String[] optionCs = request.getParameterValues("optionC");
        String[] optionDs = request.getParameterValues("optionD");
        String[] keys = request.getParameterValues("key");

        ArrayList<String> userAl = new ArrayList<>();

        // 获取老师名字赋值给name
        name = getTeacherName(account);

        // set receivers
        if (userAccountStr.contains(",")) {
            String[] userAccountArr = userAccountStr.split(",");
            Collections.addAll(userAl, userAccountArr);
        } else {
            userAl.add(userAccountStr);
        }

        // setup homeworkList
        for (int i = 0; i < Integer.parseInt(size); i++) {
            Homework hw = new Homework(subjects[i], optionAs[i], optionBs[i], optionCs[i], optionDs[i], keys[i]);
            Homework.homeworkList.add(hw);
        }
        // save homework to database for further use
        saveHomework2Database(userAl, size);

        // 待发送的作业
        String mainBody = assembleMainBody(size);
        // 由于homeworkList是静态变量，因此每次用完必须清空
        // 否则作业会堆积
        Homework.homeworkList.clear();


        // 执行发送
        String res = null;
        try {
            res = sendHomeworkToUserAccounts(name, mainBody, userAl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PrintWriter pw = response.getWriter();
        pw.write(res);
    }

    /**
     * 作业入库
     */
    private void saveHomework2Database(ArrayList<String> classIdAl, String size) {

        int hwId;

        try {
            Connection connect = DatabaseUtil.getConnection();
            Statement statement = connect.createStatement();

            ResultSet tempResult;
            do {
                // create one-and-only hwId if it has not been created before
                hwId = (int) (Math.random() * 900 + 100);
                String tempSql = "select * from " + Constant.TABLE_HOMEWORK_DETAIL_STUDENT + " where hwId='" + hwId + "'";
                tempResult = statement.executeQuery(tempSql);
            } while (tempResult.next());
            // 存入homework_detail_student表
            // 该表用于存放具体作业内容
            for (Homework hw : Homework.homeworkList) {
                String sqlInsert = "insert into " + Constant.TABLE_HOMEWORK_DETAIL_STUDENT
                        + "(hwId,hw_subject,optionA,optionB,optionC,optionD,hw_key) "
                        + "values('" + hwId + "','" + hw.subject + "','"
                        + hw.optionA + "','"
                        + hw.optionB + "','"
                        + hw.optionC + "','"
                        + hw.optionD + "','"
                        + hw.key + "')";

                int row1 = statement.executeUpdate(sqlInsert);
                if (row1 == 1) {
                    System.out.println("单条作业存储成功");
                }
            }

            // 时间
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 存入homework_student表
            // 该表用于存放作业基本信息
            for (String aClassIdAl : classIdAl) {

                String sqlInsert2Another = "insert into " + Constant.TABLE_HOMEWORK_STUDENT
                        + "(classId,title,name,time,size,hwId) "
                        + "values('"
                        + aClassIdAl + "','"
                        + title + "','"
                        + name + "','"
                        + df.format(new Date()) + "','"
                        + Integer.parseInt(size) + "','"
                        + hwId + "')";
                int row2 = statement.executeUpdate(sqlInsert2Another);
                if (row2 == 1) {
                    System.out.println("作业基本信息存储成功");

                }
            }

            connect.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 组装作业内容
     *
     * @param size 作业量
     * @return 组装好的作业内容
     */
    private String assembleMainBody(String size) {
        // 时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String[] arr = new String[Homework.homeworkList.size()];
        int i = 0;
        for (Homework hw : Homework.homeworkList) {
            arr[i++] = hw.getHomeworkItem();
        }
        return "<category>homework</category>" //通知类型
                + "<title>" + title + "</title>" //标题
                + "<name>" + name + "</name>" //老师名字
                + "<time>" + df.format(new Date()) + "</time>" //发送时间
                + "<size>" + size + "</size>" //作业条数
                + StringUtils.join(arr); //作业内容

    }


    /**
     * 获取老师名字，以显示在通知栏description中
     */
    private String getTeacherName(String account) {
        String name = null;
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
                return "XXX";
            }

        } catch (SQLException | ClassNotFoundException e1) {
            e1.printStackTrace();
        }
        return name;
    }

    /**
     * 发送作业给指定用户
     */
    private String sendHomeworkToUserAccounts(String name, String content, List<String> userAccountList) throws Exception {
        Constants.useOfficial();
        Sender sender = new Sender("dQrTRKGpbYyOkJXi13sfIA==");
        String description = name + "老师布置了新的作业！";

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

    /**
     * 作业条目
     */
    static class Homework {
        /**
         * homework类的数组，用于存放全体作业项
         */
        static List<Homework> homeworkList = new ArrayList<>();

        /**
         * 作业题目
         */
        private String subject;
        /**
         * 选项A-D
         */
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        /**
         * 答案
         */
        private String key;

        /**
         * 构造方法
         */
        Homework(String subject, String optionA, String optionB, String optionC, String optionD, String key) {
            this.subject = subject;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.key = key;
        }

        String getHomeworkItem() {
            return "<hw>"
                    + "<subject>" + subject + "</subject>"
                    + "<optionA>" + optionA + "</optionA>"
                    + "<optionB>" + optionB + "</optionB>"
                    + "<optionC>" + optionC + "</optionC>"
                    + "<optionD>" + optionD + "</optionD>"
                    + "<key>" + key + "</key>"
                    + "</hw>";
        }

    }

}
