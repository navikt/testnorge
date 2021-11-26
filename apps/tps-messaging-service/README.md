---
layout: default
title: TPS Messaging Service
parent: Applikasjoner
---

# TPS-messaging-service
TPS-messaging-service gir mulighet å sende og motta XML-meldinger mot TPS MQ-køer, samt lese XML-innhold fra TPS servicerutiner over CICS.
 
## Swagger
Swagger finnes under [/api](https://testnav-tps-messaging-service.dev.intern.nav.no/swagger) -endepunktet til applikasjonen.

## Lokal kjøring
Ha naisdevice kjørende og kjør TpsMessagingServiceApplicationStarter med følgende argumenter:
```
--add-opens java.base/java.lang=ALL-UNNAMED 
-Dspring.profiles.active=local
-Dspring.cloud.vault.token=[kopier token fra vault]
```

På Naisdevice er det ikke åpnet opp for kjøring mot MQ. Det er mulig å teste mot et Dockerimage som innholder en test-MQ,
i hht denne lenke:
https://developer.ibm.com/tutorials/mq-connect-app-queue-manager-containers/

Kommandoer for Docker:
```
testnorge % docker ps
CONTAINER ID   IMAGE              COMMAND            CREATED        STATUS        PORTS                                                      NAMES
e62a6dead67e   ibmcom/mq:latest   "runmqdevserver"   28 hours ago   Up 28 hours   0.0.0.0:1414->1414/tcp, 0.0.0.0:9443->9443/tcp, 9157/tcp   recursing_chatelet

testnorge % docker logs e62a6dead67e
...

testnorge % docker exec -it e62a6dead67e sh

sh-4.4$ runmqsc
5724-H72 (C) Copyright IBM Corp. 1994, 2021.
Starting MQSC for queue manager QM1.

DIS QSTATUS(DEV.QUEUE.1)
     1 : DIS QSTATUS(DEV.QUEUE.1)
AMQ8450I: Display queue status details.
   QUEUE(DEV.QUEUE.1)                      TYPE(QUEUE)
   CURDEPTH(1)                             CURFSIZE(1)
   CURMAXFS(2088960)                       IPPROCS(0)
   LGETDATE( )                             LGETTIME( )
   LPUTDATE( )                             LPUTTIME( )
   MEDIALOG( )                             MONQ(OFF)
   MSGAGE( )                               OPPROCS(0)
   QTIME( , )                              UNCOM(NO)

```

### Utviklerimage
I utviklerimage brukes ikke naisdevice og du må legge til følgende ekstra argumenter:
```
-Djavax.net.ssl.trustStore=[path til lokal truststore]
-Djavax.net.ssl.trustStorePassword=[passord til lokal truststore]
```
