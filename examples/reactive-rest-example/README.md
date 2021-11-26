[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=testnav-reactive-rest-example&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=testnav-reactive-rest-example)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=testnav-reactive-rest-example&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=testnav-reactive-rest-example)

# reactive-rest-example
Dette er en eksmepl på en reactiv rest api. Dette er ment som en standard som alle apper brude følge.

Alle på teamet er ansvarlig for at vi sammen er enig om en standard.

## lokal kjøring

Start `ReactiveRestExampleApplicationStarter` med følgenede props:

```
-Dspring.profiles.active=local -Dspring.cloud.vault.token=<<VAULT_TOKEN>>
```
