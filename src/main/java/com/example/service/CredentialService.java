package com.example.service;

import com.example.connection.DBConnection;
import com.example.controller.UserController;
import com.example.model.Credential;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

import static com.example.connection.DBConnection.getConnection;

public class CredentialService {

    private final Scanner scanner;

    public CredentialService() {
        this.scanner = new Scanner(System.in);
    }

    public void addAchievement(int userId) throws SQLException {
        System.out.print("Enter category (Local, Personal, National, International): ");
        String category = scanner.nextLine();

        System.out.print("Enter achievement name: ");
        String achievementName = scanner.nextLine();

        System.out.print("Enter description: ");
        String description = scanner.nextLine();

        System.out.print("Enter date achieved (MM-DD-YYYY): ");
        String dateAchieved = scanner.nextLine();

        System.out.print("Enter any additional notes (optional): ");
        String notes = scanner.nextLine();

        Date sqlDateAchieved = parseDate(dateAchieved);

        if (sqlDateAchieved == null) {
            System.err.println("Invalid date format. Achievement not added.");
            return;
        }

        Credential achievement = new Credential();

        String insertAchievementQuery = "INSERT INTO credentials (user_id, achievement_name, description, category, date_achieved, notes) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertAchievementQuery)) {

            stmt.setInt(1, achievement.getUserId());
            stmt.setString(2, achievement.getAchievementName());
            stmt.setString(3, achievement.getDescription());
            stmt.setString(4, achievement.getCategory());
            stmt.setDate(5, sqlDateAchieved);
            stmt.setString(6, achievement.getNotes());

            stmt.executeUpdate();
            System.out.println("Achievement added successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to add achievement: " + e.getMessage());
            throw e;
        }
    }

    public void addJobExperience(int userId) throws SQLException {
        System.out.println("Please enter the details of your job experience:");

        System.out.print("Enter the company name: ");
        String companyName = scanner.nextLine();

        System.out.print("Enter the job title/role: ");
        String jobTitle = scanner.nextLine();

        System.out.print("Enter the date started (MM-DD-YYYY): ");
        String startDate = scanner.nextLine();

        System.out.print("Enter the date ended (MM-DD-YYYY or 'Present' if ongoing): ");
        String endDate = scanner.nextLine();

        System.out.print("Enter the job description: ");
        String jobDescription = scanner.nextLine();

        System.out.print("Is this an internship, full-time, or part-time job? ");
        String jobType = scanner.nextLine().toLowerCase();

        Date sqlStartDate = parseDate(startDate);
        Date sqlEndDate = endDate.equalsIgnoreCase("Present") ? null : parseDate(endDate);

        if (sqlStartDate == null || (!endDate.equalsIgnoreCase("Present") && sqlEndDate == null)) {
            System.err.println("Invalid date format. Job experience not added.");
            return;
        }

        Credential jobExperience = new Credential();

        String insertJobExperienceQuery = "INSERT INTO job_experience (user_id, company_name, job_title, start_date, end_date, description) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertJobExperienceQuery)) {

            stmt.setInt(1, jobExperience.getUserId());
            stmt.setString(2, jobExperience.getCompanyName());
            stmt.setString(3, jobExperience.getJobTitle());
            stmt.setDate(4, sqlStartDate);

            if (sqlEndDate == null) {
                stmt.setNull(5, Types.DATE);
            } else {
                stmt.setDate(5, sqlEndDate);
            }

            stmt.setString(6, jobExperience.getJobDescription());

            stmt.executeUpdate();
            System.out.println("Job experience added successfully!");

        } catch (SQLException e) {
            System.err.println("Failed to add job experience: " + e.getMessage());
            throw e;
        }
    }

    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            sdf.setLenient(false);
            java.util.Date parsedDate = sdf.parse(dateStr);
            return new java.sql.Date(parsedDate.getTime());
        } catch (ParseException e) {
            System.err.println("Invalid date format: " + dateStr);
            return null;
        }
    }

    private int calculateJobExp(int userId, String jobTitle, String startDate, String endDate, String jobType) {
        int baseExp = 1000;
        double jobTypeMultiplier = 1.0;

        if (jobType.equals("internship")) {
            jobTypeMultiplier = 0.3;
        } else if (jobType.equals("part-time")) {
            jobTypeMultiplier = 0.6;
        }

        int experiencePoints = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            java.util.Date start = sdf.parse(startDate);
            java.util.Date end = endDate.equalsIgnoreCase("Present") ? new java.util.Date() : sdf.parse(endDate);

            long durationInMillis = end.getTime() - start.getTime();
            long durationInMonths = durationInMillis / (1000L * 60 * 60 * 24 * 30);

            experiencePoints = (int) (baseExp * jobTypeMultiplier * durationInMonths);
            experiencePoints = (int) (experiencePoints * Math.pow(1.05, durationInMonths));

            UserController userController = new UserController();
            userController.updateUserExp(userId, experiencePoints);

        } catch (ParseException e) {
            System.err.println("Invalid date format. Could not calculate experience points.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return experiencePoints;
    }
}
