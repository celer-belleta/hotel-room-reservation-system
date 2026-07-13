package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hotel_room_reservation_system";
    private static final String USER = "root";
    private static final String PASSWORD = "your_mysql_password"; // Replace with your MySQL password

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

// import then call this para ma connect ang system sa database
// DBConnection.getConnection();
// https://www.youtube.com/watch?v=7v2OnUti2eM