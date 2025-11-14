# PRM392_LabVerse-RPMS_BE

## 📚 Overview

This is the **backend source code (BE)** for the **LabVerse - Research Paper Management System (RPMS)**, a scholarly research document management system built as an Android mobile application.

**LabVerse** is designed to address challenges in research document management by providing a **centralized, secure, and collaborative platform** for managing academic papers, specifically tailored to the workflows of laboratories and research groups.

This backend is built using **Spring Boot** (Java) to deliver powerful, stable, and secure API services for the LabVerse mobile application.

---

## 🛠️ Tech Stack

The project is developed based on the following enterprise application technologies:

| Category | Technology |
| :--- | :--- |
| **Language** | Java 17+ |
| **Framework** | **Spring Boot** |
| **Security** | Spring Security, JWT, Google Oauth2  |
| **Build Tool** | **Maven** |
| **Database** | **SQL Server** |

---

## 🚀 Getting Started

### Prerequisites

Ensure you have the following tools installed on your development machine:

* **Java Development Kit (JDK) 17+**
* **Maven 3.x**
* **A Database Management System** (*E.g., MySQL/PostgreSQL*)

### Installation and Run

1.  **Clone Repository:**
    ```bash
    git clone [https://github.com/AnhVu-NAV/PRM392_LabVerse-RPMS_BE.git](https://github.com/AnhVu-NAV/PRM392_LabVerse-RPMS_BE.git)
    cd PRM392_LabVerse-RPMS_BE
    ```

2.  **Database Configuration:**
    * Create a new database (e.g., `labverse_db`).
    * Update the connection information (URL, username, password) in the configuration file (usually `src/main/resources/application.properties` or `application.yml`).

    *Example basic configuration in `application.properties`:*
    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/labverse_db
    spring.datasource.username=your_user
    spring.datasource.password=your_password
    spring.jpa.hibernate.ddl-auto=update
    server.port=8080
    ```

3.  **Build Project with Maven:**
    ```bash
    ./mvnw clean install
    ```

4.  **Run the Spring Boot Application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start and be available at `http://localhost:8080` (or the configured port).

---

## 📱 Frontend Connection

This backend is designed to work in conjunction with the LabVerse Android mobile application:

* **Frontend Repository (Android Application):** [LabVerse-RPMS](https://github.com/AnhVu-NAV/LabVerse-RPMS)

Please refer to the frontend repository for instructions on configuring the Android application to call the APIs from this server.

---

## 📄 License

[Insert Project License information, e.g., MIT, Apache 2.0, or state *All Rights Reserved*]
