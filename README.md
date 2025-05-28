          ---------------------Store Management System--------------------------

----Overview
The Store Management System is a Java-based desktop application built with Swing for managing store operations, including inventory, sales, customers, reports, and admin accounts. It features a user-friendly GUI with gradient headers, external icons, and secure password handling using BCrypt. The system supports two user roles: Admin (full access) and Non-Admin (limited access). Key security features include hashed passwords and password verification for sensitive actions like admin deletion.
This README provides new developers with an understanding of the system's functionality, styling, setup, and code structure, assuming development in NetBeans IDE.
Prerequisites

Java Development Kit (JDK): Version 8 or higher.
NetBeans IDE: Version 12 or later recommended.
MySQL Database: For storing admin and other data.
BCrypt Library: jbcrypt-0.4.jar for password hashing.
Icon Files: PNG images for buttons (e.g., login.png, inventory.png).
MySQL Connector/J: For database connectivity.

----Setup Instructions

Clone the Project:

Import the project into NetBeans: File > Open Project.
Ensure the project is a Java Application or Maven project.


----Add BCrypt Library:

Non-Maven Project:
Download jbcrypt-0.4.jar from Maven Central.
Right-click project > Properties > Libraries > Add JAR/Folder > Select jbcrypt-0.4.jar.


----Maven Project:
Add to pom.xml:<dependency>
    <groupId>org.mindrot</groupId>
    <artifactId>jbcrypt</artifactId>
    <version>0.4</version>
</dependency>


Reload project: Right-click project > Reload Project.




------Set Up MySQL Database:

Import sql to your local XAMPP.

Ensure the password column is VARCHAR(60) for BCrypt hashes.
Update existing passwords to BCrypt hashes using:import org.mindrot.jbcrypt.BCrypt;
String hashedPassword = BCrypt.hashpw("plainPassword", BCrypt.gensalt());
// Update database with hashedPassword


Configure DatabaseConnection.java with your MySQL credentials (URL, username, password).


----Add Icon Files:

Place PNG icons in src/resources/icons/:
login.png, exit.png (LoginFrame)
inventory.png, sales.png, customer.png, report.png, admin.png, contact.png, logout.png (DashboardFrame)


In NetBeans, create the folder: Source Packages > New > Folder > resources/icons.
Copy icons into this folder via file explorer or NetBeans.


----Build and Run:

Clean and build: Run > Clean and Build Project (Shift+F11).
Run: Run > Run Project (F6).
Test login with a valid username and hashed password from the admins table.



----Key Features

Login (LoginFrame.java):

Functionality:
Users enter a username and password.
Passwords are verified against BCrypt hashes in the admins table.
On successful login, navigates to DashboardFrame with the user’s role (Admin or Non-Admin) and username.


Security:
Uses BCrypt for password hashing (BCrypt.checkpw).
Prevents empty credentials with validation.


Styling:
Black background with a gradient header (blue #0066CC to black).
External icons for Login (login.png) and Exit (exit.png) buttons.
Green Login button (#00CC00) with hover (#33FF33), red Exit button (#FF3333) with hover (#FF6666).
Bold Arial fonts (32pt for header, 16pt for buttons).




-----Dashboard (DashboardFrame.java):

Functionality:
Displays buttons for: Inventory, Sales, Customer, Reporting, Admin Management (admin-only), Contact Us, and Logout.
Navigates to respective frames or logs out to LoginFrame.
Passes the logged-in username to AdminFrame.


Styling:
Dark gray background with a gradient header (blue #0066CC to dark gray).
External icons for all buttons (e.g., inventory.png, admin.png).
Blue buttons (#0099FF) with hover (#33B5FF), white text, white borders.
Bold Arial fonts (28pt for header, 16pt for buttons).




Admin Management (AdminFrame.java):

Functionality:
Displays a table of admins (ID, SSN, Name, Username, Email, Password, Role, Contact).
Supports Add, Edit, and Delete operations:
Add: Inputs new admin details, hashes password with BCrypt, and inserts into the database.
Edit: Updates admin details, hashes new password (if provided) with BCrypt, or retains existing hash.
Delete: Requires admin password verification before deletion (BCrypt-checked).


Returns to DashboardFrame via Return to Dashboard button.


Security:
Passwords are hashed with BCrypt (BCrypt.hashpw) for add/edit.
Deletion requires the admin to enter their password, verified against the stored hash.


Styling:
Orange background (#FFA500) with a gradient header (blue #0066CC to black).
Gray table with white text for contrast.
Blue buttons (Add, Edit, Return) and red Delete button, all with white text.
Bold Arial fonts (24pt for header).





Code Structure

Package: com.store.gui
LoginFrame.java: Login screen with authentication.
DashboardFrame.java: Main menu with navigation.
AdminFrame.java: Admin management (CRUD operations).
Other frames (not detailed): InventoryFrame, SalesFrame, CustomerFrame, ReportFrame, ContactUsFrame.


Package: com.store.db
DatabaseConnection.java: Manages MySQL connection (update with your credentials).


Resources: src/resources/icons/ for PNG icon files.

Styling Details

Consistent UI:
Gradient Headers: Blue-to-dark/gray/black gradients using GradientPaint for a modern look.
External Icons: 24x24px PNGs loaded from src/resources/icons/ with fallback to UIManager icons if missing.
Button Styling:
Hover effects (lighter color on mouse-over) using MouseAdapter.
White borders and bold Arial fonts (16pt).
Color scheme: Green (login), blue (navigation), red (exit/delete).




Color Palette:
Black (#000000): LoginFrame background.
Dark Gray (#404040): DashboardFrame background.
Orange (#FFA500): AdminFrame background.
Blue (#0066CC, #0099FF): Headers and buttons.
Green (#00CC00): Login button.
Red (#FF3333): Exit/Delete buttons.


Table Styling (AdminFrame):
Gray background (#808080) with white text for readability.
Scroll pane matches table background.



-------Security Features

BCrypt Password Hashing:
Passwords are stored as 60-character BCrypt hashes in the admins table.
Used in LoginFrame (authentication), AdminFrame (add/edit admins), and deleteAdmin (verification).


Password Verification for Deletion:
Admins must enter their password before deleting another admin, verified with BCrypt.checkpw.


Input Validation:
Prevents empty usernames/passwords in LoginFrame and AdminFrame.



-------Troubleshooting

"package org.mindrot.jbcrypt does not exist":
Verify jbcrypt-0.4.jar in Properties > Libraries or Maven dependency in pom.xml.
Clean and build (Shift+F11).


Icons Not Loading:
Check src/resources/icons/ for PNG files (e.g., login.png).
Console logs Icon not found if missing; update paths in createScaledIcon.


Database Errors:
Ensure MySQL is running and DatabaseConnection.java has correct credentials.
Verify admins table schema (password as VARCHAR(60)).


Authentication Fails:
Confirm passwords in admins are BCrypt hashes (start with $2a$).
Run UpdatePasswords.java to hash plain-text passwords.


NetBeans Issues:
Share Output window errors for specific help.
Ensure project is a Java Application (not Ant/Maven if JAR-based).



Development Tips

Adding Icons:
To add icons to AdminFrame buttons, use createScaledIcon (as in DashboardFrame):ImageIcon addIcon = createScaledIcon("/resources/icons/add.png");
addAdminButton.setIcon(addIcon);


Place add.png, edit.png, delete.png in src/resources/icons/.


Hiding Passwords:
Remove the Password column from AdminFrame’s table for security:String[] columns = {"ID", "SSN", "Name", "Username", "Email", "Role", "Contact"};




-------Enhancements:
Add password confirmation fields for showAddAdminDialog and showEditAdminDialog.
Implement audit logging for admin actions.
Add rate limiting for login attempts.



Contact
For issues or contributions, contact the development team or refer to project documentation. In NetBeans, check the Output window for detailed error messages to share with support.
