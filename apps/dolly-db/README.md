A dummy application deployed as multiple NAIS apps in order to provide easy access to the NAIS managed databases.

Originally contains one `RedirectToHealthController`, to provide NAIS with a minimum living application.

No need to run `DatabaseApplication` locally, but in any case:
* Use Spring profile `local`, for human readable log output.
* Swagger at http://localhost:8080/swagger, such as it is.