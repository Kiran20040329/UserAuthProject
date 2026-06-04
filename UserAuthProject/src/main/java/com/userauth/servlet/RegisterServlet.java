package com.userauth.servlet;

// ============================================================
// FILE: RegisterServlet.java
// PACKAGE: com.userauth.servlet
// PURPOSE: Handles the registration form submission from register.html.
//          Validates input, checks for duplicates, inserts new user.
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

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------
    // doPost() is called when the HTML form is submitted.
    // HTML form must use method="post" and action="RegisterServlet"
    // -------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- STEP 1: Read form data sent from register.html ---
        String name     = request.getParameter("name").trim();
        String username = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();
        String mobile   = request.getParameter("mobile").trim();
        String email    = request.getParameter("email").trim();
        String gender   = request.getParameter("gender");

        // --- STEP 2: Server-side validation ---
        // Even though HTML has validation, we re-validate here for security

        if (name.isEmpty() || username.isEmpty() || password.isEmpty()
                || mobile.isEmpty() || email.isEmpty() || gender == null) {
            // Redirect back with an error message
            response.sendRedirect("register.html?error=All+fields+are+required");
            return;
        }
        if (password.length() < 6) {
            response.sendRedirect("register.html?error=Password+must+be+at+least+6+characters");
            return;
        }
        if (!mobile.matches("\\d{10}")) {
            response.sendRedirect("register.html?error=Mobile+must+be+exactly+10+digits");
            return;
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            response.sendRedirect("register.html?error=Invalid+email+format");
            return;
        }

        // --- STEP 3: Check for duplicates in database ---
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // Check duplicate username
            if (isDuplicate(conn, "username", username)) {
                response.sendRedirect("register.html?error=Username+already+exists");
                return;
            }
            // Check duplicate email
            if (isDuplicate(conn, "email", email)) {
                response.sendRedirect("register.html?error=Email+already+registered");
                return;
            }
            // Check duplicate mobile
            if (isDuplicate(conn, "mobile", mobile)) {
                response.sendRedirect("register.html?error=Mobile+number+already+registered");
                return;
            }

            // --- STEP 4: Insert new user into database ---
            // Using PreparedStatement prevents SQL Injection attacks
            String insertSQL = "INSERT INTO users (name, username, password, mobile, email, gender) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertSQL);
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, password);   // NOTE: In production, hash password with BCrypt
            ps.setString(4, mobile);
            ps.setString(5, email);
            ps.setString(6, gender);

            int rowsInserted = ps.executeUpdate();  // Returns number of rows inserted

            if (rowsInserted > 0) {
                // Registration successful — redirect to login page
                response.sendRedirect("login.html?msg=Registration+successful!+Please+login.");
            } else {
                response.sendRedirect("register.html?error=Registration+failed.+Try+again.");
            }

            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("register.html?error=Database+error:+" + e.getMessage());
        } finally {
            // Always close the connection to prevent resource leaks
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // -------------------------------------------------------
    // Helper method: checks if a value already exists in a column
    // column: "username", "email", or "mobile"
    // value:  the value to search for
    // Returns true if duplicate found
    // -------------------------------------------------------
    private boolean isDuplicate(Connection conn, String column, String value) throws SQLException {
        String sql = "SELECT id FROM users WHERE " + column + " = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, value);
        ResultSet rs = ps.executeQuery();
        boolean exists = rs.next();   // true if any row was returned
        rs.close();
        ps.close();
        return exists;
    }
}
