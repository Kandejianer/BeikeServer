package profile;

import constant.Constant;
import db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "GetPhoto", urlPatterns = "/Profile/GetPhoto")
public class GetPhoto extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // get param
        String account = request.getParameter("account");
        String id = request.getParameter("id");

        // 从数据库获取图片在服务器上的存储路径
        String realPath = getRealPathFromDatabase(account,id);

        // 输出到客户端
        downloadFileByOutputStream(realPath, response);
    }

    /**
     * 获取图片路径
     *
     */
    private String getRealPathFromDatabase(String account, String id) {

        String tableName = "";
        String realPath = "";

        switch (id) {
            case Constant.ID_TEACHER:
                tableName = Constant.TABLE_TEACHER;
                break;
            case Constant.ID_STUDENT:
                tableName = Constant.TABLE_STUDENT;
                break;
            case Constant.ID_PARENT:
                tableName = Constant.TABLE_PARENT;
                break;
        }

        try {
            Connection connect = DatabaseUtil.getConnection();
            Statement statement = connect.createStatement();

            // assemble sqlQuery
            String sql = "select ImagePath from " + tableName + " where Account='" + account + "'";

            // execute
            ResultSet result = statement.executeQuery(sql);

            if (result.next()) { // 获取列表成功
                realPath = result.getString("ImagePath");
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return realPath;
    }

    /**
     * 写出图片Output流到客户端
     *
     */
    private void downloadFileByOutputStream(String realPath, HttpServletResponse response)
            throws IOException {
        // 1.获取要下载的文件的绝对路径

        // 2.获取要下载的文件名
        String fileName = realPath.substring(realPath.lastIndexOf("\\") + 1);
        // 3.设置content-disposition响应头控制浏览器以下载的形式打开文件
        response.setHeader("content-disposition", "attachment;filename=" + fileName);
        // 4.获取要下载的文件输入流
        InputStream in = new FileInputStream(realPath);
        int len = 0;
        // 5.创建数据缓冲区
        byte[] buffer = new byte[1024];
        // 6.通过response对象获取OutputStream流
        OutputStream out = response.getOutputStream();
        // 7.将FileInputStream流写入到buffer缓冲区
        while ((len = in.read(buffer)) > 0) {
            // 8.使用OutputStream将缓冲区的数据输出到客户端浏览器
            out.write(buffer, 0, len);
        }

        in.close();
    }
}
