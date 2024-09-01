# Authentication Service (JWT)

This is just an idea of Role Based Access Control (RBAC). This could be considered a service for orchestrated app to authentication and authorization.

## Endpoints

`POST` endpoints usually have payload attached to them. See code for more details:

### Actuator

* `GET /actuator`: Exposes operational information about the application – health, metrics, info, dump, env etc.

### Permissions

* `GET /permissions`: Return list of permissions
* `POST /permissions`: Create a new permission
* `GET /permissions/{permissionName}`: Return details of a permission
* `DELETE /permissions/{permissionName}`: Delete the permission

### Roles

* `GET /roles`: Return list of roles
* `POST /roles`: Create a new role
* `GET /roles/{roleName}`: Return details of a role
* `DELETE /roles/{roleName}`: Delete the role
* `POST /roles/{roleName}/permission/{permissionName}`: Add the permission to the role
* `DELETE /roles/{roleName}/permission/{permissionName}`: Remove the permission from the role

### Users

* `GET /users`: Return list of users
* `GET /users/{user}`: Return detail of a user

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

