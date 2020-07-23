# Ikonbruk i Dolly

Kort veiledning for bruk av ikoner i Dolly. Prøv å benytt et ikon som allerede finnes i NAV sin ikon-base. Her finner du en [web-app som viser alle ikonene](https://app-q6.adeo.no/nav-frontend-ikoner/).
Alternativ oversikt over ikoner finnes i nav-frontend-ikoner-backend [github repo](https://github.com/navikt/nav-frontend-ikoner-backend/blob/master/src/main/resources/static/api/icons/Line%20Version/ikon%20oversikt-%20outline%20.png).

## Custom ikoner

- Legg til SVG'en inn under `/custom` folderen
- Importer ikonet for bruk

## Ikoner fra NAV ikon-base

På sikt skal måten man benytter NAV ikoner på byttes ut med en pakke-dependency. Når dette blir gjort kan man enkelt direkte importere de ikonene man skal bruke. I skrivende stund må ikoner kopieres inn i prosjektet. For å gjøre overgangen lettere når den tid kommer ønsker vi å beholde full path til ikonet, slik at vi lett kan bytte ut med ikoner fra pakke-dependency når den tid kommer.

### Last ned ikoner:

Ikoner kan lastes ned herfra. **Behold full path til ikon!** (Se struktur allerede i `/src/assets/icons/nav-ikoner/...`)

https://github.com/navikt/nav-frontend-ikoner-backend/tree/master/src/main/resources/static/api/icons

### Bruk

- Bruk web-app for å finne nye ikoner
- Kopier hele pathen og ikonet inn i prosjektet
- Importer ikonet for bruk
