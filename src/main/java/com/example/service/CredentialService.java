package com.example.service;

import com.example.connection.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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

    public void addJobExperience(int userId) throws SQLException {
        Scanner scanner = new Scanner(System.in);

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

        String insertJobExperienceQuery = "INSERT INTO job_experience (user_id, company_name, job_title, start_date, end_date, description, exp_points) VALUES (?, ?, ?, STR_TO_DATE(?, '%m-%d-%Y'), IF(?, 'Present', STR_TO_DATE(?, '%m-%d-%Y')), ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertJobExperienceQuery)) {

            stmt.setInt(1, userId);
            stmt.setString(2, companyName);
            stmt.setString(3, jobTitle);
            stmt.setString(4, startDate);
            stmt.setString(5, endDate);
            stmt.setString(6, jobDescription);
            stmt.setInt(7, expPoints);

            stmt.executeUpdate();
            System.out.println("Job experience added successfully!");

        } catch (SQLException e) {
            System.err.println("Failed to add job experience: " + e.getMessage());
            throw e;
        }
    }
    // do not ask (hehe)
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
            long durationInMonths = durationInMillis / (1000L * 60 * 60 * 24 * 30); // do not ask

            experiencePoints = (int) (baseExp * jobTypeMultiplier * durationInMonths); // do not ask

            experiencePoints = (int) (experiencePoints * Math.pow(1.05, durationInMonths));

        } catch (ParseException e) {
            System.err.println("Invalid date format. Could not calculate experience points.");
        }

        return experiencePoints;
    }
}
