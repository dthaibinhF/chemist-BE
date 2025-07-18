# Chemist-BE Project Bank Memory

This directory serves as a "bank memory" for the Chemist-BE project, tracking changes, architectural decisions, and key knowledge about the project over time.

## Purpose

The bank memory is designed to:
1. Track changes made to the project over time
2. Document architectural decisions and their rationales
3. Provide clarity about the project structure and components
4. Serve as a reference for developers working on the project

## How to Use

When making significant changes to the project:
1. Update the relevant section in this README.md file
2. For major features or components, create a dedicated markdown file in this directory
3. Reference the bank memory when working on related features

For detailed instructions on how to update the bank memory, see the [update guide](update_guide.md).
For a summary of the bank memory implementation, see the [summary](SUMMARY.md).

## Project Overview

This is a Java Spring Boot application built using Maven. The project follows a standard layered architecture.

### Project Structure

The project is organized into the following main packages under `dthaibinhf.project.chemistbe`:

*   `config`: Contains Spring configuration classes.
*   `constants`: Holds application-wide constants.
*   `controller`: Houses RESTful API controllers that handle incoming HTTP requests.
*   `dto`: Data Transfer Objects used for transferring data between layers, especially between the service and controller layers.
*   `exception`: Contains custom exception classes and exception handlers.
*   `filter`: Holds request/response filters.
*   `mapper`: Contains mappers (e.g., MapStruct) for converting between DTOs and entity models.
*   `model`: Defines the JPA entity models that map to database tables.
*   `repository`: Contains Spring Data JPA repositories for database access.
*   `service`: Implements the business logic of the application.

## Key Components

### Entities

This section will document the key entities in the system and their relationships.

### APIs

This section will document the key APIs provided by the system.

## Change History

This section will track significant changes made to the project over time.

### [Date: 2025-07-15] Enhanced Schedule Service and Controller Documentation

- Added detailed documentation for Schedule and GroupSchedule entities in entities.md
- Added detailed documentation for Schedule API endpoints in apis.md
- Created comprehensive documentation for ScheduleService and ScheduleController in the docs directory
- Implemented weekly schedule generation feature in ScheduleService and ScheduleController

### [Date: 2025-07-14] Initial Setup

- Created bank memory structure to track project changes and knowledge
- Established initial documentation framework
