package teacher;

import constant.Constant;
import db.DatabaseUtil;
import org.apache.commons.lang3.StringUtils;

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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "GetHomeworkStatus", urlPatterns = "/Teacher/GetHomeworkStatus")
public class GetHomeworkStatus extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置编码
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        // get param
        String account = request.getParameter("account");


//        ArrayList<String> hwIdList = new ArrayList<>();
//
//        // set hwId to ArrayList
//        if (hwIds.contains(",")) {
//            String[] hwIdArr = hwIds.split(",");
//            Collections.addAll(hwIdList, hwIdArr);
//        } else {
//            hwIdList.add(hwIds);
//        }

        // 完成响应
        response.getWriter().append(queryFromDatabase(account));
    }

    /**
     * 查询提交情况并完成计算和响应字符串的组装
     *
     * @param account
     * @return 响应字符串
     */
    private String queryFromDatabase(String account) {

        String flag = Constant.FLAG_FAILURE;

        List<String> hwIdList = new ArrayList<>();

        List<String> subResList = new ArrayList<>();
        String subRes;
        try {
            Connection connect = DatabaseUtil.getConnection();
            Statement statement = connect.createStatement();

            /*************************** 获取hwIds *******************************/
            String queryHwId = "select hwId from " + Constant.TABLE_TEACHER
                    + " where Account='" + account + "'";
            ResultSet rst = statement.executeQuery(queryHwId);
            String hwId = null;
            String hwIdStr = null;
            if (rst.next()) {
                hwIdStr = rst.getString("hwId");
            }
            if (hwIdStr != null) {
                if ("".equals(hwIdStr) || hwIdStr.isEmpty()) {
                    return "null";
                } else {
                    if (hwIdStr.startsWith(",")) {
                        hwId = hwIdStr.substring(1);
                    } else {
                        return null;
                    }
                }
            } else {
                return "null";
            }

            String str[] = hwId.split(",");
            Collections.addAll(hwIdList, str);
            // 倒序一下hwIdList是为了让最后添加的作业最被查询
            // 这样在客户端就会将最后添加的作业显示在最前面
            // 增强体验吧~~
            Collections.reverse(hwIdList);

            for (String hId : hwIdList) {

                // 先查询hwId对应的title和size
                String queryTitle = "select * from " + Constant.TABLE_HOMEWORK_STUDENT
                        + " where hwId='" + hId + "'";
                ResultSet rs = statement.executeQuery(queryTitle);
                // 获取到title
                String title = null;
                String size = null;
                if (rs.next()) {
                    title = rs.getString("title");
                    size = rs.getString("size");
                }

                // errorCount下标为题号，值为该题的错误人数
                // 如:errorCount[1]表示：第一题错误人数为errorCount[1]人
                // errorCount[0]无意义（可能有值，但仍然无意义）
                int[] errorCount = new int[Integer.parseInt(size) + 1];
                List<Double> errorRateList = new ArrayList<>();

                ResultSet tempResult;
                String sqlQuery = "select * from " + Constant.TABLE_HOMEWORK_RESULT
                        + " where hwId='" + hId + "'";

                tempResult = statement.executeQuery(sqlQuery);
                // 统计提交作业的人数
                int rowCount = 0;
                while (tempResult.next()) {
                    rowCount++;

                    String errStr = tempResult.getString("err");
                    if (errStr.contains(",")) {
                        String[] errArray = errStr.split(",");
                        for (String s : errArray) {
                            errorCount[Integer.parseInt(s)]++;
                        }

                    } else {
                        errorCount[Integer.parseInt(errStr)]++;
                    }
                }

                // 计算错误率
                for (int ec : errorCount) {
                    // 两个整数相除不能直接赋值给double类型变量，会为0
                    // 因此乘一个1.0再做除法
                    double d = ec * 1.0 / rowCount;
                    errorRateList.add(d);
                }



                subRes = hId + "$" + title + "$" + rowCount + "$" + StringUtils.join(errorRateList, ",");
                subResList.add(subRes);
            }
            flag = Constant.FLAG_SUCCESS;

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        return flag + "#" + StringUtils.join(subResList,"#");
        //return flag + "#" + hwIdList.toString() + "%" + hwIdList.size();
    }
}
