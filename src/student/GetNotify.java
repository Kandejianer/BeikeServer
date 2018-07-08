package student;


import constant.Constant;
import db.DatabaseUtil;
import teacher.SendHomework;

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
import java.util.List;

@WebServlet(name = "GetNotify", urlPatterns = "/Student/GetNotify")
public class GetNotify extends HttpServlet {
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
            String sql = "select * from " + Constant.TABLE_NOTIFY_STUDENT
                    + " where classId='" + classId + "'";
            //execute
            ResultSet result = statement.executeQuery(sql);
            while(result.next()){

              String notification = result.getString("notification");
              sb.append("<notification>").append(notification).append("</notification>");

            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    /**
     * 作业条目
     */
 /*   static class Notification {
        *//**
         * NotificationList，用于存放全体通知项
         *//*
        static List<Notification> notificationList = new ArrayList<>();

        *//**
         * 学生ID
         *//*
        private String stuId;
        *//**
         * 通知标题，老师名字，通知内容，通知发送的时间
         *//*
        private String title;
        private String name;
        private String content;
        private String time;
        *//**
         * 是否已读
         *//*
        private String isReaded;

        *//**
         * 构造方法
         *//*
        Notification(String stuId, String title, String name, String content, String time, String isReaded) {
            this.stuId = stuId;
            this.title = title;
            this.name = name;
            this.content = content;
            this.time = time;
            this.isReaded = isReaded;
        }

        *//**
         * 获取本条通知
         *//*
        String getNotificationItem() {
            return "<nf>"
                    + "<stuId>" + stuId + "</stuId>"
                    + "<title>" + title + "</title>"
                    + "<name>" + name + "</name>"
                    + "<content>" + content + "</content>"
                    + "<time>" + time + "</ti e>"
                    + "<isReaded>" + isReaded + "</isReaded>"
                    + "</nf>";
        }

    }*/
}
