# Cloud Config Client

[![Java](https://github.com/Piszmog/cloud-config-client-java/actions/workflows/java.yml/badge.svg)](https://github.com/Piszmog/cloud-config-client-java/actions/workflows/java.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Piszmog_cloud-config-client&metric=alert_status)](https://sonarcloud.io/dashboard?id=Piszmog_cloud-config-client)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.piszmog/cloud-config-client/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.github.piszmog/cloud-config-client)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Description

Spring's Config Server provides way to externalize configurations of applications. Spring's
[Spring Cloud Config Client](https://github.com/spring-cloud/spring-cloud-config/tree/master/spring-cloud-config-client)
can be used to load the base configurations an application requires to function.

But the Config Client does not fully utilizes the Config Server's endpoints.

This library provides clients for utilizing all the endpoints in the Config Server.

__Cloud Config Client 3.2.x__ is compatible with

| Dependency | Version |
| :---: | :---: |
| Spring Boot | 2.4.x |
| Spring Cloud Services | 3.2.x.RELEASE |

__Cloud Config Client 3.1.x__ is compatible with

| Dependency | Version |
| :---: | :---: |
| Spring Boot | 2.2.x - 2.3.x |
| Spring Cloud Services | 3.1.x.RELEASE |

__Cloud Config Client 2.x__ is compatible with

| Dependency | Version |
| :---: | :---: |
| Spring Boot | 2.1.x |
| Spring Cloud Services | 2.x.x.RELEASE |

__Cloud Config Client 1.x__ is compatible with

| Dependency | Version |
| :---: | :---: |
| Spring Boot | 1.5.x |
| Spring Cloud Services | 1.5.x.RELEASE |

See [Cloud Config Client Autoconfig](https://github.com/Piszmog/cloud-config-client-autoconfig)
for Spring ready library.

### Config Clients

All Config Clients require a `ConfigTemplate`. `ConfigTemplate` is a wrapper for calling the Config Server endpoints.

Implementations of the `ConfigTemplate` are `LocalConfigTemplate`, for calling a locally running Config Server,
and `OAuth2ConfigTemplate` for calling a Config Server requiring OAuth2 authentication - this is primarily used when
using the PCF provided Config Server.

#### DecryptConfigClient

Client for decrypting values that the Config Server has encrypted.

#### EncryptConfigClient

Client for encrypting values using the Config Server.

#### FileConfigClient

Client for retrieving files from the Config Server.

The Config Server today retrieves `.yml` and `.properties` files, but is unable to retrieve `.json`, `.txt`, and etc...

##### Specific Branch

File can only be retried from the root of the repo if a specific branch name is provided. This is a limitation on how
the endpoints for the Config Server are set up.

##### Default Branch

To retrieve files from the Config Server's default branch, the files must be in a subdirectory.

* __Only compatible with Config Server version 1.4.0.RELEASE or greater__

#### PublicKeyClient

Client for retrieving the Public Key from the Config Server.

### Config Server

#### Endpoints

The Config Server exposes the following endpoints,

##### Security

All security specific endpoints dealing with encryption and decryption.

| Name | Operation | Endpoint Path | Description |
| :---: | :---: | :---: | :---: |
|Retrieve Public Key|GET|`/key`|Returns the Public Key for default application name and profile|
|Retrieve Public Key|GET|`/key/{name}/{profiles}`|Returns the Public Key for specified application name and profile|
|Encryption Install Status|GET|`/encrypt/status`|Check if an encryptor is setup|
|Encrypt Data|POST|`/encrypt`|Encrypts the request body using the default application name and profile|
|Encrypt Data|POST|`/encrypt/{name}/{profiles}`|Encrypts the request body using the specified application name and profile|
|Decrypt Data|POST|`/decrypt`|Decrypts the request body using the default application name and profile|
|Decrypt Data|POST|`/decrypt/{name}/{profiles}`|Decrypts the request body using the specified application name and profile|

Where,

| Path Variable | Description |
| ---: | :--- |
|`name`| The application name|
|`profiles`| The active profile|

##### Environment

All environment specific endpoints dealing with loading configuration files to applications on their startup.

| Name | Operation | Endpoint Path | Description |
| :---: | :---: | :---: | :---: |
|Retrieve Configuration |GET|`/{name}/{profiles:.*[^-].*}`|Retrieves the configuration file matching the regex expression on the profile for the application name |
|Retrieve Configuration |GET|`/{name}/{profiles}/{label:.*}`|Retrieves the configuration file matching the regex expression on the label for the application name and profile|
|Retrieve Properties Configuration |GET|`/{name}-{profiles}.properties`|Retrieves the application properties in Properties format|
|Retrieve Properties Configuration |GET|`/{label}/{name}-{profiles}.properties`|Retrieves the application properties in Properties format|
|Retrieve JSON Configuration |GET|`{name}-{profiles}.json`|Retrieves the application properties in JSON format|
|Retrieve JSON Configuration |GET|`/{label}/{name}-{profiles}.json`|Retrieves the application properties in JSON format|
|Retrieve YML Configuration |GET|`/{name}-{profiles}.yml`|Retrieves the application properties in YML format
|Retrieve YAML Configuration |GET|`/{name}-{profiles}.yaml`|Retrieves the application properties in YAML format|
|Retrieve YML Configuration |GET|`/{label}/{name}-{profiles}.yml`|Retrieves the application properties in YML format|
|Retrieve YAML Configuration |GET|`/{label}/{name}-{profiles}.yaml`|Retrieves the application properties in YAML format|

Where,

| Path Variable | Description |
| ---: | :--- |
|`name`| The application name|
|`profiles`| The active profile|
|`label`| The GIT branch to retrieve from|

##### Resource

All resource specific endpoints dealing with loading files.

| Name | Operation | Endpoint Path | Parameters | Description |
| :---: | :---: | :---: | :---: | :---: |
|Retrieve Resource |GET|`/{name}/{profile}/{label}/**`| `useDefaultBranch` - boolean |Retrieves the resource of the specified application name, profile, label, and resource name from the default branch|
|Retrieve Resource |GET|`/{name}/{profile}/{label}/**`| NA |Retrieves the resource of the specified application name, profile, label, and resource name|

Where,

| Path Variable | Description |
| ---: | :--- |
|`name`| The application name|
|`profile`| The active profile|
|`label`| The GIT branch to retrieve from|
