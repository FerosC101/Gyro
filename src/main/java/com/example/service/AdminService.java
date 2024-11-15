package com.example.service;

import com.example.connection.UserDAO;

import java.sql.SQLException;
import java.util.Scanner;

public class AdminService extends AccountService {

    private final UserDAO userDAO = new UserDAO();

    @Override
    public void manageUserSession(int userId, Scanner scanner) throws SQLException {
        System.out.println("Admin user session management.");
        boolean loggedIn = true;

        while (loggedIn) {
            System.out.println("[1] View All Users");
            System.out.println("[2] Remove User");
            System.out.println("[3] Logout");
            System.out.print("Choose an option: ");
            int option = scanner.nextInt();

            switch (option) {
                case 1 -> viewAllUsers();
                case 2 -> deleteUser(userId);
                case 3 -> {
                    System.out.println("Logging out...");
                    loggedIn = false;
                }
                default -> System.out.println("Invalid option. Please try again.");
            }
        }
    }

    public void deleteUser(int userId) throws SQLException {
        boolean deleted = userDAO.deleteUserById(userId);
        if (deleted) {
            System.out.println("User with ID " + userId + " has been deleted successfully.");
        } else {
            System.out.println("User with ID " + userId + " not found.");
        }
    }

    public void viewAllUsers() throws SQLException {
        System.out.println("\n========== All Users ==========");
        userDAO.viewAllUsers();
        System.out.println("===============================");
    }
}
