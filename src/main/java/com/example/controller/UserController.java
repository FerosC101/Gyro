package com.example.controller;

import com.example.connection.UserDAO;
import com.example.model.Credential;
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
    private final Credential credential;

    public UserController() {
        this.credential = new Credential();
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

    public void startApplication() {
    }
}
