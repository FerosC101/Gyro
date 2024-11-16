# Gyro

This program is a console-based system that transforms achievements and skills into experience points (EXP) using an RPG-style leveling framework. Inspired by isekai anime, it serves as a unique alternative to resumes by presenting personal stats and levels based on real-life accomplishments. Users can register, log in, view their stats, and add new achievements that increase their EXP and levels. All data, including personal stats and achievements, is stored in a MySQL database, making the program a gamified approach to tracking growth and skills.
## Features

- **User Registration and Login:** Secure registration and login processes with credentials stored in a MySQL database.
- **Admin System:** Delete users and every information they have in the database.
- **Achievements and Job Experience Tracking:** Record achievements and job experiences, calculate experience points (EXP), and visualize user progress.
- **Daily Routine Assignment:** Assign daily tasks and routines to users, calculate EXP for task completion, and visualize progress.
- **Level Calculation:** Track and display user levels based on accumulated experience, with custom EXP calculations.
- **Profile Customization:** Users can update their profiles with new contact details, height, weight, and profession.

## File Structure

```plaintext
Gyro/
├── src/
│   ├── Main.java                             # Main entry point to initialize the application
│   │
│   ├── controller/                           # Controllers for handling specific user actions
│   │   ├── UserController.java               # Manages user interactions and account views
│   │   ├── AdminController.java              # Manages admin-specific actions like banning users
│   │   └── CredentialController.java         # Handles credential-related actions
│   │
│   ├── model/                                # Data models representing the entities
│   │   ├── User.java                         # Model for user data with properties and methods
│   │   └── Credential.java                   # Model for credentials (achievements and experiences)
│   │
│   ├── service/                              # Business logic layer containing service classes
│   │   ├── AccountService.java               # Abstract class for account-related services
│   │   ├── UserService.java                  # Service for standard user actions
│   │   ├── AdminService.java                 # Service for admin-specific actions (polymorphic with UserService)
│   │   └── CredentialService.java            # Service for managing user credentials
│   │
│   ├── connection/                           # Data Access Objects for database interactions
│   │   ├── UserDAO.java                      # DAO for user data (CRUD operations)
│   │   ├── CredentialDAO.java                # DAO for credential data (CRUD operations)
│   │   └── DBConnection.java                 # Utility for establishing and managing database connections
│   │
│   ├── util/                                 # Utility classes and constants
│   │   └── Constants.java                    # Application-wide constants and configurations
│   │
│   ├── view/                                 # Classes for managing display and user interactions
│   │   └── Menu.java                         # Main menu display and input handler
│   │
│   └── resources/                            # Configuration and resource files
│       ├── schema.sql                        # Creadting the Database and the tables in the database
│       └── db.properties                     # Database configuration settings
│
└── Readme.md
```
## Application of the 4 Principles of Object-Oriented Programming
### 1. **Encapsulation**
- **Implementation**:
   - Each class has private fields and access is provided via public getter and setter methods, allowing controlled access to the properties.
   - The **UserDAO** and **CredentialDAO** classes encapsulate database access logic, making it accessible only through specific methods, such as **getUserById** and **addCredential**.
   - The **UserController** class organizes operations such as **viewAccount** and **editUserInfo**, encapsulating the functionalities in methods.
### 2. **Inheritance**
- **Implementation**:
   - **AdminService** and **UserService** inherit from **AccountService**. They share the **register** and **login** methods but have distinct functionalities, such as user management for admin purposes.
   - **CredentialController** inherits functionalities from **CredentialService**, reusing methods like **addAchievement**.
### 3. **Polymorphism**
- **Implementation**:
   - **Method Overriding**: **AdminService** and **UserService** override **manageUserSession** from **AccountService** to provide different user experiences based on role (admin vs. standard user).
   - **Dynamic Binding**: At runtime, depending on whether a user is an admin, **UserController** dynamically calls the **AdminService** or **UserService** implementation.
### 4. **Abstraction**
- **Implementation**:
   - The **AccountService** is an abstract class that provides a structure for account-related actions like **register** and **login** but delegates specific implementation details to subclasses (**AdminService** and **UserService**).
   - **DBConnection** abstracts away the complexities of establishing a database connection, providing a **getConnection** method that any DAO can use without needing to know the connection details.


## Sustainable Development Goal

### 1. **SDG 4: Quality Education** 
- By gamifying skill development and achievements, it encourages continuous learning and personal growth. This unique approach to tracking skills can motivate users to improve in various areas, fostering a culture of lifelong learning.

### 2. **SDG 8: Decent Work and Economic Growth** 
- It redefines how people present their skills and achievements, promoting a skills-based approach to employment. By highlighting achievements in a gamified format, it provides an innovative way to increase employability and help users reach their career potential.
