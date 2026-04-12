package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_management_system";
    private static final String USER = "root";
    private static final String PASSWORD = "celerbelleta";

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Hotel System: Connection Successful!");
        } catch (SQLException e) {
            System.out.println("Connection Failed.");
            e.printStackTrace();
        }
        return connection;
    }
}

// call this para ma connect ang system sa database
// db.DC.getConnection();