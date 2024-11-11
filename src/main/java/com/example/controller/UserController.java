package com.example.controller;

import com.example.connection.UserDAO;
import com.example.model.User;
import com.example.service.CredentialService;
import com.example.service.UserService;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.example.connection.DBConnection.getConnection;

public class UserController {
    private final User user;
    private final UserService userService;
    private final CredentialService credentialService;
    private final UserDAO userDAO;

    public UserController() {
        this.userDAO = new UserDAO();
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

    public void addExpToUser(int userId, int exp) throws SQLException {
        int currentExp = getUserExp(userId);
        int newExp = currentExp + exp;
        user.setExp(newExp);

        String updateExpSQL = "UPDATE users SET exp = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateExpSQL)) {
            pstmt.setInt(1, newExp);
            pstmt.setInt(2, userId);
            pstmt.executeUpdate();
        }

        System.out.println("EXP successfully updated by " + exp + ". New total: " + newExp);
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
        return exp;
    }

    public int calculateAgeExp(User user) {
        LocalDate today = LocalDate.now();
        LocalDate birthday = user.getBirthday().toLocalDate();

        int age = user.getAge();
        if (today.getMonth() == birthday.getMonth() && today.getDayOfMonth() == birthday.getDayOfMonth()) {
            age += 1;
        }

        int ageExp = age * 1000;
        user.setExp(user.getExp() + ageExp);

        return ageExp;
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

        int ageExp = calculateAgeExp(user);
        addExpToUser(userId, ageExp);
        System.out.println("Age-based EXP of " + ageExp + " has been added to your account.");

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

    public void editUserInfo(int userId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Edit your information. Leave the field empty if you do not wish to change it.");

        System.out.print("Enter new contact number (leave blank to keep current): ");
        String contactNumber = scanner.nextLine();
        if (contactNumber.isEmpty()) {
            contactNumber = getUserField(userId, "contact_number");
        }

        System.out.print("Enter new email (leave blank to keep current): ");
        String email = scanner.nextLine();
        if (email.isEmpty()) {
            email = getUserField(userId, "email");
        }

        System.out.print("Enter new height (leave blank to keep current): ");
        String heightInput = scanner.nextLine();
        float height = heightInput.isEmpty() ? Float.parseFloat(getUserField(userId, "height")) : Float.parseFloat(heightInput);

        System.out.print("Enter new weight (leave blank to keep current): ");
        String weightInput = scanner.nextLine();
        float weight = weightInput.isEmpty() ? Float.parseFloat(getUserField(userId, "weight")) : Float.parseFloat(weightInput);

        System.out.print("Enter new profession (leave blank to keep current): ");
        String profession = scanner.nextLine();
        if (profession.isEmpty()) {
            profession = getUserField(userId, "profession");
        }

        String updateSQL = "UPDATE user_details SET contact_number = ?, email = ?, height = ?, weight = ?, profession = ? WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, contactNumber);
            pstmt.setString(2, email);
            pstmt.setFloat(3, height);
            pstmt.setFloat(4, weight);
            pstmt.setString(5, profession);
            pstmt.setInt(6, userId);

            int rowsUpdated = pstmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Your information has been updated successfully.");
            } else {
                System.out.println("Failed to update your information.");
            }
        }
    }

    private String getUserField(int userId, String fieldName) throws SQLException {
        String query = "SELECT " + fieldName + " FROM user_details WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString(fieldName);
            } else {
                throw new SQLException("User not found.");
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

        addExpToUser(userId, totalExp);
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

    public void viewAccount(int userId) {
        try {
            User user = userDAO.getUserById(userId); // Fetch user by ID
            if (user != null) {
                System.out.println("=========== Account Information ===========");
                System.out.printf("%-15s: %s%n", "Full Name", user.getFullName());
                System.out.printf("%-15s: %s%n", "Contact Number", user.getContactNumber());
                System.out.printf("%-15s: %s%n", "Email", user.getEmail());
                System.out.printf("%-15s: %d%n", "Age", user.getAge());
                System.out.printf("%-15s: %.1f cm%n", "Height", user.getHeight());
                System.out.printf("%-15s: %.1f kg%n", "Weight", user.getWeight());
                System.out.printf("%-15s: %s%n", "Profession", user.getProfession());
                System.out.println("===========================================");
            } else {
                System.out.println("User not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving account information: " + e.getMessage());
        }
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

    public void displayDailyRoutines(int userId) throws SQLException {
        Scanner scanner = new Scanner(System.in);
        LocalDate today = LocalDate.now();
        boolean completedRoutines = false;

        List<String> currentRoutines = getUserAssignedRoutines(userId, today);
        if (currentRoutines.isEmpty() && checkIfRoutinesCompletedForToday(userId, today)) {
            currentRoutines = getRandomRoutines();
            assignUserRoutines(userId, currentRoutines, today);
        }

        if (checkIfRoutinesCompletedForToday(userId, today)) {
            while (!completedRoutines) {
                System.out.println("Here are your daily routines:");
                for (int i = 0; i < currentRoutines.size(); i++) {
                    System.out.println((i + 1) + ". " + currentRoutines.get(i));
                }

                System.out.print("Have you completed all routines? (yes/no): ");
                String answer = scanner.nextLine();

                if (answer.equalsIgnoreCase("yes")) {
                    System.out.println("Congratulations on completing your daily routines!");
                    addExpToUser(userId, 150);
                    markRoutinesCompleted(userId, today);
                    completedRoutines = true;
                } else {
                    System.out.println("Please complete all routines to proceed.");
                    break;
                }
            }
        } else {
            System.out.println("You have already completed your routines for today.");
        }
    }

    private boolean checkIfRoutinesCompletedForToday(int userId, LocalDate date) throws SQLException {
        String query = "SELECT completed FROM user_daily_routines WHERE user_id = ? AND date = ? AND completed = TRUE";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            return !rs.next();
        }
    }

    private List<String> getUserAssignedRoutines(int userId, LocalDate date) throws SQLException {
        List<String> routines = new ArrayList<>();
        String query = "SELECT dr.routine FROM daily_routines dr " +
                       "JOIN user_daily_routines udr ON dr.id = udr.routine_id " +
                       "WHERE udr.user_id = ? AND udr.date = ? AND udr.completed = FALSE";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                routines.add(rs.getString("routine"));
            }
        }
        return routines;
    }

    private void assignUserRoutines(int userId, List<String> routines, LocalDate date) throws SQLException {
        if (checkRoutinesExistForToday(userId, date)) {
            System.out.println("Routines for today are already assigned.");
            return;
        }

        String insertSQL = "INSERT INTO user_daily_routines (user_id, routine_id, date, completed) VALUES (?, ?, ?, FALSE)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            for (String routine : routines) {
                int routineId = getRoutineIdByName(routine);
                pstmt.setInt(1, userId);
                pstmt.setInt(2, routineId);
                pstmt.setDate(3, Date.valueOf(date));
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }

    }

    private boolean checkRoutinesExistForToday(int userId, LocalDate date) throws SQLException {
        String query = "SELECT 1 FROM user_daily_routines WHERE user_id = ? AND date = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(date));
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        }
    }

    private int getRoutineIdByName(String routineName) throws SQLException {
        String query = "SELECT id FROM daily_routines WHERE routine = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, routineName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        throw new SQLException("Routine not found: " + routineName);
    }

    private void markRoutinesCompleted(int userId, LocalDate date) throws SQLException {
        String updateSQL = "UPDATE user_daily_routines SET completed = TRUE WHERE user_id = ? AND date = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setInt(1, userId);
            pstmt.setDate(2, Date.valueOf(date));
            pstmt.executeUpdate();
        }
    }

    private List<String> getRandomRoutines() throws SQLException {
        List<String> routines = new ArrayList<>();
        String query = "SELECT routine FROM daily_routines ORDER BY RAND() LIMIT 5";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                routines.add(rs.getString("routine"));
            }
        }
        return routines;
    }

}
    