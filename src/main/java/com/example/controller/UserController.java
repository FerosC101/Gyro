package com.example.controller;

import com.example.service.UserService;
import com.example.view.Menu;

import java.sql.SQLException;
import java.util.Scanner;

public class UserController {
    private final UserService userService = new UserService();
    private final CredentialController credentialController = new CredentialController();
    private final ViewController viewController = new ViewController();

    public void startApplication() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            Menu.displayMainMenu();
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1 -> viewController.viewAllUsers();
                    case 2 -> loginUser(scanner);
                    case 3 -> registerUser(scanner);
                    case 4 -> {
                        System.out.println("Exiting... Goodbye!");
                        return;
                    }
                    default -> System.out.println("Invalid choice. Please try again.");
                }
            } catch (SQLException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }

    private void registerUser(Scanner scanner) throws SQLException {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Integer userId = userService.register(username, password);
        if (userId != null) {
            System.out.println("\n====Registration successful! Please provide additional information.====\n");
            viewController.displayAdditionalQuestions(userId);
        } else {
            System.out.println("Registration failed.");
        }
    }

    private void loginUser(Scanner scanner) throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Integer userId = userService.login(username, password);
        if (userId != null) {
            System.out.println("Welcome back, " + username + "!");
            userService.manageUserSession(userId, scanner);
        } else {
            System.out.println("Login failed. Please try again.");
        }
    }
}
