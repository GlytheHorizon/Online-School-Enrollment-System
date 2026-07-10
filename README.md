# Online School Enrollment System

A Java desktop application with MySQL database for managing student enrollment, courses, and tuition payments using MVC architecture and DAO design pattern.

## Group 2

- Cruz, Jerwin E.
- Matiga, John Michael
- Villabroza, Clark Daren
- Lazaro, Natalie
- Layos, Joland

## Project Structure

```
activity15/
├── sql/
│   └── schema.sql              # Database schema and seed data
├── src/
│   └── school/enrollment/
│       ├── Main.java            # Entry point
│       ├── model/               # JavaBeans / POJOs
│       ├── dao/                 # DAO interfaces
│       ├── daoimpl/             # DAO implementations (JDBC)
│       ├── database/            # Database connection utility
│       ├── controller/          # Business logic layer
│       └── view/                # JFrame GUI (Swing)
├── lib/                         # Place mysql-connector-java.jar here
└── build/                       # Compiled .class files
```

## How to Run

### Prerequisites

- Java JDK 17 or later
- MySQL Server 8.0+
- MySQL Connector/J (`mysql-connector-java.jar`)

### Setup Database

1. Open MySQL command line or any MySQL client (e.g., MySQL Workbench, phpMyAdmin)
2. Run the schema script:
   ```
   source C:\Apache24\htdocs\TESDA\activity15\sql\schema.sql
   ```
   Or copy and paste the contents of `sql/schema.sql` into your MySQL client.

### Configure Database Connection

Edit `src/school/enrollment/database/DatabaseConnection.java` to match your MySQL credentials:

```java
private static final String URL = "jdbc:mysql://localhost:3306/school_enrollment";
private static final String USER = "root";
private static final String PASSWORD = "";  // change if needed
```

### Add MySQL Connector

1. Download `mysql-connector-java-x.x.x.jar` from https://dev.mysql.com/downloads/connector/j/
2. Place the JAR file inside the `lib/` folder

### Compile

```bash
javac -d build -cp "lib\*" src\school\enrollment\**\*.java
```

### Run

```bash
java -cp "build;lib\*" school.enrollment.Main
```

> **Note:** On Windows, use `;` as path separator. On Linux/Mac, use `:` instead.

## Features

- **Student Registration** – Add, update, delete, and search students with custom ID format (XXXX-XXXX)
- **Course Management** – Add, update, delete, and search courses with computed total tuition
- **Enrollment** – Enroll students in courses with real-time balance tracking
- **Tuition Payment** – Select subjects via checkbox, pay per subject or all at once, supports Cash, Bank Transfer, and Check

## Architecture

- **MVC** (Model-View-Controller)
- **DAO** (Data Access Object) design pattern
- **JDBC** for database connectivity
- **Swing (JFrame)** for GUI
- **Computation**: Total tuition = units × tuition_per_unit, Balance = total - paid
