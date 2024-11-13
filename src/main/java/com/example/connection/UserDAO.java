package com.example.connection;

import com.example.model.User;

import java.sql.*;


import static com.example.connection.DBConnection.getConnection;

public class UserDAO {
    public User getUserById(int userId) throws SQLException {
        String selectQuery = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Failed to get user by ID: " + e.getMessage());
            throw e;
        }
    }


    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setBirthday(rs.getDate("birthday"));
        user.setContactNumber(rs.getString("contact_number"));
        user.setEmail(rs.getString("email"));
        user.setAge(rs.getInt("age"));
        user.setHeight(rs.getInt("height"));
        user.setWeight(rs.getInt("weight"));
        user.setGender(rs.getString("gender"));
        user.setProfession(rs.getString("profession"));
        return user;
    }
}
