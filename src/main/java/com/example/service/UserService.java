package com.example.service;

import com.example.connection.DBConnection;
import com.example.model.User;

import java.sql.*;


public class UserService {
    private DBConnection dbConnection = new DBConnection();
    private final User user = new User();

    public UserService() {
    }

    public Integer register(String username, String password) throws SQLException {
        String checkUserQuery = "SELECT * FROM users WHERE username = ?";
        String insertUserQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
        Integer userId = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement checkUserStmt = conn.prepareStatement(checkUserQuery);
             PreparedStatement insertUserStmt = conn.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS)) {

            checkUserStmt.setString(1, username);
            ResultSet rs = checkUserStmt.executeQuery();

            if (rs.next()) {
                System.out.println("User already exists.");
            } else {
                insertUserStmt.setString(1, username);
                insertUserStmt.setString(2, password);
                int affectedRows = insertUserStmt.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = insertUserStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1);
                        System.out.println("User registered successfully! Your user ID is: " + userId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            throw e;
        }
        return userId;
    }

    public Integer login(String username, String password) throws SQLException {
        String query = "SELECT user_id, password FROM users WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (password.equals(storedPassword)) {
                    int userId = rs.getInt("user_id");
                    System.out.println("Login successful!");
                    user.setUserId(userId);
                    return userId;
                } else {
                    System.out.println("Invalid username or password.");
                }
            } else {
                System.out.println("Invalid username or password.");
            }
        }
        return null;
    }
}
