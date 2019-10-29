# SyntRest API
Syntrest kontrollerer start og stopp av de forskjellige synt-pakkene på NAIS.

## Hvorfor et eget SyntRest lag?
Mange av maskinlærings-modellene for syntetisering av ulike miljøer tar opptil flere Gb med lagringsplass. 
Dette har vist seg vanskelig å senke, siden det i [BeAn](#) brukes tredjeparts-biblioteker (scikit-learn) med ferdige 
moduler som tar opp plass. 

Når en applikasjon deployes på kubernetes-clusteret til NAIS spesifiserer man minimum påkrevd minne, slik at 
applikasjonen alltid vil kreve minst så mye ressurser av clusteret så lenge den kjører. 
Siden generering av syntetiske testdata kun skjer når det blir gjort et kall til en synt-applikasjon, er det klart at 
disse synt-appliakasjonene ikke trenger å være oppe og kjøre på clusteret hele tiden (og ta opp unødvendige ressurser). 

Løsningen på dette problemet er en egen SyntRest-applikasjon som dynamisk deployer og fjerner synt-applikasjoner 
"on the fly" etter behov, i tillegg til å være applikasjonen man gjør alle api-kall til, siden de blir routet videre 
til endepunkter i synt-applikasjonene.

## Struktur


## Hvordan legge til Synt-Pakker
De filene som må endres når man legger til synt-pakkene er:

 - [SyntController](src/main/java/no/nav/registre/syntrest/controllers/SyntController.java)