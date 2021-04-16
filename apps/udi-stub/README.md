---
layout: default
title: UDI Stub
parent: Applikasjoner
---

Master: [![CircleCI](https://circleci.com/gh/navikt/udi-stub/tree/master.svg?style=svg)](https://circleci.com/gh/navikt/udi-stub/tree/master)
# UDI-stub

UDI-stub stubs the services NAV uses which are exposed by UDI. The application also provides REST endpoints to store new people in the database.

## Usage

The application exposes a SOAP service which provides immigration statuses for people, the data is defined through the REST interface, and is not connected to the Norwegian immigration service. 

## Documentation

`/swagger-ui.html`

`WSDL: /ws`

## Deployment

The application can be deployed locally with an in memory h2 database. There is also a main class that is implemented with a vault and postgresql integration.

Further integrations can be implemented by creating a new submodule.
