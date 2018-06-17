package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    public static Connection getConnection() throws ClassNotFoundException, SQLException {

        String url = "jdbc:mysql://localhost:3306/accounts?useSSL=false";

        Connection connector;

        Class.forName("com.mysql.jdbc.Driver");

        connector = DriverManager.getConnection(url, "root", "123456");

        return connector;
    }
}
