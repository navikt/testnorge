# Dolly
Prosjekt for å opprette og konfigurere testpersoner knyttet til fellesregistrene i NAV.

## Dokumentasjon
### Swagger
Swagger finnes under /swagger-ui.html

## Lokal kjøring

### Javascript
- Følg oppskriften i Java, denne kreves for å kjøre Dolly lokalt
- Kjør applikasjonen med npm start (fra ./src/main/js)

**NB: Legg til i .npmrc filen for å kjøre fra utviklerimage:**

```
https-proxy=http://155.55.60.117:8088/
proxy=http://155.55.60.117:8088/
registry=http://registry.npmjs.org/
no-proxy=*.adeo.no
strict-ssl=false
```

### Java
For å kjøre lokalt (DollyFrontendApplicationStarter) må active profile settes til `dev`. I tillegg, må cloud vault token 
hentes fra Vault. Vault token hentes ved at man logger inn i Vault, trykker på nedtrekksmenyen oppe til høyre, og 
trykker på "Copy token".

Disse verdiene fylles deretter inn i VM Options på IDE:

Run -> Edit Configurations -> VM Options 

```
-Dspring.cloud.vault.token=(Copy token fra Vault)
-Dspring.profiles.active=dev
```

#### Utviklerimage
For å kunne gjøre kall mot NAIS apper fra utviklerimage, må nav truststore settes opp og følgende verdier må
også legges til i VM Options:

```
-Djavax.net.ssl.trustStore=C:\path\to\truststore
-Djavax.net.ssl.trustStorePassword=(Passord)
```

##### Legge til sertifikat i truststore:
Dersom det dukker opp RunTimeException under oppstart kan det være at du mangler sertifikat i din truststore.

Sertifikat kan hentes fra [Microsoft Login Cert](https://login.microsoftonline.com/62366534-1ec3-4962-8869-9b5535279d0b/login) (Ignorer feilmeldingen om POST)

For Mac:
- Trykk på hengelåsen til venstre for URL og klikk deretter på sertifikat

 ![Microsoft Sertifikat](docs/assets/microsoft_keychain.png)

- Klikk på pilen ved siden av detaljer og bla helt ned

 ![Sertifikat Detaljer](docs/assets/cert_details.png)

- Ved å trykke på linken vil sertifikat lastes ned og man kan deretter legge denne til i Truststore ved hjelp av kommandoen:
```
keytool -import -trustcacerts -alias MicrosoftLoginCert -file DIN_DOWNLOAD_DIR/DigiCertSHA2SecureServerCA.crt -keystore PATH_TIL_DIN_KEYSTORE.jts
```

 ![Sertifikat Download](docs/assets/cert_download.png)
