package com.example.service;

import com.example.connection.DBConnection;
import com.example.connection.UserDAO;
import com.example.controller.CredentialController;
import com.example.model.Credential;
import com.example.model.User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.example.connection.DBConnection.getConnection;


public class UserService extends AccountService {
    private final DBConnection dbConnection = new DBConnection();
    private final User user = new User();
    private final UserDAO userDAO = new UserDAO();
    private final CredentialController credentialController = new CredentialController();

    public void displayAdditionalQuestions(int userId) throws SQLException {
        System.out.println("Please answer the following questions:");
        collectAdditionalInfo(userId);
        collectLifeExperience(userId);
    }

    @Override
    public void manageUserSession(int userId, Scanner scanner) throws SQLException {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n==== User Menu ====");
            System.out.println("[1] Add Credential");
            System.out.println("[2] Add Job Experience");
            System.out.println("[3] Edit Information");
            System.out.println("[4] View Routines");
            System.out.println("[5] View Account");
            System.out.println("[6] Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> credentialController.addAchievement(userId);
                case 2 -> credentialController.addJobExperience(userId);
                case 3 -> editUserInfo(userId);
                case 4 -> displayDailyRoutines(userId);
                case 5 -> viewAccount(userId);
                case 6 -> {
                    System.out.println("Logging out...");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
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
        userDAO.updateUserExp(userId, ageExp);
        System.out.println("\nAge-based EXP of " + ageExp + " has been added to your account.");

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
                System.out.println("Additional information collected and saved successfully!\n");
            } else {
                System.out.println("Failed to save additional information.");
            }
        }
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


    public void editUserInfo(int userId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("\n==== Edit Information ====");
        System.out.println("\nEdit your information. Leave the field empty if you do not wish to change it.");

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

        System.out.println("\n ==== Please answer the following questions by rating on a scale from 1-10. ====");

        System.out.println("\n1. Rate your physical fitness level on a scale from 1-10:");
        totalExp += scanner.nextInt() * 50 + 100;

        System.out.println("2. Rate your mental resilience on a scale from 1-10:");
        totalExp += scanner.nextInt() * 50 + 100;

        System.out.println("3. Rate your diet balance on a scale from 1-10:");
        int dietBalance = scanner.nextInt();
        totalExp += dietBalance * 50 + 100;

        System.out.println("4. Rate your sleep quality on a scale from 1-10:");
        int sleepQuality = scanner.nextInt();
        totalExp += sleepQuality * 50 + 100;

        System.out.println("5. Rate your stress management skills on a scale from 1-10:");
        int stressManagement = scanner.nextInt();
        totalExp += stressManagement * 50 + 100;

        System.out.println("6. Rate your work-life balance on a scale from 1-10:");
        int workLifeBalance = scanner.nextInt();
        totalExp += workLifeBalance * 50 + 100;

        System.out.println("7. Rate your social interaction quality on a scale from 1-10:");
        int socialInteraction = scanner.nextInt();
        totalExp += socialInteraction * 50 + 100;

        System.out.println("8. Rate your financial management skills on a scale from 1-10:");
        int financialManagement = scanner.nextInt();
        totalExp += financialManagement * 50 + 100;

        System.out.println("9. Rate your time management skills on a scale from 1-10:");
        int timeManagement = scanner.nextInt();
        totalExp += timeManagement * 50 + 100;

        System.out.println("10. Rate your creativity level on a scale from 1-10:");
        int creativity = scanner.nextInt();
        totalExp += creativity * 50 + 100;

        userDAO.updateUserExp(userId, totalExp);
        System.out.println("Thank you for completing the life experience questionnaire. Your responses have been recorded, and your total experience points have been added.");

        boolean choosing = true;
        while (choosing) {
            System.out.println("\n=== What would you like to do next? ===");
            System.out.println("[1] Add Job Experience");
            System.out.println("[2] Add Achievement");
            System.out.println("[3] Continue to Log In");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    credentialController.addJobExperience(userId);
                    break;
                case 2:
                    credentialController.addAchievement(userId);
                    break;
                case 3:
                    System.out.println("\nRedirecting to User Menu...");
                    choosing = false;
                    manageUserSession(userId, scanner);
                    break;
                default:
                    System.out.println("Invalid option. Please choose again.");
            }
        }
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
                    userDAO.updateUserExp(userId, 150);
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

    public void viewAccount(int userId) throws SQLException {
        try (Connection conn = getConnection()) {

            User user = new User();
            String userQuery = "SELECT ud.full_name, ud.profession, u.exp " +
                    "FROM users u " +
                    "JOIN user_details ud ON u.user_id = ud.user_id " +
                    "WHERE u.user_id = ?";
            try (PreparedStatement userStmt = conn.prepareStatement(userQuery)) {
                userStmt.setInt(1, userId);
                ResultSet userRs = userStmt.executeQuery();
                if (userRs.next()) {
                    user.setFullName(userRs.getString("full_name"));
                    user.setProfession(userRs.getString("profession"));
                    user.setExp(userRs.getInt("exp"));
                } else {
                    System.out.println("User not found.");
                    return;
                }
            }

            System.out.println("========== User Profile ==========");
            System.out.println("Full Name    : " + user.getFullName());
            System.out.println("Level        : " + calculateLevel(user.getExp()));
            System.out.println("Profession   : " + user.getProfession());

            String detailsQuery = "SELECT contact_number, email, age, height, weight, gender " +
                    "FROM user_details WHERE user_id = ?";
            try (PreparedStatement detailsStmt = conn.prepareStatement(detailsQuery)) {
                detailsStmt.setInt(1, userId);
                ResultSet detailsRs = detailsStmt.executeQuery();
                if (detailsRs.next()) {
                    user.setContactNumber(detailsRs.getString("contact_number"));
                    user.setEmail(detailsRs.getString("email"));
                    user.setAge(detailsRs.getInt("age"));
                    user.setHeight(detailsRs.getFloat("height"));
                    user.setWeight(detailsRs.getFloat("weight"));
                    user.setGender(detailsRs.getString("gender"));
                }
            }

            System.out.println("\n========== Other Details ==========");
            System.out.println("Contact Number : " + user.getContactNumber());
            System.out.println("Email          : " + user.getEmail());
            System.out.println("Age            : " + user.getAge());
            System.out.println("Height         : " + user.getHeight() + " cm");
            System.out.println("Weight         : " + user.getWeight() + " kg");
            System.out.println("Gender         : " + user.getGender());

            System.out.println("\n========== Credentials ==========");

            String experienceQuery = "SELECT job_title, company_name, start_date, end_date, description, job_type " +
                    "FROM job_experience WHERE user_id = ?";
            try (PreparedStatement expStmt = conn.prepareStatement(experienceQuery)) {
                expStmt.setInt(1, userId);
                ResultSet expRs = expStmt.executeQuery();
                System.out.println("\n--- Experience ---");
                while (expRs.next()) {
                    Credential experience = new Credential();
                    experience.setJobTitle(expRs.getString("job_title"));
                    experience.setCompanyName(expRs.getString("company_name"));
                    experience.setStartDate(expRs.getDate("start_date"));
                    experience.setEndDate(expRs.getDate("end_date"));
                    experience.setJobType(expRs.getString("job_type"));
                    experience.setJobDescription(expRs.getString("description"));

                    System.out.println("Title       : " + experience.getJobTitle());
                    System.out.println("Company     : " + experience.getCompanyName());
                    System.out.println("Start Date  : " + experience.getStartDate());
                    System.out.println("End Date    : " + experience.getEndDate());
                    System.out.println("Job Type    : " + experience.getJobType());
                    System.out.println("Description : " + experience.getJobDescription());
                    System.out.println("-------------------------------");
                }
            }

            String achievementQuery = "SELECT achievement_name, category, date_achieved, description, notes " +
                    "FROM credentials WHERE user_id = ?";
            try (PreparedStatement achStmt = conn.prepareStatement(achievementQuery)) {
                achStmt.setInt(1, userId);
                ResultSet achRs = achStmt.executeQuery();
                System.out.println("\n--- Achievements ---");
                while (achRs.next()) {
                    Credential achievement = new Credential();
                    achievement.setAchievementName(achRs.getString("achievement_name"));
                    achievement.setCategory(achRs.getString("category"));
                    achievement.setDateAchieved(achRs.getDate("date_achieved"));
                    achievement.setDescription(achRs.getString("description"));
                    achievement.setNotes(achRs.getString("notes"));

                    System.out.println("Achievement : " + achievement.getAchievementName());
                    System.out.println("Category    : " + achievement.getCategory());
                    System.out.println("Date        : " + achievement.getDateAchieved());
                    System.out.println("Description : " + achievement.getDescription());
                    System.out.println("Notes       : " + achievement.getNotes());
                    System.out.println("-------------------------------");
                }
            }
            System.out.println("=================================\n");
        }
    }

    public int calculateLevel(int exp) {
        int level = 0;
        int requiredExp = 1000;

        while (exp >= requiredExp) {
            exp -= requiredExp;
            level++;
            requiredExp += 250;
        }

        return level;
    }

    public void viewGlobalServer() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int offset = 0;
        final int limit = 5;
        boolean continueViewing = true;

        while (continueViewing) {
            try (Connection conn = getConnection()) {
                String userQuery = "SELECT u.user_id, ud.full_name, u.exp, ud.profession " +
                        "FROM users u " +
                        "JOIN user_details ud ON u.user_id = ud.user_id " +
                        "LIMIT ? OFFSET ?";
                try (PreparedStatement stmt = conn.prepareStatement(userQuery)) {
                    stmt.setInt(1, limit);
                    stmt.setInt(2, offset);
                    ResultSet rs = stmt.executeQuery();

                    System.out.println("\n========== Global Server Users ==========");
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        int userId = rs.getInt("user_id");
                        String fullName = rs.getString("full_name");
                        int exp = rs.getInt("exp");
                        String profession = rs.getString("profession");
                        int level = calculateLevel(exp);

                        System.out.println("User ID     : " + userId);
                        System.out.println("Full Name   : " + fullName);
                        System.out.println("Level       : " + level);
                        System.out.println("Profession  : " + profession);
                        System.out.println("-------------------------------");
                    }

                    if (count == 0) {
                        System.out.println("No users found.");
                    }
                }
            }

            System.out.println("\n========== Options ==========");
            System.out.println("[1] Next 5 Users");
            System.out.println("[2] Previous 5 Users");
            System.out.println("[3] Search User by Full Name");
            System.out.println("[4] Back to Main Menu");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    offset += limit;
                    break;
                case 2:
                    if (offset >= limit) {
                        offset -= limit;
                    } else {
                        System.out.println("\nYou're already at the beginning of the list.\n");
                    }
                    break;
                case 3:
                    System.out.print("Enter full name to search: ");
                    String fullNameSearch = scanner.nextLine();
                    searchUserByFullName(fullNameSearch);
                    break;
                case 4:
                    continueViewing = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    public void searchUserByFullName(String fullName) throws SQLException {
        try (Connection conn = getConnection()) {
            String query = "SELECT u.user_id, ud.full_name, u.exp, ud.profession " +
                    "FROM users u " +
                    "JOIN user_details ud ON u.user_id = ud.user_id " +
                    "WHERE ud.full_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, fullName);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("user_id");
                    System.out.println("\nUser found! Displaying full information...\n");
                    viewAccount(userId);
                } else {
                    System.out.println("User not found.");
                }
            }
        }
    }
}
