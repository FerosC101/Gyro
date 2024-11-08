package com.example.connection;

import com.example.connection.DBConnection;
import com.example.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    public List<User> getAllUsers() throws SQLException {
        String selectQuery = "SELECT * FROM users";
        List<User> users = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            System.err.println("Failed to get all users: " + e.getMessage());
            throw e;
        }
        return users;
    }

    public void updateUser(User user) throws SQLException {
        String updateQuery = "UPDATE user_details SET full_name = ?, birthday = ?, contact_number = ?, email = ?, age = ?, height = ?, weight = ?,  gender = ?, profession = ? WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {

            stmt.setString(1, user.getFullName());
            stmt.setDate(2, user.getBirthday());
            stmt.setString(3, user.getContactNumber());
            stmt.setString(4, user.getEmail());
            stmt.setInt(5, user.getAge());
            stmt.setInt(6, user.getHeight());
            stmt.setInt(7, user.getWeight());
            stmt.setString(8, user.getGender());
            stmt.setInt(9, user.getUserId());

            stmt.executeUpdate();
            System.out.println("User updated successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to update user: " + e.getMessage());
            throw e;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setFullName(rs.getString("full_name"));
        user.setAge(rs.getInt("age"));
        user.setHeight(rs.getInt("height"));
        user.setWeight(rs.getInt("weight"));
        user.setBirthday(rs.getDate("birthdate"));
        return user;
    }
}
