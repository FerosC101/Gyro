package com.example.controller;

import com.example.model.User;
import com.example.service.CredentialService;
import com.example.service.UserService;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import static com.example.connection.DBConnection.getConnection;

public class UserController {
    private final User user;
    private final UserService userService;
    private final CredentialService credentialService;

    public UserController() {
        this.credentialService = new CredentialService();
        this.userService = new UserService();
        this.user = new User();
    }

    public Integer register(String username, String password) throws SQLException {
        return userService.register(username, password);
    }

    public Integer login(String username, String password) throws SQLException {
        return userService.login(username, password);
    }

    public void addAchievement(int userId) throws SQLException {
        credentialService.addAchievement(userId);
    }
    
    public void addJobExperience(int userId) throws SQLException {
        credentialService.addJobExperience(userId);
    }

    public void collectAdditionalInfo(int userId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter your full name: ");
        user.setFullName(scanner.nextLine());

        System.out.print("Enter your birthdate (MM-DD-YYYY): ");
        String birthdateStr = scanner.nextLine();
        Date birthdate;
        try {
            birthdate = new Date(new SimpleDateFormat("MM-dd-yyyy").parse(birthdateStr).getTime());
            user.setBirthday(birthdate);
        } catch (ParseException e) {
            System.err.println("Invalid date format. Please use MM-DD-YYYY.");
            return;
        }

        System.out.print("Enter your contact number: ");
        user.setContactNumber(scanner.nextLine());

        System.out.print("Enter your email: ");
        user.setEmail(scanner.nextLine());

        System.out.print("Enter your age: ");
        user.setAge(scanner.nextInt());

        System.out.print("Enter your height(cm): ");
        user.setHeight(scanner.nextFloat());

        System.out.print("Enter your weight(kg): ");
        user.setWeight(scanner.nextFloat());

        scanner.nextLine();
        System.out.print("Enter your gender (Male/Female/Other): ");
        user.setGender(scanner.nextLine());

        System.out.print("Enter your profession: ");
        user.setProfession(scanner.nextLine());

        String insertDetailsSQL = "INSERT INTO user_details (user_id, full_name, birthday, contact_number, email, age, height, weight, gender, profession) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertDetailsSQL)) {

            pstmt.setInt(1, userId);
            pstmt.setString(2, user.getFullName());
            pstmt.setDate(3, user.getBirthday());
            pstmt.setString(4, user.getContactNumber());
            pstmt.setString(5, user.getEmail());
            pstmt.setInt(6, user.getAge());
            pstmt.setFloat(7, user.getHeight());
            pstmt.setFloat(8, user.getWeight());
            pstmt.setString(9, user.getGender());
            pstmt.setString(10, user.getProfession());

            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Additional information collected and saved successfully!");
            } else {
                System.out.println("Failed to save additional information.");
            }
        }
    }

    public void collectLifeExperience(int userId) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int totalExp = 0;

        System.out.println("Please answer the following questions by rating on a scale from 1-10.");

        System.out.println("\n1. Rate your physical fitness level on a scale from 1-10:");
        totalExp += scanner.nextInt() * 50 + 50;

        System.out.println("2. Rate your mental resilience on a scale from 1-10:");
        totalExp += scanner.nextInt() * 50 + 50;

        System.out.println("3. Rate your diet balance on a scale from 1-10:");
        int dietBalance = scanner.nextInt();
        totalExp += dietBalance * 50 + 50;

        System.out.println("4. Rate your sleep quality on a scale from 1-10:");
        int sleepQuality = scanner.nextInt();
        totalExp += sleepQuality * 50 + 50;

        System.out.println("5. Rate your stress management skills on a scale from 1-10:");
        int stressManagement = scanner.nextInt();
        totalExp += stressManagement * 50 + 50;

        System.out.println("6. Rate your work-life balance on a scale from 1-10:");
        int workLifeBalance = scanner.nextInt();
        totalExp += workLifeBalance * 50 + 50;

        System.out.println("7. Rate your social interaction quality on a scale from 1-10:");
        int socialInteraction = scanner.nextInt();
        totalExp += socialInteraction * 50 + 50;

        System.out.println("8. Rate your financial management skills on a scale from 1-10:");
        int financialManagement = scanner.nextInt();
        totalExp += financialManagement * 50 + 50;

        System.out.println("9. Rate your time management skills on a scale from 1-10:");
        int timeManagement = scanner.nextInt();
        totalExp += timeManagement * 50 + 50;

        System.out.println("10. Rate your creativity level on a scale from 1-10:");
        int creativity = scanner.nextInt();
        totalExp += creativity * 50 + 50;

        updateUserExp(userId, totalExp);
        System.out.println("Thank you for completing the life experience questionnaire. Your responses have been recorded, and your total experience points have been added.");

        boolean choosing = true;
        while (choosing) {
            System.out.println("What would you like to do next?");
            System.out.println("[1] Add Job Experience");
            System.out.println("[2] Add Achievement");
            System.out.println("[3] Continue to Log In");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addJobExperience(userId);
                    break;
                case 2:
                    addAchievement(userId);
                    break;
                case 3:
                    System.out.println("Continuing to log in...");
                    choosing = false;
                    break;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
    }

    public int getUserExp(int userId) throws SQLException {
        String query = "SELECT exp FROM users WHERE user_id = ?";
        int exp = 0;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                exp = rs.getInt("exp");
            }
        }
        user.setExp(exp);
        return exp;
    }

    public void updateUserExp(int userId, int additionalExp) throws SQLException {
        int newExp = user.getExp() + additionalExp;
        user.setExp(newExp);
        String query = "UPDATE users SET exp = ? WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newExp);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public void displayAdditionalQuestions(int userId) throws SQLException {
        System.out.println("Please answer the following questions:");
        collectAdditionalInfo(userId);
        collectLifeExperience(userId);
    }

    public void editStats(int userId) {
        System.out.println("Edit stats for user ID: " + userId);
    }
}
