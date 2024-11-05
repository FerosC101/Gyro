package com.example;

import com.example.controller.UserController;
import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserController userController = new UserController();

        while (true) {
            System.out.println("Welcome to WANDAT");
            System.out.println("[1] View Global Server");
            System.out.println("[2] Login");
            System.out.println("[3] Register");
            System.out.println("[4] Exit");
            System.out.print("Enter choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            try {
                switch (choice) {
                    case 1:
                        System.out.println("Global Server view feature coming soon!");
                        break;

                    case 2:
                        System.out.print("Enter username: ");
                        String loginUsername = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String loginPassword = scanner.nextLine();

                        Integer userId = userController.login(loginUsername, loginPassword);

                        if (userId != null) {
                            System.out.println("Welcome back, " + loginUsername + "!");
                            System.out.println("[1] Add Achievement");
                            System.out.println("[2] Edit Stats");
                            System.out.println("[3] View Routines");
                            System.out.println("[4] View Account");
                            System.out.println("[5] Logout");
                            System.out.print("Choose an option: ");
                            int loginOption = scanner.nextInt();
                            scanner.nextLine();

                            if (loginOption == 1) {
                                System.out.print("Enter category (Local, Personal, National, International): ");
                                String category = scanner.nextLine();

                                System.out.print("Enter achievement name: ");
                                String achievement = scanner.nextLine();

                                System.out.print("Enter description: ");
                                String description = scanner.nextLine();

                                System.out.print("Enter date achieved (mm-dd-yyyy): ");
                                String dateAchieved = scanner.nextLine();

                                System.out.print("Enter any additional notes (optional): ");
                                String notes = scanner.nextLine();

                                userController.addAchievement(userId, achievement, description, category, dateAchieved, notes);

                            } else if (loginOption == 2) {
                                userController.editStats(userId);
                            }
                        } else {
                            System.out.println("Login failed. Please try again.");
                        }
                        break;
                    case 3:
                        System.out.print("Enter username: ");
                        String registerUsername = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String registerPassword = scanner.nextLine();

                        Integer newUserId = userController.register(registerUsername, registerPassword);

                        if (newUserId != null) {
                            System.out.println("Registration successful! Please provide additional information.");
                            userController.collectAdditionalInfo(newUserId);
                        } else {
                            System.out.println("Registration failed.");
                        }
                        break;

                    case 4:
                        System.out.println("Exiting... Goodbye!");
                        return;

                    default:
                        System.out.println("Invalid choice. Please try again.");
                        break;
                }
            } catch (SQLException e) {
                System.err.println("An error occurred: " + e.getMessage());
            }
        }
    }
}
