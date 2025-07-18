# Project Guidelines

This document provides guidelines for working on the Chemist-BE project.

## Project Overview

This is a Java Spring Boot application built using Maven. The project follows a standard layered architecture.

## Project Structure

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

## Project Structure

All model will have the following structure:
* entity: Contains the JPA entity class. place it in `model`.
  * Entity class will extend `BaseEntity` if it has common fields like `id`, `createdAt`, `updatedAt`.
  * Entity class should be annotated with `@Entity` and `@Table(name = "table_name")` to map it to a database table.
  * Entity class should have soft delete functionality, which can be implemented using a `deleted` boolean field.
* DTO: Contains the Data Transfer Object class. Place it in `dto`.
  * for each field, if the field using camelCase, the DTO field should use snake_case by @JSONProperty.
* Mapper: Contains the mapper class for converting between entity and DTO. Place it in `mapper`.
    * Mapper class can be interface or abstract class, if the field is simple, you can use MapStruct to generate the implementation.
    * Mapper should be abstract if it has complex fields or custom mapping logic.
    * use MapStruct for mapping. Be aware that DTO may not be the same as entity, so you need to configure the mapper accordingly.
    * Mapper class should reuse other mappers if the entity has nested objects or collections with use in @Mapper.
    * Mapper class should be named as `EntityNameMapper` (e.g., `UserMapper` for `User` entity).
    * Mapper class should be annotated with `@Mapper(componentModel = "spring")` to enable Spring dependency injection.
    * Mapper class should have methods for converting between entity and DTO, such as `toDto(Entity entity)` and `toEntity(Dto dto)`.
* repository: Contains the Spring Data JPA repository interface. Place it in `repository`.
  * Repository interface should extend `JpaRepository<Entity, Long>` or similar, where `Entity` is the JPA entity class.
  * Repository will have methods for querying the database, such as `findActiveById`, `findAllActive` based on the entity's soft delete functionality.
  * Repository interface should be annotated with `@Repository` to enable Spring Data JPA features.
* Service: Contains the service class that implements the business logic. Place it in `service`.
  * Service class should be annotated with `@Service` to enable Spring dependency injection.
  * Service class should use entity mapper and entity repository to perform operations.
  * Service class should use the repository to access the database and the mapper to convert between entity and DTO.
  * Service class should handle exceptions and return appropriate responses.
  * Service class should be named as `EntityNameService` (e.g., `UserService` for `User` entity).
  * Service class should have methods for business logic, such as `getAllActive`, `getActiveById`, `createEntity`, `updateEntity`, and `deleteEntity`.
  * Service class should use `@Transactional` annotation for methods that modify the database to ensure proper transaction management.
*  Controller: Contains the RESTful API controller class. Place it in `controller`.
  * Controller class should be annotated with `@RestController` to enable Spring MVC features.
  * Controller class should use service to perform operations and return appropriate responses.
  * Controller class should have methods for handling HTTP requests, such as `getAll`, `getById`, `create`, `update`, and `delete`.
  * Controller class should use appropriate HTTP methods (GET, POST, PUT, DELETE) for each operation.
  * Some Entity may to complex to get all, so create method to get by element id.
  * Controller class should use DTOs for request and response bodies.
  * Controller class should handle exceptions and return appropriate HTTP status codes.
  * Controller class should be named as `EntityNameController` (e.g., `UserController` for `User` entity).

# Project Features
This project is a RESTful API that provides api for a React TypeScript application using Vite as the frontend framework. The API is designed to be used by the frontend application to perform CRUD operations on various entities.

# Guidelines for Contributions
after you have read the project structure and features, you can start working on the project. Here are some guidelines for contributions:
* Path always start with /api/v1, and next domain will use `-` to separate words, for example: `/api/v1/user`, `/api/v1/product-category`, `/api/v1/product`.
* When you make a new feature, you should check the Entity and DTO structure, with other entities and DTOs with it mapper, the relationship between entities can be too complicated.
* With post method, the request body should be a DTO, and the response body should be the created entity's DTO. The important part is the mapper, you should use the mapper to convert between DTO and entity, some field can be null.
* Write documentation for your code, especially for public methods and classes. Use Javadoc comments to explain the purpose and usage of classes and methods.