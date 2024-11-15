package com.example.connection;

import com.example.model.User;

import java.sql.*;



import static com.example.connection.DBConnection.getConnection;

public class UserDAO {
    private final User user = new User();

    public User getUserById(int userId) throws SQLException {
        String selectQuery = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(selectQuery)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Failed to get user by ID: " + e.getMessage());
            throw e;
        }
    }


    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setBirthday(rs.getDate("birthday"));
        user.setContactNumber(rs.getString("contact_number"));
        user.setEmail(rs.getString("email"));
        user.setAge(rs.getInt("age"));
        user.setHeight(rs.getInt("height"));
        user.setWeight(rs.getInt("weight"));
        user.setGender(rs.getString("gender"));
        user.setProfession(rs.getString("profession"));
        return user;
    }

    public void updateUserExp(int userId, int additionalExp) throws SQLException {
        int newExp = user.getExp() + additionalExp;
        user.setExp(newExp);
        String query = "UPDATE users SET exp = ? WHERE user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, newExp);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }

    public boolean deleteUserById(int userId) throws SQLException {
        String deleteDailyRoutines = "DELETE FROM user_daily_routines WHERE user_id = ?";
        String deleteUserDetails = "DELETE FROM user_details WHERE user_id = ?";
        String deleteUserCredentials = "DELETE FROM credentials WHERE user_id = ?";
        String deleteUserExperience = "DELETE FROM job_experience WHERE user_id = ?";
        String deleteUser = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtDailyRoutines = conn.prepareStatement(deleteDailyRoutines);
                 PreparedStatement pstmtUserDetails = conn.prepareStatement(deleteUserDetails);
                 PreparedStatement pstmtUserCredentials = conn.prepareStatement(deleteUserCredentials);
                 PreparedStatement pstmtUserExperience = conn.prepareStatement(deleteUserExperience);
                 PreparedStatement pstmtDeleteUser = conn.prepareStatement(deleteUser)) {

                pstmtDailyRoutines.setInt(1, userId);
                pstmtDailyRoutines.executeUpdate();

                pstmtUserDetails.setInt(1, userId);
                pstmtUserDetails.executeUpdate();

                pstmtUserCredentials.setInt(1, userId);
                pstmtUserCredentials.executeUpdate();

                pstmtUserExperience.setInt(1, userId);
                pstmtUserExperience.executeUpdate();

                pstmtDeleteUser.setInt(1, userId);
                int rowsDeleted = pstmtDeleteUser.executeUpdate();

                conn.commit();

                return rowsDeleted > 0;
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Failed to delete user and associated data: " + e.getMessage(), e);
            }
        }
    }

    public void viewAllUsers() throws SQLException {
        String query = "SELECT u.user_id, ud.full_name, u.exp, ud.profession FROM users u JOIN user_details ud ON u.user_id = ud.user_id";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String fullName = rs.getString("full_name");
                int exp = rs.getInt("exp");
                String profession = rs.getString("profession");

                System.out.println("User ID    : " + userId);
                System.out.println("Full Name  : " + fullName);
                System.out.println("Level      : " + calculateLevel(exp));
                System.out.println("Profession : " + profession);
                System.out.println("----------------------------");
            }
        }
    }

    private int calculateLevel(int exp) {
        int level = 0;
        int requiredExp = 1000;

        while (exp >= requiredExp) {
            exp -= requiredExp;
            level++;
            requiredExp += 250;
        }
        return level;
    }


}
