package com.userauth.database;

// ============================================================
// FILE: DBConnection.java
// PACKAGE: com.userauth.database
// PURPOSE: Handles JDBC connection to MySQL database.
//          All servlets import this class to get a Connection object.
// ============================================================

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // --- DATABASE CONFIGURATION CONSTANTS ---
    // Change these values to match your MySQL setup
    private static final String DB_URL      = "jdbc:mysql://localhost:3306/user_authentication";
    private static final String DB_USER     = "root";       // Your MySQL username
    private static final String DB_PASSWORD = "XXXXXX";       // Your MySQL password
    private static final String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";

    // --- getConnection() ---
    // Returns a live Connection object to the caller (servlet).
    // Throws SQLException if connection fails.
    public static Connection getConnection() throws SQLException {
        Connection conn = null;
        try {
            // Step 1: Load the MySQL JDBC driver class into memory
            Class.forName(DRIVER_CLASS);

            // Step 2: Ask DriverManager to create a connection using our URL/credentials
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

        } catch (ClassNotFoundException e) {
            // This error means the mysql-connector JAR is NOT in the Build Path
            System.err.println("JDBC Driver not found! Add mysql-connector-j JAR to Build Path.");
            e.printStackTrace();
            throw new SQLException("JDBC Driver missing: " + e.getMessage());
        }
        return conn;  // Return the open connection to the servlet
    }
}
