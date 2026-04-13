# Smart Job Application Filtering System

## Overview

This project implements the **Chain of Responsibility design pattern** in Java using a job application filtering system as a practical example. The system simulates how applications are processed through multiple evaluation stages, where each stage independently decides whether the application should proceed further.

---

## Problem Statement

In a typical recruitment process, a job application goes through multiple levels of evaluation such as HR screening, technical assessment, and managerial approval. Implementing this logic using traditional conditional structures can lead to tightly coupled and difficult-to-maintain code.

---

## Solution Approach

The system is designed using the Chain of Responsibility pattern, where each evaluation stage is represented as a separate handler. Each handler processes the request and either forwards it to the next stage or terminates the process.

This approach ensures that:

* Each component has a single responsibility
* The system remains flexible and easy to extend
* New evaluation stages can be added with minimal changes

---

## System Design

### Key Components

* **JobApplication** – Represents the request being processed
* **ApplicationHandler** – Abstract class defining the structure for all handlers
* **HRFilter** – Performs initial screening
* **TechnicalFilter** – Evaluates technical qualifications
* **ManagerApprovalFilter** – Final decision stage
* **JobApplicationProcessor** – Manages the flow of the application

---

## Workflow

1. A job application is created
2. It is passed to the first handler in the chain
3. Each handler evaluates the application based on its criteria
4. If approved, the application moves to the next handler
5. If rejected at any stage, processing stops immediately

---

## How to Run

Compile and execute the program using:

```
javac ChainOfResponsibilityProject.java
java ChainOfResponsibilityProject
```

---

## Features

* Clear implementation of a behavioral design pattern
* Modular and maintainable code structure
* Easy to extend by adding new handlers
* Demonstrates real-world applicability of design patterns

---

## Advantages

* Promotes loose coupling between components
* Follows the Single Responsibility Principle
* Simplifies code modification and extension
* Improves code readability and maintainability

---

## Limitations

* Request handling is not guaranteed
* Debugging can be more complex
* Performance may be affected with longer chains

---

## Applications

This pattern is widely used in:

* Event handling systems
* Approval workflows
* Logging mechanisms
* Middleware processing

---

## Author

Shivani Badule

---

## Academic Context

This project was developed as part of coursework to demonstrate the practical implementation of design patterns in software engineering.
