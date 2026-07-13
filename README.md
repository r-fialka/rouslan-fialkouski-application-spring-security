# 💸 Pay My Buddy

```{=html}
<p align="center">
```
`<img src="images/logo.svg" alt="Pay My Buddy Logo" width="180">`{=html}
```{=html}
</p>
```
```{=html}
<p align="center">
```
`<strong>`{=html}A Secure Money Transfer Web
Application`</strong>`{=html}
```{=html}
</p>
```
```{=html}
<p align="center">
```
![Java](https://img.shields.io/badge/Java-21-orange) ![Spring
Boot](https://img.shields.io/badge/Spring%20Boot-4.1-brightgreen)
![Spring
Security](https://img.shields.io/badge/Spring%20Security-6-green)
![Hibernate](https://img.shields.io/badge/Hibernate-ORM-brown)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3-green) ![JUnit
5](https://img.shields.io/badge/JUnit-5-success)
![Mockito](https://img.shields.io/badge/Mockito-5-yellowgreen)
![JaCoCo](https://img.shields.io/badge/JaCoCo-100%25-red)
![MySQL](https://img.shields.io/badge/MySQL-8-blue)

```{=html}
</p>
```
> \[!NOTE\] This project was developed as part of the **OpenClassrooms
> -- Java Application Developer** program. It demonstrates the
> implementation of a secure Spring Boot web application based on the
> MVC architecture using Spring Security, Spring Data JPA, Thymeleaf and
> comprehensive automated testing.

------------------------------------------------------------------------

## 📖 Overview

**Pay My Buddy** is a secure web application that allows registered
users to transfer money safely between trusted connections.

The application demonstrates:

-   MVC architecture
-   Spring Security authentication
-   Business service layer
-   Spring Data JPA persistence
-   Thymeleaf server-side rendering
-   Automated testing with JUnit 5, Mockito and JaCoCo

------------------------------------------------------------------------

## ✨ Features

-   Secure user registration
-   User authentication with Spring Security
-   Password encryption using BCrypt
-   Profile management
-   Trusted connections management
-   Secure money transfers
-   Transaction history
-   Email uniqueness validation
-   Duplicate connection prevention
-   Self-connection prevention

------------------------------------------------------------------------

## 🛠 Technologies

  Category          Technologies
  ----------------- ----------------------------------------------------------
  Language          Java 21
  Framework         Spring Boot 4.1
  Web               Spring MVC
  Security          Spring Security, BCrypt
  Persistence       Spring Data JPA, Hibernate
  Database          MySQL 8
  Template Engine   Thymeleaf
  Frontend          HTML5, CSS3
  Testing           JUnit 5, Mockito, Spring Boot Test, Spring Security Test
  Reports           JaCoCo, Maven Surefire Report
  Build Tool        Maven

------------------------------------------------------------------------

## 🏗 Architecture

``` text
Browser
    │
    ▼
Controller
    │
    ▼
Service
    │
    ▼
Repository
    │
    ▼
MySQL Database
```

Each layer has a single responsibility:

-   Controllers handle HTTP requests.
-   Services implement business logic.
-   Repositories access the database.
-   Entities represent the domain model.

------------------------------------------------------------------------

## 📁 Project Structure

``` text
src
├── main
│   ├── java
│   │   └── com.openclassrooms.paymybuddy
│   │       ├── configuration
│   │       ├── controller
│   │       ├── model
│   │       ├── repository
│   │       ├── security
│   │       └── service
│   └── resources
│       ├── static
│       ├── templates
│       └── application.properties
└── test
```

------------------------------------------------------------------------

## 📸 Application Screenshots

> Replace the filenames below if your screenshots use different names.

```{=html}
<p align="center">
```
`<img src="images/login.png" width="47%" alt="Login">`{=html}
`<img src="images/register.png" width="47%" alt="Registration">`{=html}
```{=html}
</p>
```
```{=html}
<p align="center">
```
`<img src="images/home.png" width="47%" alt="Home">`{=html}
`<img src="images/profile.png" width="47%" alt="Profile">`{=html}
```{=html}
</p>
```
```{=html}
<p align="center">
```
`<img src="images/add-relation.png" width="70%" alt="Add Relation">`{=html}
```{=html}
</p>
```

------------------------------------------------------------------------

## 📂 UML Documentation

Store the project diagrams in the `docs/` directory.

-   Use Case Diagram
-   Class Diagram
-   Physical Data Model (MPD)

------------------------------------------------------------------------

## 🗄 Database

``` sql
CREATE DATABASE paymybuddy
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;
```

Import the provided SQL script.

------------------------------------------------------------------------

## ⚙ Configuration

Configure your database in:

``` text
src/main/resources/application.properties
```

Example:

``` properties
spring.datasource.url=jdbc:mysql://localhost:3306/paymybuddy
spring.datasource.username=root
spring.datasource.password=your_password
```

------------------------------------------------------------------------

## ▶ Installation

``` bash
git clone https://github.com/your-account/paymybuddy.git
cd paymybuddy
mvn clean install
```

------------------------------------------------------------------------

## ▶ Running the Application

``` bash
mvn spring-boot:run
```

------------------------------------------------------------------------

## 🧪 Running Tests

``` bash
mvn clean verify
```

This command:

-   runs all unit tests;
-   executes MVC tests;
-   executes Spring Security tests;
-   generates JaCoCo reports;
-   generates Maven Surefire reports.

------------------------------------------------------------------------

## 📊 Test Coverage

Coverage reports:

-   JaCoCo: `target/site/jacoco/index.html`
-   Surefire: `target/reports/surefire.html`

------------------------------------------------------------------------

## 🚀 Future Improvements

-   REST API
-   Docker support
-   CI/CD with GitHub Actions
-   Email notifications
-   Pagination
-   Transaction search
-   User avatars
-   Role-based authorization
-   Internationalization (i18n)

------------------------------------------------------------------------

## 👤 Author

**Rouslan**

OpenClassrooms -- Project 6

------------------------------------------------------------------------

## 📄 License

This project is licensed under the MIT License.
