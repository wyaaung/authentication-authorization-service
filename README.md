# Authentication Service (JWT)

This is just an idea of Role Based Access Control (RBAC). This could be considered a service for orchestrated app to authentication and authorization.

## Endpoints

`POST` endpoints usually have payload attached to them. See code for more details:

### Actuator

* `GET /actuator`: Exposes operational information about the application – health, metrics, info, dump, env etc.

### Permissions

* `GET /permission`: Return list of permissions
* `POST /permission`: Create a new permission
* `GET /permission/{permissionName}`: Return details of a permission
* `DELETE /permission/{permissionName}`: Delete the permission

### Roles

* `GET /role`: Return list of roles
* `POST /role`: Create a new role
* `GET /role/{roleName}`: Return details of a role
* `DELETE /role/{roleName}`: Delete the role
* `POST /role/{roleName}/permission/{permissionName}`: Add the permission to the role
* `DELETE /role/{roleName}/permission/{permissionName}`: Remove the permission from the role

### Users

* `GET /user`: Return list of users
* `GET /user/{user}`: Return detail of a user

### Authentication

* `GET /auth/register`: Register a new user. This returns both access and refresh token.
* `GET /auth/authenticate`: Authenticate a user. This returns both access and refresh token.
* `GET /auth/refresh-token`: Refresh an access token of a user. This returns both access and refresh token.

## Prerequisites

* Java 17
* Gradle

## Run

```sh
./gradlew bootRun
 ```

## Metrics 

Spring Boot Actuator already has some built in metrics for things like JVM memory/thread usage and counting endpoint status responses.

