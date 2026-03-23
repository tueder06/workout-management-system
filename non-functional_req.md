# Non-Functional Requirements for Workout Management System application

## 1. Usability

| Requirement | Technical Specification |
| :--- | :--- |
| **Learning Curve** | A new user shall be able to initiate a workout session in less than 3 minutes without referring to a manual. |
| **GUI Conventions** | The interface shall adopt a High-Contrast Dark Mode scheme to ensure legibility on the smartwatch under direct sunlight. |

## 2. Performance

| Requirement | Technical Specification |
| :--- | :--- |
| **Response Time** | Loading a workout plan from the database onto the mobile device must not exceed **2.0 seconds**. |
| **Availability** | The system must be operational and accessible at any time. However, in order to log in into the account , there must be an Internet connection.
| **Accuracy** | Calculations for total volume lifted and calories burned must have a margin of error no greater than 3%. |

## 3. Reliability

| Requirement | Technical Specification |
| :--- | :--- |
| **Security (Attacks)** | The system must provide a decent security in order to not be able to have unauthorized access to the sensitive data of the users. |
| **Robustness** | In the event of internet connectivity loss, the app must continue to record sets locally to ensure no data loss occurs. |
| **Correctness** | Synchronization between the smartwatch and the mobile app must guarantee data conformity in 95% of successfully ended sessions. |

## 4. Supportability

| Requirement | Technical Specification |
| :--- | :--- |
| **Adaptability** | The architecture shall allow the addition of new exercise categories (new domain concepts) without requiring modifications to the core database schema. |
| **Maintainability** | Source code shall be documented to allow for critical bug fixes to be deployed within 24 hours of reporting. |
