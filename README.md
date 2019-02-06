# FMK IDWS XUA test client

Client for testing the IDWS XUA support in FMK (Det Fælles Medicinkort).

## Features

* Supports employee authentication by requesting a bootstrap token for accessing the following APIs:
    * FMK 1.4.4 (MedicineCard_Idws_Xua_2015_01_01). Implemented operation: GetMedicineCard.
    * FMK 1.4.4 E1 (MedicineCard_Idws_Xua_2015_01_01_E1). Implemented operation: GetMedicineCard.
    * EO 1.4.4 (EffectuationOrdering_2015_01_01). Implemented operation: GetOrderedEffectuations.
    * POR 1.4.4 E1 (PatientRegistration_2015_01_01_E1). Implemented operation: GetPatientRegistration.
* Supports the Security token service: [NSP STS3](https://www.nspop.dk/display/STS3) (currently in development at NSP).
* Can be invoked using the included commandline application or by using the library in e.g. an integration test.
* Logs STS requests and responses to a file.
* Uses the currently newest versions of CXF and Spring.

## Structure

The project is structured as a Maven project with 3 modules:
* *lib-schemas*: contains WSDL-files for using different API versions of the FMK webservice with IDWS XUA
* *lib*: library containing CXF clients for calling FMK
* *app*: an example application using the CXF clients from lib and providing the neccesarry configuration files

Note: the official FMK IDWS XUA WSDL-files are available in the project [fmk-schemas](https://github.com/trifork/fmk-schemas), but those included should be identical (but not guaranteed).

## Requirements

* Usage requires a number of configuration files and certificates. Defaults are configured to use NSP STS3 as a test user. See resources in app-module.
* An FMK webservice must be accessible. This can either be the actual FMK system or a stub. Defaults are configured to use FMK test1.
* NSP STS3 must be accessible. Defaults are configured to use NSP test1.

## Usage

### Building

The project is a multi-module Maven project and must be build from the top level:
```
mvn install
```

### Client app

An application (a runnable jar-file) named *fmkclient.jar* can be build by the app-module. To run the application (using the default configuration) use the following commands:

```
$ cd app/target
$ java -jar fmkclient.jar
```

The application has a command line argument parser. To see available options use the argument *-h*:

```
$ java -jar fmkclient.jar -h
```

To use a specific API (see the table in the section below), use the argument *-a* and *-ws* e.g.:

```
$ java -jar fmkclient.jar -a EffectuationOrdering_2015_01_01 -ws https://test1.fmk.netic.dk/proxy/services/eo_xua_144
```

### Library

The following table summarises the API names (in this library) and endpoints in FMK test1:

API name | Endpoint
--- | ---
MedicineCard_2015_01_01 | https://test1.fmk.netic.dk/proxy/services/fmk_xua_144
MedicineCard_2015_01_01_E1 | https://test1.fmk.netic.dk/proxy/services/fmk_xua_144_E1
EffectuationOrdering_2015_01_01 | https://test1.fmk.netic.dk/proxy/services/eo_xua_144
PatientRegistration_2015_01_01_E1 | https://test1.fmk.netic.dk/proxy/services/por_xua_144_E1


The lib-module can be used as a library e.g. for integration tests. This requires Spring and can be done by importing the configuration class *IDWSXUALibSpringConfiguration*.
Several configuration and certificate files must be provided. A Spring component implementing `XUAProperties` must me made available in the Spring context. See the app-module as an example of usage.
