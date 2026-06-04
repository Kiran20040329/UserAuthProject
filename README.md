# UserAuthProject
🔐 A full-stack User Authentication System built with pure Java EE — Servlets, JDBC, MySQL &amp; Apache Tomcat. Features: registration with duplicate validation, secure login with HttpSession, forgot-password reset, and logout. Zero frameworks — just core Java backend fundamentals.
# 🔐 User Authentication System

> A full-stack web application built with **pure Java EE** — no frameworks, no shortcuts. Just core backend fundamentals.

![Java](https://img.shields.io/badge/Java-EE%2FJakarta-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.x-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Tomcat](https://img.shields.io/badge/Apache%20Tomcat-10.x-F8DC75?style=for-the-badge&logo=apachetomcat&logoColor=black)
![Eclipse](https://img.shields.io/badge/Eclipse-IDE-2C2255?style=for-the-badge&logo=eclipseide&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-green?style=for-the-badge)

---

## 📌 About the Project

This project is a secure, framework-free **User Authentication System** built using core Java EE technologies. It demonstrates how a real-world login system works under the hood — from HTML forms to Servlets to MySQL — without relying on Spring Boot, Hibernate, or any external framework.

Built as a learning project, it covers all essential backend concepts:
- JDBC database connectivity
- HTTP Session management
- PreparedStatement-based SQL injection prevention
- Server-side input validation
- MVC-style code organization

---

## ✨ Features

- ✅ **User Registration** — Collects Name, Username, Password, Mobile, Email & Gender
- ✅ **Duplicate Validation** — Blocks duplicate username, email, and mobile at registration
- ✅ **Secure Login** — Authenticates against MySQL with `HttpSession` creation
- ✅ **Forgot Password** — Reset via username, email, or mobile number
- ✅ **Logout** — Fully invalidates session with `session.invalidate()`
- ✅ **Welcome Page** — Displays logged-in user's name after successful login
- ✅ **SQL Injection Prevention** — `PreparedStatement` used throughout
- ✅ **Dual Validation** — Server-side (Servlet) + client-side (HTML5)

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java (Java EE / Jakarta EE) |
| Web Layer | Java Servlets (`@WebServlet`) |
| Database | MySQL 8.x |
| DB Access | JDBC with `PreparedStatement` |
| Server | Apache Tomcat 10.x |
| Frontend | HTML5 + CSS3 (no frameworks) |
| IDE | Eclipse IDE Enterprise Edition |

---

## 📁 Project Structure

```
UserAuthProject/
│
├── src/
│   ├── com/userauth/servlet/
│   │     ├── RegisterServlet.java        ← Registration logic & duplicate check
│   │     ├── LoginServlet.java           ← Login validation + HttpSession creation
│   │     ├── ForgotPasswordServlet.java  ← Password reset by username/email/mobile
│   │     └── LogoutServlet.java          ← Session invalidation & redirect
│   │
│   └── com/userauth/database/
│         └── DBConnection.java           ← JDBC utility — connection factory
│
├── WebContent/
│   ├── register.html                     ← Registration page
│   ├── login.html                        ← Login page
│   ├── forgotpassword.html               ← Forgot / Reset password page
│   ├── welcome.html                      ← Post-login landing page
│   └── style.css                         ← Shared CSS for all pages
│
├── WEB-INF/
│   └── web.xml                           ← Deployment descriptor
│
└── database_setup.sql                    ← Full DB + table creation script
```

---

## 🗄️ Database Schema

```sql
CREATE DATABASE IF NOT EXISTS user_authentication;
USE user_authentication;

CREATE TABLE users (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    mobile     VARCHAR(10)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    gender     VARCHAR(10)  NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 🚀 Getting Started

### Prerequisites

- Java JDK 11 or higher
- Eclipse IDE for Enterprise Java Developers
- Apache Tomcat 10.x
- MySQL 8.x + MySQL Workbench
- [MySQL Connector/J JAR](https://dev.mysql.com/downloads/connector/j/)

### Installation

**1. Clone the repository**
```bash
git clone https://github.com/your-username/UserAuthProject.git
```

**2. Import into Eclipse**
```
File → Import → Existing Projects into Workspace → Browse to cloned folder
```

**3. Set up the database**
```bash
mysql -u root -p < database_setup.sql
```
Or run `database_setup.sql` manually in MySQL Workbench.

**4. Configure database credentials**

Open `src/com/userauth/database/DBConnection.java` and update:
```java
private static final String DB_USER     = "root";     // ← your MySQL username
private static final String DB_PASSWORD = "root";     // ← your MySQL password
```

**5. Add MySQL Connector JAR**
```
Copy mysql-connector-j-X.X.X.jar → WebContent/WEB-INF/lib/
Right-click JAR → Build Path → Add to Build Path
```

**6. Run on Tomcat**
```
Right-click project → Run As → Run on Server → Select Tomcat 10.x → Finish
```

**7. Open in browser**
```
http://localhost:8080/UserAuthProject/
```

---

## 🔄 Application Flow

### Registration Flow
```
register.html → RegisterServlet
     │
     ├── Validate all fields (server-side)
     ├── Check duplicate: username / email / mobile
     ├── If clean → INSERT INTO users (PreparedStatement)
     └── Redirect → login.html ✅
```
<img width="1366" height="733" alt="Screenshot (14)" src="https://github.com/user-attachments/assets/2729438f-aad7-4e35-9d70-2641d2d38654" />

### Login Flow
```
login.html → LoginServlet
     │
     ├── SELECT * FROM users WHERE username=? AND password=?
     ├── If found → create HttpSession → store name, username, userId
     │             Redirect → welcome.html ✅
     └── If not found → redirect → login.html with error ❌
```
<img width="1366" height="729" alt="Screenshot (13)" src="https://github.com/user-attachments/assets/e37996b6-9a28-4732-9cc0-510194d2c727" />

### Forgot Password Flow
```
forgotpassword.html → ForgotPasswordServlet
     │
     ├── SELECT FROM users WHERE username=? OR email=? OR mobile=?
     ├── If found → UPDATE users SET password=? WHERE username=?
     └── Redirect → login.html ✅
```
<img width="1366" height="717" alt="Screenshot (16)" src="https://github.com/user-attachments/assets/576ef29c-ccdc-496f-9065-87340e8f1e8a" />

### Logout Flow
```
Logout button → LogoutServlet
     │
     ├── session.invalidate()   ← destroys all session attributes
     └── Redirect → login.html ✅
```
<img width="1366" height="725" alt="Screenshot (15)" src="https://github.com/user-attachments/assets/2dc21d4b-9576-4607-9262-eafb77588e74" />

---

## ✅ Validation Rules

| Field | Rule |
|---|---|
| Name | Cannot be empty, max 100 characters |
| Username | Cannot be empty, must be unique in database |
| Password | Minimum 6 characters |
| Mobile | Exactly 10 digits, must be unique |
| Email | Must match standard email format, must be unique |
| Gender | Must select one: Male / Female / Others |

---

## 🔒 Security Features

| Concern | Implementation |
|---|---|
| SQL Injection | `PreparedStatement` with `?` placeholders everywhere |
| Session Hijacking | Session created only on successful auth; expires in 30 min |
| Duplicate Accounts | SQL `UNIQUE` constraints + Java-side pre-insert checks |
| Input Tampering | Server-side validation in every servlet regardless of client |
| Logout Security | `session.invalidate()` fully destroys the session object |

> ⚠️ **Note for Production:** This project stores passwords as plain text for simplicity. In a production system, always hash passwords using **BCrypt** before storing.

---

## 🐛 Common Errors & Fixes

| Error | Fix |
|---|---|
| `HTTP 404 — Servlet not found` | Verify `@WebServlet("/RegisterServlet")` matches `action="RegisterServlet"` in HTML |
| `ClassNotFoundException: com.mysql.cj.jdbc.Driver` | Add `mysql-connector-j.jar` to `WEB-INF/lib/` and Build Path |
| `Access denied for user 'root'@'localhost'` | Update `DB_USER` / `DB_PASSWORD` in `DBConnection.java` |
| `Port 8080 already in use` | Change Tomcat port to `8090` in `conf/server.xml` |
| `Communications link failure` | Start MySQL service — `net start MySQL` (Win) or `sudo service mysql start` (Linux) |
| `Cannot GET /UserAuthProject/` | Check `web.xml` welcome-file list and project deployment in Tomcat |

---

## 📂 File Reference

| File | Purpose |
|---|---|
| `DBConnection.java` | Loads JDBC driver, returns `Connection` object to callers |
| `RegisterServlet.java` | Reads form data, validates, checks duplicates, inserts user |
| `LoginServlet.java` | Queries DB, creates `HttpSession` on success, redirects |
| `ForgotPasswordServlet.java` | Finds user by username/email/mobile, updates password |
| `LogoutServlet.java` | Calls `session.invalidate()`, redirects to login page |
| `register.html` | Registration form — Name, Username, Password, Mobile, Email, Gender |
| `login.html` | Login form with Forgot Password link |
| `forgotpassword.html` | Reset form — accepts username OR email OR mobile |
| `welcome.html` | Displays logged-in user's name with Logout button |
| `style.css` | Shared professional CSS styling for all pages |
| `web.xml` | Deployment descriptor — sets `login.html` as welcome page |
| `database_setup.sql` | Full SQL: create database, create table, sample data |

---

## 🔮 Future Enhancements

- [ ] 🔐 **Password Hashing** — BCrypt integration for secure password storage
- [ ] 📧 **Email Verification** — OTP via JavaMail API after registration
- [ ] 📱 **OTP Login** — Mobile OTP via SMS API (Twilio / MSG91)
- [ ] 👨‍💼 **Admin Panel** — Role-based access with admin dashboard
- [ ] 👤 **User Profile Page** — View and edit profile after login
- [ ] ⚡ **Connection Pooling** — Apache DBCP or HikariCP for better performance
- [ ] 🍪 **Remember Me** — Persistent login across browser sessions with cookies

---

## 🎓 Interview Questions

<details>
<summary><b>Q1: What is a Servlet?</b></summary>
A Servlet is a Java class that runs on a web server (like Tomcat) and handles HTTP requests and responses. It extends <code>HttpServlet</code> and overrides <code>doGet()</code> or <code>doPost()</code>.
</details>

<details>
<summary><b>Q2: Why use PreparedStatement over Statement?</b></summary>
PreparedStatement is precompiled, faster for repeated execution, and most importantly — it prevents SQL Injection by treating all user input as data, never as executable SQL code.
</details>

<details>
<summary><b>Q3: What does session.invalidate() do?</b></summary>
It destroys the <code>HttpSession</code> object and removes all attributes stored in it (userId, username, name). After this, the user is no longer recognized as logged in.
</details>

<details>
<summary><b>Q4: What are the steps to connect Java to MySQL using JDBC?</b></summary>
1. Load driver: <code>Class.forName("com.mysql.cj.jdbc.Driver")</code><br>
2. Get connection: <code>DriverManager.getConnection(url, user, pass)</code><br>
3. Create statement: <code>conn.prepareStatement(sql)</code><br>
4. Execute query: <code>ps.executeQuery()</code> or <code>ps.executeUpdate()</code><br>
5. Process <code>ResultSet</code><br>
6. Close all resources in <code>finally</code> block
</details>

<details>
<summary><b>Q5: What is the purpose of web.xml?</b></summary>
It is the Deployment Descriptor — it tells Tomcat which file to serve as the default page (welcome-file), defines servlet mappings, filters, error pages, and other application-level configuration.
</details>

<details>
<summary><b>Q6: What is MVC in this project?</b></summary>
<b>Model</b> = DBConnection.java + SQL queries (data layer)<br>
<b>View</b> = HTML pages (presentation layer)<br>
<b>Controller</b> = Servlet classes (process request, coordinate model and view)
</details>

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

---

## 🙋‍♂️ Author

**Your Name**
- GitHub: [@KIRAN KUMAR](https://github.com/Kiran20040329)
- LinkedIn: [KIRAN KUMAR](https://www.linkedin.com/in/kiran-kumar-madam-93a837277/)

---

> ⭐ If this project helped you learn, consider giving it a **star** on GitHub!
