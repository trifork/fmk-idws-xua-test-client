# FMK IDWS XUA test client

Client for testing the IDWS XUA support in FMK (Det Fælles Medicinkort).

## Features

* Supports FMK 1.4.4 (MedicineCard_Idws_Xua_2015_01_01) and the following operations: GetMedicineCard.
* Supports FMK 1.4.4 E1 (MedicineCard_Idws_Xua_2015_01_01_E1) and the following operations: GetMedicineCard.
* Supports the Security token service: NSP STS 3 (currently in development at NSP).
* Can be invoked using the included commandline application or by using the library in e.g. an integration test.
* Logs STS requests and responses to a file.
* Uses the currently newest versions of CXF and Spring Boot.

## Structure

The project is structured as a Maven project with 3 modules:
* *lib-schemas*: contains WSDL-files for using different API versions of the FMK webservice with IDWS XUA
* *lib*: library containing CXF clients for calling FMK
* *app*: an example application using the CXF clients from lib and providing the neccesarry configuration files

Note: the official FMK IDWS XUA WSDL-files are available in the project fmk-schemas, but those included are identical (as of right now).

## Requirements

* Usage requires a number of configuration files and certificates. Defaults are configured to use NSP STS3 as a test user. See resources in app-module.
* An FMK webservice must be accessible. This can either be the actual FMK system or a stub.
* NSP STS3 must be accessible.

## Usage

### Client app

An application (a runnable jar-file) can be build by the app-module: *fmkclient.jar*
The application has a command line argument parser. To see available options use the argument *-h*.

### Library

The lib-module can be used as a library e.g. for integration tests. This requires Spring and can be done by importing the configuration class *IDWSXUALibSpringConfiguration*.
Several configuration and certificate files must be provided. See the app-module as an example of usage.
