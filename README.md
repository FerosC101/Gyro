# Gyro

This program is a console-based system that transforms achievements and skills into experience points (EXP) using an RPG-style leveling framework. Inspired by isekai anime, it serves as a unique alternative to resumes by presenting personal stats and levels based on real-life accomplishments. Users can register, log in, view their stats, and add new achievements that increase their EXP and levels. All data, including personal stats and achievements, is stored in a MySQL database, making the program a gamified approach to tracking growth and skills.
## Features

- **User Registration and Login:** Secure registration and login processes with credentials stored in a MySQL database.
- **Admin System:** Delete users and every information they have in the database.
- **Achievements and Job Experience Tracking:** Record achievements and job experiences, calculate experience points (EXP), and visualize user progress.
- **Daily Routine Assignment:** Assign daily tasks and routines to users, calculate EXP for task completion, and visualize progress.
- **Level Calculation:** Track and display user levels based on accumulated experience, with custom EXP calculations.
- **Profile Customization:** Users can update their profiles with new contact details, height, weight, and profession.

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

## How to Run Gyro


#### **Prerequisites**
Before running the application, ensure you have the following installed:
1. **Java Development Kit (JDK)** (minimum version 8 or above).
2. **Apache Maven** (for dependency management, optional if precompiled).
3. **MySQL Server** (for the database).
4. A suitable **IDE** (e.g., IntelliJ IDEA, Eclipse, VS Code) or a terminal to compile and run the program.

---

#### **Database Setup**

1. **Create the Database**:
  - Open MySQL Workbench or any SQL editor.
  - Run the script in the **schema.sql** file to set up the database and tables.
  

2. **Update `Db.properties`**:
  - Configure database connection details in the `db.properties` file.
  - Replace with your local database credentials:

    ```
    db.url=jdbc:mysql://localhost:3306/wandat
    db.user=YOUR_MYSQL_USERNAME
    db.password=YOUR_MYSQL_PASSWORD
    ```

3. **Test Database Connection**:
  - Run the application to confirm database connectivity. Errors like `SQLException` indicate issues in connection configuration.

---

#### **Project File Structure**

The project adheres to an organized structure for maintainability:

```
src/
├── com.example/
│   ├── connection/         # Database connections and DAOs
│   │   ├── DBConnection.java
│   │   ├── UserDAO.java
│   │   └── CredentialDAO.java
│   │
│   ├── controller/         # Business logic controllers
│   │   ├── AdminController.java    
│   │   ├── UserController.java
│   │   └── CredentialController.java
│   │
│   ├── model/              # Models for entities
│   │   ├── User.java
│   │   └── Credential.java
│   │
│   ├── service/            # Application services and logic
│   │   ├── AccountService.java
│   │   ├── UserService.java
│   │   ├── AdminService.java           # Service for admin-specific actions (polymorphic with UserService)
│   │   └── CredentialService.java 
│   │
│   ├── util/               # Utility classes
│   │   └── Constants.java
│   │
│   ├── view/               # User interaction components
│   │   └── Menu.java
│   │
│   └── Main.java           # Application entry point
│
└── resources/              # Configuration files
    ├── db.properties       # Database configurations
    └── schema.sql          # Database schema
```

---

#### **Running the Application**

1. **Compile the Application**:
  - Open a terminal and navigate to the project directory.
  - Compile all `.java` files:
    ```bash
    javac -d out src/com/example/main.java
    ```

2. **Run the Application**:
  - Start the program from the `Main` class:
    ```bash
    java -cp out com.example.Main
    ```

3. **Interacting with the Program**:
  - **Main Menu Options**:
    ```
    ==== Welcome to Gyro ====
    [1] View Global Server
    [2] Login
    [3] Register
    [4] Exit
    ```

  - **User Features**:
    - Register, provide details, and log in.
    - View, add, and edit user credentials (achievements and job experience).
    - Participate in routines and earn EXP.

  - **Admin Features**:
    - View all users globally.
    - Delete user accounts.

---

#### **Additional Notes**

- **User Authentication**:
  - Regular users log in with their credentials.
  - Admin credentials:
    ```
    Username: admin
    Password: admin123
    ```

- **EXP and Level Calculation**:
  - EXP is earned through achievements, job experience, and daily routines.
  - Level progression:
    - Base EXP for Level 1: `1000`
    - Increment per level: `+250`
    - Formula integrated into services.

- **Known Dependencies**:
  - **MySQL Connector**: Ensure `mysql-connector-java` is in the classpath for database interactions.
  - Maven dependency for MySQL:
    ```xml
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.31</version>
    </dependency>
    ```

---


Enjoy exploring **Gyro**!