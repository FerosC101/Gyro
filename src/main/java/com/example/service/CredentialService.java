package com.example.service;

import com.example.connection.DBConnection;
import com.example.model.Credential;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
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

        Credential achievement = new Credential(0, userId, achievementName, description, category,
                java.sql.Date.valueOf(Objects.requireNonNull(formatDate(dateAchieved))), notes, 0, null, null,
                null, null, null);

        String insertAchievementQuery = "INSERT INTO credentials (user_id, achievement_name, description, category, date_achieved, notes) VALUES (?, ?, ?, ?, STR_TO_DATE(?, '%m-%d-%Y'), ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertAchievementQuery)) {

            stmt.setInt(1, achievement.getUserId());
            stmt.setString(2, achievement.getAchievementName());
            stmt.setString(3, achievement.getDescription());
            stmt.setString(4, achievement.getCategory());
            stmt.setString(5, dateAchieved);
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

        int expPoints = calculateJobExp(jobTitle, startDate, endDate, jobType);

        Credential jobExperience = new Credential(0, userId, null, null, null, null,
                null, 0, companyName, jobTitle,
                java.sql.Date.valueOf(Objects.requireNonNull(formatDate(startDate))),
                endDate.equalsIgnoreCase("Present") ? null : java.sql.Date.valueOf(Objects.requireNonNull(formatDate(endDate))),
                jobDescription);

        String insertJobExperienceQuery = "INSERT INTO job_experience (user_id, company_name, job_title, start_date, end_date, description) VALUES (?, ?, ?, STR_TO_DATE(?, '%m-%d-%Y'), IF(?, 'Present', STR_TO_DATE(?, '%m-%d-%Y')), ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertJobExperienceQuery)) {

            stmt.setInt(1, jobExperience.getUserId());
            stmt.setString(2, jobExperience.getCompanyName());
            stmt.setString(3, jobExperience.getJobTitle());
            stmt.setString(4, startDate);
            stmt.setString(5, endDate.equalsIgnoreCase("Present") ? null : endDate);
            stmt.setString(6, jobExperience.getJobDescription());

            stmt.executeUpdate();
            System.out.println("Job experience added successfully!");

        } catch (SQLException e) {
            System.err.println("Failed to add job experience: " + e.getMessage());
            throw e;
        }
    }

    private String formatDate(String date) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("MM-dd-yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = inputFormat.parse(date);
            return outputFormat.format(parsedDate);
        } catch (ParseException e) {
            System.err.println("Invalid date format.");
            return null;
        }
    }

    private int calculateJobExp(String jobTitle, String startDate, String endDate, String jobType) {
        int baseExp = 1000;

        double jobTypeMultiplier = 1.0;

        if (jobType.equals("internship")) {
            jobTypeMultiplier = 0.5;
        } else if (jobType.equals("part-time")) {
            jobTypeMultiplier = 0.75;
        }

        int experiencePoints = 0;

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
            Date start = sdf.parse(startDate);
            Date end = endDate.equalsIgnoreCase("Present") ? new Date() : sdf.parse(endDate);

            long durationInMillis = end.getTime() - start.getTime();
            long durationInMonths = durationInMillis / (1000L * 60 * 60 * 24 * 30);

            experiencePoints = (int) (baseExp * jobTypeMultiplier * durationInMonths);
            experiencePoints = (int) (experiencePoints * Math.pow(1.05, durationInMonths));

        } catch (ParseException e) {
            System.err.println("Invalid date format. Could not calculate experience points.");
        }

        return experiencePoints;
    }
}
