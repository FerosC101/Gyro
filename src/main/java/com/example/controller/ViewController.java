package com.example.controller;

import com.example.service.UserService;

import java.sql.SQLException;

public class ViewController {
    private final UserService userService =  new UserService();

    public void viewAllUsers() throws SQLException {
        userService.viewGlobalServer();
    }

    public void displayAdditionalQuestions(int userId) throws SQLException {
        userService.displayAdditionalQuestions(userId);
    }
}
