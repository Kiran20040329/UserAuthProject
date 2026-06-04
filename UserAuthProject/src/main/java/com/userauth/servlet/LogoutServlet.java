package com.userauth.servlet;

// ============================================================
// FILE: LogoutServlet.java
// PACKAGE: com.userauth.servlet
// PURPOSE: Handles logout.
//          Invalidates the current HTTP session so no one can
//          access protected pages after logout.
//          Redirects user back to login.html.
// ============================================================

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // -------------------------------------------------------
    // doGet() — Called when user clicks the Logout button
    // The logout link/button should point to: LogoutServlet
    // -------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // --- STEP 1: Get the existing session (do NOT create a new one) ---
        // getSession(false) returns null if no session exists — safe approach
        HttpSession session = request.getSession(false);

        if (session != null) {
            // --- STEP 2: Invalidate (destroy) the session ---
            // This removes all session attributes (userId, username, name, etc.)
            // The user will no longer be recognized as logged in
            session.invalidate();
        }

        // --- STEP 3: Redirect to login page with a logout confirmation message ---
        response.sendRedirect("login.html?msg=You+have+been+logged+out+successfully.");
    }
}
