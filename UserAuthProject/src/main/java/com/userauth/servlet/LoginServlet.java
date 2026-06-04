package com.userauth.servlet;

// ============================================================
// FILE: LoginServlet.java
// PACKAGE: com.userauth.servlet
// PURPOSE: Validates login credentials from login.html.
//          Creates an HttpSession on success. Redirects to welcome.html.
// ============================================================

import com.userauth.database.DBConnection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------
    // doPost() — Called when user submits the login form
    // -------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- STEP 1: Read form data from login.html ---
        String username = request.getParameter("username").trim();
        String password = request.getParameter("password").trim();

        // --- STEP 2: Basic validation ---
        if (username.isEmpty() || password.isEmpty()) {
            response.sendRedirect("login.html?error=Username+and+password+are+required");
            return;
        }

        // --- STEP 3: Validate credentials against database ---
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();

            // Query: find user with matching username AND password
            // PreparedStatement prevents SQL Injection
            String sql = "SELECT id, name, username FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                // --- STEP 4: Credentials are CORRECT ---

                // Retrieve user details from the result
                int userId       = rs.getInt("id");
                String fullName  = rs.getString("name");
                String uname     = rs.getString("username");

                // --- STEP 5: Create an HTTP Session ---
                // getSession(true) creates a new session if one doesn't exist
                HttpSession session = request.getSession(true);

                // Store user info in session (accessible on any page)
                session.setAttribute("userId",   userId);
                session.setAttribute("username", uname);
                session.setAttribute("name",     fullName);

                // Session will expire after 30 minutes of inactivity
                session.setMaxInactiveInterval(30 * 60);

                // --- STEP 6: Redirect to Welcome page ---
                String redirectURL = "welcome.html"
                	    + "?name=" + java.net.URLEncoder.encode(fullName, "UTF-8")
                	    + "&username=" + java.net.URLEncoder.encode(uname, "UTF-8");
                	response.sendRedirect(redirectURL);

            } else {
                // --- Credentials are WRONG ---
                response.sendRedirect("login.html?error=Invalid+username+or+password");
            }

            rs.close();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("login.html?error=Database+error.+Please+try+again.");
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
