# Keycloak Custom REST User Federation Provider

## Overview
Custom User Storage SPI provider for Keycloak 26.0.0 that connects to an external REST service for user authentication and management.
It calls user-service app which runs on port 8082. It exposes endpoints which valid user.

## Requirements
- Keycloak 26.0.0
- Java 21
- Maven 3.8+

## Build
mvn clean package


## Enable the Provider in the Admin Console
Open your browser and go to http://localhost:8080 (or your configured port).

Log into the Admin Console (use the credentials you set when you first ran Keycloak).

Select the Realm where you want your external users to log in (e.g., "my-realm").

In the left sidebar, click User Federation.

Click the Add provider dropdown. You should see custom-rest-provider (or whatever ID you returned in your Factory.getId() method).

## End-to-End Testing
Run auth-portal application, checkout from https://github.com/javed460/auth-portal

Run user-service application, checkout from https://github.com/javed460/user-service

Access auth-portal's endpoint: http://localhost:8081/dashboard

It redirects you to keycloak login page, enter creds from user-service's data.sql

## Flow
auth-portal -> keycloak-custom-provider -> user-service
