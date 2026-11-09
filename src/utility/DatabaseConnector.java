package utility;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import model.User;

/**
 * Connects to the MySQL server in Docker and performs the CRUD operations
 * for the users table. Local lab values only: never reuse this password.
 */
public class DatabaseConnector {

    // DB_URL = "jdbc:<database>://ip_address:port/database_name";
    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/lab_db";
    static final String DB_USERNAME = "root";
    static final String DB_PASSWORD = "my-secret-pw";

    // Create a connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
    }

    // C - Create: inserting a user into the database
    public static void addUser(User u) throws SQLException {
        String query = "INSERT INTO users (first_name, last_name, gender, date_of_birth, phone, "
                + "email, continent, experience, hobbies, photo_path) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            bindUser(stmt, u);
            stmt.executeUpdate();
        }
    }

    // R - Read: getting all the users from the database
    public static ArrayList<User> getUsers() throws SQLException {
        ArrayList<User> users = new ArrayList<>();
        String query = "SELECT * FROM users ORDER BY id";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(readUser(rs));
            }
        }
        return users;
    }

    // U - Update: changing a user. The WHERE clause uses the primary key (id).
    public static int updateUser(User oldUser, User updatedUser) throws SQLException {
        String query = "UPDATE users SET first_name = ?, last_name = ?, gender = ?, date_of_birth = ?, "
                + "phone = ?, email = ?, continent = ?, experience = ?, hobbies = ?, photo_path = ? "
                + "WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            bindUser(stmt, updatedUser);
            stmt.setInt(11, oldUser.getUserId());
            return stmt.executeUpdate();
        }
    }

    // D - Delete: removing a user
    public static int deleteUser(User u) throws SQLException {
        String query = "DELETE FROM users WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, u.getUserId());
            return stmt.executeUpdate();
        }
    }

    // Fills parameters 1 to 10, shared by insert and update so the order stays the same
    private static void bindUser(PreparedStatement stmt, User u) throws SQLException {
        stmt.setString(1, u.getFirstName());
        stmt.setString(2, u.getLastName());
        stmt.setString(3, u.getGender());
        stmt.setDate(4, Date.valueOf(u.getDateOfBirth()));
        stmt.setString(5, u.getPhone());
        stmt.setString(6, u.getEmail());
        stmt.setString(7, u.getContinent());
        stmt.setString(8, u.getExperience());
        stmt.setString(9, u.getHobbies());
        stmt.setString(10, u.getPhotoPath());
    }

    // Builds a User from the current row. Age is not stored, it is worked out from the birth date.
    private static User readUser(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("id"));
        u.setFirstName(rs.getString("first_name"));
        u.setLastName(rs.getString("last_name"));
        u.setGender(rs.getString("gender"));
        LocalDate dob = rs.getDate("date_of_birth").toLocalDate();
        u.setDateOfBirth(dob);
        u.setAge(Period.between(dob, LocalDate.now()).getYears());
        u.setPhone(rs.getString("phone"));
        u.setEmail(rs.getString("email"));
        u.setContinent(rs.getString("continent"));
        u.setExperience(rs.getString("experience"));
        u.setHobbies(rs.getString("hobbies"));
        u.setPhotoPath(rs.getString("photo_path"));
        return u;
    }
}
