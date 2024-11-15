package com.example.controller;

import com.example.service.AdminService;
import com.example.service.UserService;
import com.example.service.AccountService;
import com.example.view.Menu;

import java.sql.SQLException;
import java.util.Scanner;

public class UserController {
    private AccountService userService;
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

        userService = new UserService();
        Integer userId = userService.register(username, password);
        if (userId != null) {
            System.out.println("\n==== Registration successful! Please provide additional information. ====\n");
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

        if ("admin".equals(username) && "admin123".equals(password)) {
            System.out.println("Admin login successful!");
            userService = new AdminService();
            adminMenu(scanner);
        } else {
            userService = new UserService();
            Integer userId = userService.login(username, password);
            if (userId != null) {
                System.out.println("Welcome back, " + username + "!");
                userService.manageUserSession(userId, scanner);
            } else {
                System.out.println("Login failed. Please try again.");
            }
        }
    }

    private void adminMenu(Scanner scanner) throws SQLException {
        if (userService instanceof AdminService adminService) {
            boolean loggedIn = true;
            while (loggedIn) {
                System.out.println("\n==== Admin Menu ====");
                System.out.println("[1] View All Users");
                System.out.println("[2] Delete User");
                System.out.println("[3] Logout");
                System.out.print("Choose an option: ");
                int option = scanner.nextInt();

                switch (option) {
                    case 1 -> adminService.viewAllUsers();
                    case 2 -> {
                        System.out.print("Enter User ID to delete: ");
                        int userId = scanner.nextInt();
                        adminService.deleteUser(userId);
                    }
                    case 3 -> {
                        System.out.println("Logging out...");
                        loggedIn = false;
                    }
                    default -> System.out.println("Invalid option. Please try again.");
                }
            }
        } else {
            System.out.println("Access denied: Admin privileges are required to access this menu.");
        }
    }
}
