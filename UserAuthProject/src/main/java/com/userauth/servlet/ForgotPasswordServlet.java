package com.userauth.servlet;

// ============================================================
// FILE: ForgotPasswordServlet.java
// PACKAGE: com.userauth.servlet
// PURPOSE: Handles the forgot password flow.
//          User provides username/email/mobile + new password.
//          If the user exists, password is updated in the database.
// ============================================================

import com.userauth.database.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/ForgotPasswordServlet")
public class ForgotPasswordServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------
    // doPost() — Called when user submits forgotpassword.html form
    // -------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- STEP 1: Read form inputs ---
        // User can provide any ONE of: username, email, or mobile
        String identifier   = request.getParameter("identifier").trim();  // username/email/mobile
        String newPassword  = request.getParameter("newPassword").trim();
        String confirmPass  = request.getParameter("confirmPassword").trim();

        // --- STEP 2: Validate inputs ---
        if (identifier.isEmpty() || newPassword.isEmpty() || confirmPass.isEmpty()) {
            response.sendRedirect("forgotpassword.html?error=All+fields+are+required");
            return;
        }
        if (newPassword.length() < 6) {
            response.sendRedirect("forgotpassword.html?error=Password+must+be+at+least+6+characters");
            return;
        }
        if (!newPassword.equals(confirmPass)) {
            response.sendRedirect("forgotpassword.html?error=Passwords+do+not+match");
            return;
        }

        // --- STEP 3: Find user in database ---
        // We search by username OR email OR mobile — whichever the user entered
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // This query checks all three fields at once
            String findSQL = "SELECT id, username FROM users WHERE username = ? OR email = ? OR mobile = ?";
            PreparedStatement findPs = conn.prepareStatement(findSQL);
            findPs.setString(1, identifier);
            findPs.setString(2, identifier);
            findPs.setString(3, identifier);

            ResultSet rs = findPs.executeQuery();

            if (rs.next()) {
                // --- STEP 4: User found — update password ---
                String foundUsername = rs.getString("username");

                // UPDATE query with PreparedStatement (prevents SQL injection)
                String updateSQL = "UPDATE users SET password = ? WHERE username = ?";
                PreparedStatement updatePs = conn.prepareStatement(updateSQL);
                updatePs.setString(1, newPassword);   // NOTE: hash in production
                updatePs.setString(2, foundUsername);

                int rows = updatePs.executeUpdate();

                updatePs.close();

                if (rows > 0) {
                    // Password updated successfully
                    response.sendRedirect("login.html?msg=Password+updated+successfully!+Please+login.");
                } else {
                    response.sendRedirect("forgotpassword.html?error=Password+update+failed.+Try+again.");
                }

            } else {
                // --- User not found ---
                response.sendRedirect("forgotpassword.html?error=No+account+found+with+that+username,+email,+or+mobile");
            }

            rs.close();
            findPs.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("forgotpassword.html?error=Database+error:+" + e.getMessage());
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
