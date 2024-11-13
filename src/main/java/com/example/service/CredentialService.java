package com.example.service;

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
        System.out.println("==== Add a New Achievement ====");
        System.out.println("Please provide the details of the achievement.");

        System.out.print("\nCategory (Local, Personal, National, International): ");
        String category = scanner.nextLine().trim();

        System.out.print("\nAchievement Name: ");
        String achievementName = scanner.nextLine().trim();

        System.out.print("\nDescription: ");
        String description = scanner.nextLine().trim();

        System.out.print("\nDate Achieved (MM-DD-YYYY): ");
        String dateAchieved = scanner.nextLine().trim();

        System.out.print("\nAdditional Notes (optional): ");
        String notes = scanner.nextLine().trim();

        Date sqlDateAchieved = parseDate(dateAchieved);

        if (sqlDateAchieved == null) {
            System.err.println("Invalid date format. Achievement not added.");
            return;
        }

        Credential achievement = new Credential();
        achievement.setUserId(userId);
        achievement.setAchievementName(achievementName);
        achievement.setDescription(description);
        achievement.setCategory(category);
        achievement.setDateAchieved(sqlDateAchieved);
        achievement.setNotes(notes);

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

            int experiencePoints = calculateAchievementExp(category);

            UserController userController = new UserController();
            userController.updateUserExp(userId, experiencePoints);

            System.out.println("\n==== Achievement Added Successfully! ====");
        } catch (SQLException e) {
            System.err.println("\nFailed to add achievement: " + e.getMessage());
            throw e;
        }
    }

    private int calculateAchievementExp(String category) {
        int expPoints = 0;
        switch (category.toLowerCase()) {
            case "personal":
                expPoints = 1000;
                break;
            case "local":
                expPoints = 5000;
                break;
            case "national":
                expPoints = 20000;
                break;
            case "international":
                expPoints = 50000;
                break;
            default:
                System.err.println("Invalid category. No experience points awarded.");
        }
        return expPoints;
    }


    public void addJobExperience(int userId) throws SQLException {
        System.out.println("==== Add Job Experience ====");
        System.out.println("Please enter the details of your job experience below.");

        System.out.print("\nCompany Name: ");
        String companyName = scanner.nextLine().trim();

        System.out.print("\nJob Title/Role: ");
        String jobTitle = scanner.nextLine().trim();

        System.out.print("\nStart Date (MM-DD-YYYY): ");
        String startDate = scanner.nextLine().trim();

        System.out.print("\nEnd Date (MM-DD-YYYY or 'Present' if ongoing): ");
        String endDate = scanner.nextLine().trim();

        System.out.print("\nJob Description: ");
        String jobDescription = scanner.nextLine().trim();

        System.out.print("\nJob Type (Internship, Full-time, or Part-time): ");
        String jobType = scanner.nextLine().trim().toLowerCase();

        Date sqlStartDate = parseDate(startDate);
        Date sqlEndDate = endDate.equalsIgnoreCase("Present") ? null : parseDate(endDate);

        if (sqlStartDate == null || (!endDate.equalsIgnoreCase("Present") && sqlEndDate == null)) {
            System.err.println("Invalid date format. Job experience not added.");
            return;
        }

        Credential jobExperience = new Credential();
        jobExperience.setUserId(userId);
        jobExperience.setCompanyName(companyName);
        jobExperience.setJobTitle(jobTitle);
        jobExperience.setStartDate(sqlStartDate);
        jobExperience.setEndDate(sqlEndDate);
        jobExperience.setJobDescription(jobDescription);
        jobExperience.setJobType(jobType);

        String insertJobExperienceQuery = "INSERT INTO job_experience (user_id, company_name, job_title, start_date, end_date, description, job_type) VALUES (?, ?, ?, ?, ?, ?, ?)";

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
            stmt.setString(7, jobExperience.getJobType());

            stmt.executeUpdate();

            calculateJobExp(userId, jobTitle, sqlStartDate, sqlEndDate, jobType);

            System.out.println("\n==== Job Experience Added Successfully! ====");
            System.out.println("Job Experience at " + companyName + " (" + jobTitle + ") has been added.");
            System.out.println("Duration: " + startDate + " to " + (endDate.equalsIgnoreCase("Present") ? "Present" : endDate + "\n"));
        } catch (SQLException e) {
            System.err.println("\nFailed to add job experience: " + e.getMessage());
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

    private void calculateJobExp(int userId, String jobTitle, Date sqlStartDate, Date sqlEndDate, String jobType) {
        int baseExp = 1000;
        double jobTypeMultiplier = 1.0;

        if (jobType.equalsIgnoreCase("internship")) {
            jobTypeMultiplier = 0.3;
        } else if (jobType.equalsIgnoreCase("part-time")) {
            jobTypeMultiplier = 0.6;
        }

        int experiencePoints = 0;

        long durationInMonths;
        if (sqlEndDate == null) {
            sqlEndDate = new java.sql.Date(new java.util.Date().getTime()); // Use current date if "Present"
        }

        durationInMonths = (sqlEndDate.getTime() - sqlStartDate.getTime()) / (1000L * 60 * 60 * 24 * 30);

        experiencePoints = (int) (baseExp * jobTypeMultiplier * durationInMonths);

        try {
            UserController userController = new UserController();
            userController.updateUserExp(userId, experiencePoints);
            System.out.println("Experience points updated successfully! " + experiencePoints + " points added.");
        } catch (SQLException e) {
            System.err.println("Failed to update experience points: " + e.getMessage());
        }
    }

}
