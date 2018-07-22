# Config Server Commons

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.piszmog/cloud-config-client/badge.svg?style=plastic)](https://maven-badges.herokuapp.com/maven-central/io.github.piszmog/cloud-config-client)


## Description
Spring's Config Server provides way to externalize configurations of applications. Spring's
[Spring Cloud Config Client](https://github.com/spring-cloud/spring-cloud-config/tree/master/spring-cloud-config-client)
can be used to load the base configurations an application requires to function.

But the Config Client does not fully utilizes the Config Server's endpoints.

This library provides clients for utilizing all the endpoints in the Config Server.

The Spring dependencies of this library are,

| Dependency | Version |
| :---: | :---: |
| Spring Boot | 1.5.12 |
| Spring Cloud Services | 1.5.0.RELEASE |

See [Cloud Config Client Autoconfig](https://github.com/Piszmog/cloud-config-client-autoconfig) 
for Spring ready library.

### Config Clients
All Config Clients require a `ConfigTemplate`. `ConfigTemplate` is a wrapper for
calling the Config Server endpoints.

Implementations of the `ConfigTemplate` are `LocalConfigTemplate`, for calling a locally
running Config Server, and `OAuth2ConfigTemplate` for calling a Config Server requiring
OAuth2 authentication - this is primarily used when using the PCF provided Config Server.

#### DecryptConfigClient
Client for decrypting values that the Config Server has encrypted.

#### EncryptConfigClient
Client for encrypting values using the Config Server.

#### FileConfigClient
Client for retrieving files from the Config Server.

The Config Server today retrieves `.yml` and `.properties` files, but is unable
to retrieve `.json`, `.txt`, and etc...

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

//todo -- .json gets environment as a json/yml/properties
| Name | Operation | Endpoint Path | Description |
| :---: | :---: | :---: | :---: |
|Retrieve Configuration |GET|`/{name}/{profiles:.*[^-].*}`|Retrieves the configuration file matching the regex expression on the profile for the application name |
|Retrieve Configuration |GET|`/{name}/{profiles}/{label:.*}`|Retrieves the configuration file matching the regex expression on the label for the application name and profile|
|Retrieve Properties Configuration |GET|`/{name}-{profiles}.properties`|Retrieves the properties file matching application name and profile|
|Retrieve Properties Configuration |GET|`/{label}/{name}-{profiles}.properties`|Retrieves the properties file matching application name, profile, and label|
|Retrieve JSON Configuration |GET|`{name}-{profiles}.json`|Retrieves the JSON file matching application name and profile|
|Retrieve JSON Configuration |GET|`/{label}/{name}-{profiles}.json`|Retrieves the JSON file matching application name, profile, and label|
|Retrieve YML Configuration |GET|`/{name}-{profiles}.yml`|Retrieves the YML file matching application name and profile|
|Retrieve YAML Configuration |GET|`/{name}-{profiles}.yaml`|Retrieves the YAML file matching application name and profile|
|Retrieve YML Configuration |GET|`/{label}/{name}-{profiles}.yml`|Retrieves the YML file matching application name, profile, and label|
|Retrieve YAML Configuration |GET|`/{label}/{name}-{profiles}.yaml`|Retrieves the YAML file matching application name, profile, and label|

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
|Retrieve Resource |GET|`/{name}/{profile}/{label}/**`| `resolvePlaceholders` - boolean |Retrieves the resource of the specified application name, profile, label, and resource name|
|Retrieve Resource |GET|`/{name}/{profile}/{label}/**`| NA |Retrieves the resource of the specified application name, profile, label, and resource name|

Where,  

| Path Variable | Description |
| ---: | :--- |
|`name`| The application name|
|`profile`| The active profile|
|`label`| The GIT branch to retrieve from|