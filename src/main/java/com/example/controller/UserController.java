package com.example.controller;

import com.example.connection.DBConnection;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import static com.example.connection.DBConnection.getConnection;

public class UserController {
    
    private DBConnection dbConnection;

    public UserController() {
        dbConnection = new DBConnection();
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
    
    public static void saveUserDetails(int user_id, String full_name, Date birthday, String contact_number, String email, int age, String gender, float height, float weight) throws SQLException {
        String insertDetailsQuery = "INSERT INTO user_details (user_id, full_name, birthday, contact_number, email, age, gender, height, weight) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
        PreparedStatement stmt = conn.prepareStatement(insertDetailsQuery)) {
            
            stmt.setInt(1, user_id);
            stmt.setString(2, full_name);
            stmt.setDate(3, birthday);
            stmt.setString(4, contact_number);
            stmt.setString(5, email);
            stmt.setInt(6, age);
            stmt.setString(7, gender);
            stmt.setFloat(8, height);
            stmt.setFloat(9, weight);

            stmt.executeUpdate();
            System.out.println("Details added successfully!");
        }
    }

    private Date formatDate(String dateStr) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
            java.util.Date utilDate = formatter.parse(dateStr);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            System.out.println("Invalid date format. Please enter the date in MM-DD-YYYY format.");
            return null;
        }
    }


    public void editStats(int userId) {
        System.out.println("Edit stats for user ID: " + userId);
    }
}
