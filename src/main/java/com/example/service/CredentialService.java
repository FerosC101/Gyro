package com.example.service;

import com.example.connection.CredentialDAO;
import com.example.connection.UserDAO;
import com.example.model.Credential;

import java.sql.Date;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class CredentialService {

    private final Scanner scanner = new Scanner(System.in);
    private final CredentialDAO credentialDAO = new CredentialDAO();
    private final UserDAO userDAO = new UserDAO();

    public void addAchievement(int userId) throws SQLException {
        System.out.println("\n==== Add a New Achievement ====\n");
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

        credentialDAO.addCredential(achievement);
        int experiencePoints = calculateAchievementExp(category);
        userDAO.updateUserExp(userId, experiencePoints);

        System.out.println("\n==== Achievement Added Successfully! ====\n");
    }

    private int calculateAchievementExp(String category) {
        switch (category.toLowerCase()) {
            case "personal": return 1000;
            case "local": return 5000;
            case "national": return 20000;
            case "international": return 50000;
            default:
                System.err.println("\n====Invalid category. No experience points awarded.====\n");
                return 0;
        }
    }

    public void addJobExperience(int userId) throws SQLException {
        System.out.println("\n==== Add Job Experience ====\n");
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
            System.err.println("Invalid date format. Job experience not added.\n");
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

        credentialDAO.addJobExperience(jobExperience);
        int experiencePoints = calculateJobExp(userId, sqlStartDate, sqlEndDate, jobType);
        userDAO.updateUserExp(userId, experiencePoints);

        System.out.println("\n==== Job Experience Added Successfully! ====");
        System.out.println("Job Experience at " + companyName + " (" + jobTitle + ") has been added.\n");
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

    private int calculateJobExp(int userId, Date sqlStartDate, Date sqlEndDate, String jobType) {
        int baseExp = 1000;
        double jobTypeMultiplier = switch (jobType) {
            case "internship" -> 0.3;
            case "part-time" -> 0.6;
            default -> 1.0;
        };

        if (sqlEndDate == null) {
            sqlEndDate = new Date(System.currentTimeMillis()); // Use current date if "Present"
        }

        long durationInMonths = (sqlEndDate.getTime() - sqlStartDate.getTime()) / (1000L * 60 * 60 * 24 * 30);
        return (int) (baseExp * jobTypeMultiplier * durationInMonths);
    }
}
