package com.example.controller;

import java.sql.*;
import java.util.Scanner;
import static com.example.connection.DBConnection.getConnection;

public class UserController {

    private final Scanner scanner;

    public UserController(Scanner scanner) {
        this.scanner = scanner;
    }

    public void register(String username, String password) throws SQLException {
        String checkUserQuery = "SELECT * FROM users WHERE username = ?";
        String insertUserQuery = "INSERT INTO users (username, password) VALUES (?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement checkUserStmt = conn.prepareStatement(checkUserQuery);
             PreparedStatement insertUserStmt = conn.prepareStatement(insertUserQuery, Statement.RETURN_GENERATED_KEYS)) {

            checkUserStmt.setString(1, username);
            ResultSet rs = checkUserStmt.executeQuery();

            if (rs.next()) {
                System.out.println("User already exists.");
            } else {
                insertUserStmt.setString(1, username);
                insertUserStmt.setString(2, password);
                insertUserStmt.executeUpdate();

                ResultSet generatedKeys = insertUserStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);
                    System.out.println("User registered successfully! Your user ID is: " + userId);
                }
            }
        } catch (SQLException e) {
            System.err.println("Registration failed: " + e.getMessage());
            throw e;
        }
    }

    public Integer login(String username, String password) throws SQLException {
        String query = "SELECT user_id, password FROM users WHERE username = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");

                if (password.equals(storedPassword)) {
                    int userId = rs.getInt("user_id");
                    System.out.println("Login successful!");
                    return userId;
                } else {
                    System.out.println("Invalid username or password.");
                    return null;
                }
            } else {
                System.out.println("Invalid username or password.");
                return null;
            }
        }
    }


    public void addAchievement(int userId, String achievementName, String description, String category, String dateAchieved, String notes) throws SQLException {
        String insertAchievementQuery = "INSERT INTO credentials (user_id, achievement_name, description, category, date_achieved, notes) VALUES (?, ?, ?, ?, STR_TO_DATE(?, '%m-%d-%Y'), ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertAchievementQuery)) {

            stmt.setInt(1, userId);
            stmt.setString(2, achievementName);
            stmt.setString(3, description);
            stmt.setString(4, category);
            stmt.setString(5, dateAchieved);
            stmt.setString(6, notes);

            stmt.executeUpdate();
            System.out.println("Achievement added successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to add achievement: " + e.getMessage());
            throw e;
        }
    }


    public void editStats(int userId) {
        System.out.println("Edit stats for user ID: " + userId);
        // Additional stat-editing logic to be implemented here
    }
}
