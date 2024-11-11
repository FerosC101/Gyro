package com.example;

import com.example.controller.UserController;

import java.sql.SQLException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        UserController userController = new UserController();

        while (true) {
            System.out.println("Welcome to Gyro");
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
                        System.out.println("Global Server view!");
                        break;

                    case 2:
                        System.out.print("Enter username: ");
                        String loginUsername = scanner.nextLine();
                        System.out.print("Enter password: ");
                        String loginPassword = scanner.nextLine();

                        Integer userId = userController.login(loginUsername, loginPassword);

                        if (userId != null) {
                            System.out.println("Welcome back, " + loginUsername + "!");
                            boolean loggedIn = true;

                            while (loggedIn) {
                                System.out.println("[1] Add Achievement");
                                System.out.println("[2] Add Job Experience");
                                System.out.println("[3] Edit Information");
                                System.out.println("[4] View Routines");
                                System.out.println("[5] View Account");
                                System.out.println("[6] Logout");
                                System.out.print("Choose an option: ");
                                int loginOption = scanner.nextInt();
                                scanner.nextLine();

                                switch (loginOption) {
                                    case 1:
                                        userController.addAchievement(userId);
                                        break;
                                    case 2:
                                        userController.addJobExperience(userId);
                                        break;
                                    case 3:
                                        userController.editUserInfo(userId);
                                    case 4:
                                        userController.displayDailyRoutines(userId);
                                        break;
                                    case 5:
                                        System.out.println("Viewing account (feature not yet implemented).");
                                        break;
                                    case 6:
                                        System.out.println("Logging out...");
                                        loggedIn = false;
                                        break;

                                    default:
                                        System.out.println("Invalid option. Please try again.");
                                        break;
                                }
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
                            userController.displayAdditionalQuestions(newUserId);
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
