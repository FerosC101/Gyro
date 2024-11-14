package com.example.controller;

import com.example.service.CredentialService;

import java.sql.SQLException;

public class CredentialController {
    private final CredentialService credentialService = new CredentialService();

    public void addAchievement(int userId) throws SQLException {
        credentialService.addAchievement(userId);
    }

    public void addJobExperience(int userId) throws SQLException {
        credentialService.addJobExperience(userId);
    }
}
