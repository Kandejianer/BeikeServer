package profile;

import constant.Constant;
import db.DatabaseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "UploadPhoto", urlPatterns = "/Profile/UploadPhoto")

@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10,      // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MB

public class UploadPhoto extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        String finalResponse;

        String account = request.getParameter("account");
        String id = request.getParameter("id");

        final String SAVE_DIR = "uploadFiles";
        // 最终的图片存储路径,进数据库
        String finalPath = "";
        // 获取绝对路径
        String appPath = request.getServletContext().getRealPath("");
        // 构建一个路径用于存放文件
        String savePath = appPath + SAVE_DIR;

        // creates the save directory if it does not exists
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdir();
        }

        for (Part part : request.getParts()) {
            String fileName = extractFileName(part);
            // refines the fileName in case it is an absolute path
            fileName = new File(fileName).getName();
            finalPath = savePath + File.separator + fileName;
            part.write(finalPath);
        }

        finalResponse = savePath2Database(id, account, finalPath);

        response.getWriter().append(finalResponse);

    }

    /**
     * 保存图片路径至数据库
     *
     * @param id
     * @param account
     * @param finalPath
     * @return
     */
    private String savePath2Database(String id, String account, String finalPath) {
        String response = Constant.FLAG_FAILURE;

        String tableName = "";

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

            finalPath = finalPath.replace("\\", "\\\\");

            String sql = "update " + tableName
                    + " set ImagePath='" + finalPath + "'"
                    + " where Account='" + account + "'";
            int row1 = statement.executeUpdate(sql);
            if (row1 == 1) {  //success
                response = Constant.FLAG_SUCCESS;
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return response;

    }

    /**
     * 提取文件名
     *
     * @param part 负载在request中的文件
     * @return
     */
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
