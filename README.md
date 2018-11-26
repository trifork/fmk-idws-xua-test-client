# FMK IDWS XUA test client

Client for testing the IDWS XUA support in FMK (Det Fælles Medicinkort).

The project is structured as a Maven project with 3 modules:
* lib-schemas: contains WSDL-files for using different API versions of the FMK webservice with IDWS XUA
* lib: library containing CXF clients for calling FMK
* app: an example application using the CXF clients from lib and providing the neccesarry configuration files

## Requirements

* Usage requires a number of configuration files and certificates. Defaults are configured to use NSP STS3 as a test user. See resources in app-module.
* NSP STS3 must be accessible.
* An FMK webservice must be accessible. This can either be the actual FMK system or a stub.

## Usage

### Client app

A applicatino (a runnable jar-file) can be build by the app-module: *fmkclient.jar*
The application has a command line argument parser. To see available options use the argument *-h*.

### Library

The lib-module can be used as a library e.g. for integration tests. This requires Spring and can be done by importing the configuration class *IDWSXUALibSpringConfiguration*.
Several configuration and certificate files must be provided. See the app-module as an example of usage.
