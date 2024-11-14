package com.example.connection;

import com.example.model.Credential;

import java.sql.*;

public class CredentialDAO {

    public void addCredential(Credential credential) throws SQLException {
        String query = "INSERT INTO credentials (user_id, achievement_name, description, category, date_achieved, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, credential.getUserId());
            stmt.setString(2, credential.getAchievementName());
            stmt.setString(3, credential.getDescription());
            stmt.setString(4, credential.getCategory());
            stmt.setDate(5, credential.getDateAchieved());
            stmt.setString(6, credential.getNotes());
            stmt.executeUpdate();
        }
    }

    public void addJobExperience(Credential credential) throws SQLException {
        String query = "INSERT INTO job_experience (user_id, company_name, job_title, start_date, end_date, description, job_type) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setLong(1, credential.getUserId());
            stmt.setString(2, credential.getCompanyName());
            stmt.setString(3, credential.getJobTitle());
            stmt.setDate(4, credential.getStartDate());
            stmt.setDate(5, credential.getEndDate());
            stmt.setString(6, credential.getJobDescription());
            stmt.setString(7, credential.getJobType());
            stmt.executeUpdate();
        }
    }
}

