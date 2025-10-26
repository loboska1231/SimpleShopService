# Simple Shop Service

## 1. About the project

The main aim of "Simple Shop Service" project is to show basic skills of programming web application.
A user can view products by sending a request to uri /products.
Also, user can create order with this/these product(s) if he *sign in* or *sign up* on uri /auth/signin or /auth/signup.

## 2. Start the project locally

### 2.1 Required to install

* Java 17
* Maven 3.9.11
* Docker desktop

### 2.2 How to run

1. You should open in Intellij IDEA File -> `New Project` -> `Project from version control`
    -> `Repository URL` -> `URL` (https://github.com/loboska1231/SimpleShopService.git) -> `Clone`

2. `Environment variables`

    Required to set:
   ```yaml
    spring:
       datasource:
         url: ${POSTGRES_URL}
         username: ${SQL_USERNAME}
         password: ${POSTGRES_URL}
       data:
         mongodb:
           uri: ${MONGODB_URI}
       mail:
         username: ${MAIL_USERNAME}
         password: ${MAIL_PASSWORD}
    jwt:
      secret-key: ${JWT_SECRET}
      access-token:
        ttl-millis: ${JWT_ACC_TTL}
      refresh-token:
        ttl-millis: ${JWT_REF_TTL}
   ```
   * *MAIL_PASSWORD*  - from Google app passwords

   * *JWT_SECRET* - should have at least 256 bits ( 64 characters ), you can use [this](https://jwtsecrets.com) to generate it
![env-vars](/docs-photos/env.png)

3. Open `Terminal` write `mvn clean compile` (this will create dir *target* with compiled project)

4. Open `Terminal` write `docker compose up psql-service mongo-service`

5. `Run application` or press `Shift+F10`

> [!WARNING]
> You may have an error with timezone :
> `FATAL: invalid value for parameter "TimeZone": "Europe/Kiev"`
> All you need to do is change your timezone on PC to `UTC`
> and then `Run application` or press `Shift+F10` again

7. If you did everything correctly, you should be able to access swagger by this URL: http://localhost:8080/swagger-ui/index.html .

## 2.3 Swagger-UI
this URL: http://localhost:8080/swagger-ui/index.html 
    has three main endpoints 

    /products
    /orders
    /auth/**
   
'**' - _signup_ or _signin_ or _refresh_ .

### 2.4 Requirements to send specific requests
If you want to **change**, **add**, **delete** _product_, you should have **JWT** token with role - `ADMIN`.

If you want to **GET all** _orders_, you should have **JWT** token with role - `ADMIN`.

To create JWT token, you have to send a `POST` request to `localhost:8080/auth/signup` with request body
and after that you will receive `JSON` object with two fields : **accessToken**, **refreshToken**.
```json
{
  "firstName": "john",
  "lastName": "wallet",
  "email": "anyEmail@gmail.com",
  "password": "1111",
  "role": "ADMIN"
}
```
where _**firstName, lastName, email, password**_ are `REQUIRED`!

Or you can send `POST` request to `localhost:8080/auth/signin` with request body
and after that you will receive tokens, if user exists.
```json
{
  "email": "existingUser@gmail.com",
  "password": "password"
}
```
where _**email**_ and _**password**_ are `REQUIRED`!

Or you can send `POST` request to `localhost:8080/auth/refresh` with refreshToken
which you received from two previous requests and after that you will update refresh token in user field **refreshToken**
and receive tokens.
```json
{
  "refreshToken": "some refresh token"
}
```
If you didn't specify field **role**, by default it would be role `USER`.

After all of these operations you might have `JWT` token with role `ADMIN`
and you want to send a specific request to do something, you should put this token into
header `AUTHORIZATION` and set value that must start with `Bearer ...` .

## 3. Future plans 
    
* Add Swagger-UI annotation to Controllers to describe them 
* * Add SecurityScheme for Swagger-UI
* Add `.env.postgres` and `.env.mongo` file which can replace field values in [compose.yaml](/compose.yaml)

Over time, items will be added or removed.
