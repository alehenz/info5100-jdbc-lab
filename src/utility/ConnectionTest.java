package utility;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Run this file on its own to check the database connection before any screen uses it.
 */
public class ConnectionTest {

    public static void main(String[] args) {
        try (Connection conn = DatabaseConnector.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT VERSION()")) {
            rs.next();
            System.out.println("Connected. MySQL server version: " + rs.getString(1));
            System.out.println("Rows in the users table: " + DatabaseConnector.getUsers().size());
        } catch (SQLException e) {
            System.out.println("Connection test failed: " + e.getMessage());
        }
    }
}
