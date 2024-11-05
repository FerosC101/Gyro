package com.example.controller;

import com.example.connection.DBConnection;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;
import static com.example.connection.DBConnection.getConnection;

public class UserController {

    public UserController() {
        DBConnection dbConnection = new DBConnection();
    }

    public Integer register(String username, String password) throws SQLException {
        String checkUserQuery = "SELECT * FROM users WHERE username = ?";
        String insertUserQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
        Integer userId = null;

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
                int affectedRows = insertUserStmt.executeUpdate();

                if (affectedRows > 0) {
                    ResultSet generatedKeys = insertUserStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        userId = generatedKeys.getInt(1); // Get the auto-generated user ID
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

    public void collectAdditionalInfo(int userId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your full name: ");
        String fullName = scanner.nextLine();

        System.out.print("Enter your birthdate (MM-DD-YYYY): ");
        String birthdateStr = scanner.nextLine();
        java.sql.Date birthdate;
        try {
            birthdate = new java.sql.Date(new SimpleDateFormat("MM-dd-yyyy").parse(birthdateStr).getTime());
        } catch (ParseException e) {
            System.err.println("Invalid date format. Please use MM-DD-YYYY.");
            return;
        }

        System.out.print("Enter your contact number: ");
        String contactNumber = scanner.nextLine();

        System.out.print("Enter your email: ");
        String email = scanner.nextLine();

        System.out.print("Enter your age: ");
        int age = scanner.nextInt();

        System.out.print("Enter your height(cm): ");
        float height = scanner.nextFloat();

        System.out.print("Enter your weight(kg): ");
        float weight = scanner.nextFloat();

        scanner.nextLine();
        System.out.print("Enter your gender (Male/Female/Other): ");
        String gender = scanner.nextLine();

        scanner.nextLine();
        System.out.print("Enter your profession: ");
        String profession = scanner.nextLine();

        String insertDetailsSQL = "INSERT INTO user_details (user_id, full_name, birthday, contact_number, email, age, height, weight, gender, profession) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertDetailsSQL)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, fullName);
            pstmt.setDate(3, birthdate);
            pstmt.setString(4, contactNumber);
            pstmt.setString(5, email);
            pstmt.setInt(6, age);
            pstmt.setFloat(7, height);
            pstmt.setFloat(8, weight);
            pstmt.setString(9, gender);
            pstmt.setString(10, profession);

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Additional information collected and saved successfully!");
            } else {
                System.out.println("Failed to save additional information.");
            }
        }
    }

    public void collectLifeExperience(int userId) throws SQLException {
        String query = "SELECT question_id, question_text, question_type, max_scale, choice_options FROM user_questions";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            Scanner scanner = new Scanner(System.in);

            while (rs.next()) {
                int questionId = rs.getInt("question_id");
                String questionText = rs.getString("question_text");
                String questionType = rs.getString("question_type");
                int maxScale = rs.getInt("max_scale");
                String choiceOptions = rs.getString("choice_options");

                System.out.println("\n" + questionText);

                int answer = 0;
                int expPoints = 0;

                if ("scale".equals(questionType)) {
                    System.out.println("Please rate on a scale from 1 to " + maxScale + ": ");
                    System.out.print("Your answer (1-" + maxScale + "): ");
                    answer = scanner.nextInt();

                    // Calculate exp points for scale questions
                    expPoints = calculateScaleExpPoints(answer);
                    System.out.println("You earned " + expPoints + " exp points.");

                } else if ("multiple_choice".equals(questionType) && choiceOptions != null) {
                    String[] choices = choiceOptions.split(",");
                    for (int i = 0; i < choices.length; i++) {
                        System.out.println((i + 1) + ". " + choices[i].trim());
                    }
                    System.out.print("Select a choice (1-" + choices.length + "): ");
                    answer = scanner.nextInt();

                    System.out.println("Answer saved for future use.");
                }

                // Store user answer and exp (0 exp for multiple-choice)
                storeUserAnswer(userId, questionId, answer, expPoints);
            }
        }
    }

    // Calculate exp points for scale questions based on 100 for 1, 150 for 2, etc.
    private int calculateScaleExpPoints(int answer) {
        return 100 + (answer - 1) * 50;
    }

    // Store answer in the database
    private void storeUserAnswer(int userId, int questionId, int answer, int expPoints) throws SQLException {
        String insertAnswerQuery = "INSERT INTO user_answers (user_id, question_id, answer, exp_points) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement insertStmt = conn.prepareStatement(insertAnswerQuery)) {
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, questionId);
            insertStmt.setInt(3, answer);
            insertStmt.setInt(4, expPoints);
            insertStmt.executeUpdate();
        }
    }


    public void editStats(int userId) {
        System.out.println("Edit stats for user ID: " + userId);
    }

    public void displayAdditionalQuestions(int userId) throws SQLException {
        System.out.println("Please answer the following questions:");
        collectAdditionalInfo(userId);
        collectLifeExperience(userId);
    }
}
