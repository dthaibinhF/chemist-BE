# How to Run and Use Swagger UI in Chemist-BE

This guide explains how to access and use the Swagger UI documentation for the Chemist-BE API.

## Prerequisites

- Java 21 installed
- Maven installed
- PostgreSQL database running (as configured in application properties)

## Running the Application

1. Clone the repository
2. Navigate to the project root directory
3. Run the application using Maven:

```bash
mvn spring-boot:run
```

This will start the application with the default "dev" profile, which includes Swagger UI configuration.

## Accessing Swagger UI

Once the application is running, you can access the Swagger UI at:

```
http://localhost:8080/swagger-ui-custom.html
```

This URL is configured in the `application-dev.properties` file with the property:
```properties
springdoc.swagger-ui.path=/swagger-ui-custom.html
```

## Authentication for Swagger UI

The application uses JWT authentication for API endpoints. The Swagger UI interface itself is accessible without authentication, as the following paths are explicitly permitted in the security configuration:

- `/swagger-ui-custom.html` - The custom Swagger UI path
- `/swagger-ui/**` - For Swagger UI resources
- `/v3/api-docs/**` - For OpenAPI documentation
- `/api-docs` - For the OpenAPI JSON

However, most API endpoints require authentication.

To use protected endpoints through Swagger UI:

1. First, use the `/api/v1/auth/login` endpoint to obtain a JWT token
2. Click the "Authorize" button at the top of the Swagger UI page
3. Enter your JWT token in the format: `Bearer your_token_here`
4. Click "Authorize" to apply the token to all subsequent requests

## Available Documentation

The OpenAPI documentation is available in JSON format at:

```
http://localhost:8080/api-docs
```

This URL is configured in the `application-dev.properties` file with the property:
```properties
springdoc.api-docs.path=/api-docs
```

## Exporting OpenAPI Specification

The Swagger UI interface includes functionality to export the OpenAPI specification in various formats. To export the OpenAPI spec:

1. Navigate to the Swagger UI page at `http://localhost:8080/swagger-ui-custom.html`
2. Look for the export options in the top right corner of the Swagger UI interface
3. Click on the desired format to download the specification:
   - **JSON**: Downloads the OpenAPI specification in JSON format
   - **YAML**: Downloads the OpenAPI specification in YAML format

The export functionality is enabled by the following configuration in `application-dev.properties`:

```properties
springdoc.swagger-ui.supportedSubmitMethods=get,put,post,delete,options,head,patch,trace
springdoc.swagger-ui.display-request-duration=true
```

You can also access the raw OpenAPI specification directly at:
- JSON format: `http://localhost:8080/api-docs`
- YAML format: `http://localhost:8080/api-docs.yaml`

## Troubleshooting

If you cannot access the Swagger UI:

1. Ensure the application is running (check console output)
2. Verify you're using the correct URL (http://localhost:8080/swagger-ui-custom.html)
3. Check that the `springdoc-openapi-starter-webmvc-ui` dependency is included in the project's pom.xml
4. Verify that Swagger UI is enabled in the properties with `springdoc.swagger-ui.enabled=true`

## Additional Resources

- [SpringDoc OpenAPI Documentation](https://springdoc.org/)
- [Swagger UI Documentation](https://swagger.io/tools/swagger-ui/)
