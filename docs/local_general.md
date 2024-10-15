# Kjøring lokalt
Dette er felles for alle applikasjoner som er ment å kunne kjøres lokalt.
* Bruk Spring profile `local`. **NB:** Noen applikasjoner/proxyer bruker fortsatt andre profiler, f.eks. `dev`. Sjekk og vurder å endre til standard `local` der det gjelder.
* Bruk VM options `--add-opens java.base/java.lang=ALL-UNNAMED`.
* Hvis Swagger er satt opp er det tilgjengelig på http://localhost:8080/swagger.