---
title: Hvordan logge inn i Dolly og opprette Z-bruker (testbruker i IDA-rutinen)
layout: default
nav_order: 4
---

# {{ page.title }}

Bruk av Dolly selvbetjeningsløsning krever pålogging som testbruker - Z-bruker - med rettigheter gitt i verktøyet IDA. Disse testbrukerne kan tildeles andre og flere rettigheter enn man har med NAVIdenten i produksjon. Testerne kan på denne måten teste ny funksjonalitet i systemene med ulike roller og tilganger. 

Dolly kan du åpne fra fra utviklerimage/Citrix: https://dolly.nais.preprod.local/ 

eller direkte fra laptop: https://dolly.nais-dev-fss.adeo.no/

Brukerveiledning: [Kom i gang med Dolly](../index.md)

## Innhold
{: .no_toc .text-delta }

1. TOC
{:toc}

---
    
 
## Logge inn i Dolly

### Åpne Dolly på Laptop

* Åpne Dolly i nettleseren ved å trykke på lenken: https://dolly.nais-dev-fss.adeo.no/ 

* Logg på med AD-brukeren din (NAVident og passord)

![Adeo Login](assets/adeo_login.png)


* Deretter må du logge på med Z-bruker → se 1.3 for nærmere beskrivelse.

![Openam Login](assets/openam_logginn.png)

---

### Åpne Dolly fra utvikler image/Citrix
Dolly åpnes i Chrome SKSS som du finner under "NAV Applikasjoner" 

![Chrome SKSS](assets/windows_search.png)

![Chrome SKSS](assets/chrome_skss.png)

Dolly kan også åpnes fra ‘VMWare View Client’ (Utviklerimage). 

Velg nettleseren Chrome  fra startmenyen i ‘VMWare View Client’. Åpne ny fane i Chrome og legg inn URL.

---

### Pålogging
Åpne Dolly i Chrome SKSS ved å trykke på lenken eller legg inn: https://dolly.nais.preprod.local/

![Openam Login](assets/openam_logginn.png)

I påloggingsvinduet legger du inn Z-brukeren og passordet ditt

Se punkt 2 under for hvordan du oppretter en Z-bruker

![Oprett Z Bruker](assets/testdatagrupper.png)
* Beskrivelse av hvordan du bruker Dolly finner du her: [Kom i gang med Dolly](../index.md)


---


## Opprette Z-bruker i IDA
For å kunne opprette en egen Z-bruker trenger du tilgang til IDA. Dette bestilles via identansvarlig for enheten du tilhører.  

Merk at for bruk av en Dolly-testperson i Gosys, må egne egenskaper legges på Z-bruker i IDA rutinen. Se nærmere beskrivelse under.

### Logg inn i IDA
* Fra Chrome SKSS logg inn i IDA: https://ida.adeo.no/#/login


![Ida Login](assets/ida_login.png)

* Legg inn 
    * Brukernavn: din NAV brukerident
    * Passord: ditt nav passord
    * Trykk på "Logg inn"
* Velg 'Nye identer'

![Nye Identer](assets/nye_identer.png)

* Velg testidenter → velg en av testidentene som vises i nedtrekkslisten

![Velg Testidenter](assets/velg_testident.png)

* Prosjekt: Gi prosjektet ditt et navn. Min 3 og max 30 tegn. Store og små bokstaver, samt tall kan benyttes. Ingen spesialtegn eller mellomrom.

![Velg Identer](assets/veld_identer.png)

* Passord: skriv inn ønsket passord for din Zident. Passordet må bestå av minst 8 tegn og må inneholde store og små bokstaver, samt tall.
* Velg gruppenavn. Skriv inn: 0000-GA-TPSF-LES 
    * Trykk på pluss-tegnet og gruppen legges under Gruppeliste
    
![Legg til Ident](assets/leggtil_ident.png)

Trykk på «Lagre ident»

![Lagre Ident](assets/lagre_ident.png)

* Du har nå opprettet en Z-bruker som kan brukes til innlogging i Dolly (klar om ca.15 min)

---

### Opprette Z-bruker for test i Gosys
* Logg inn og opprett ny ident som beskrevet i 2.1. Dersom du allerede har har en Z-bruker, kan nye grupper legges til ved å følge stegene beskrevet i pkt. 3 under. 
* For å teste i Gosys må Z-brukeren din å være tilknyttet flere AD-grupper
    * Velg de gruppenavnene du trenger og legg til ved å trykke på +

![Lagre Ident](assets/nye_identer_2.png)

Trykk på «Lagre ident» 


* **Velg Norg**
    * Velg Testmiljø: Q2 (eller et annet testmiljø)
    * **VIKTIG!** Zidenten må også opprettes i Q1. Dette gjelder selv om du ikke skal bruke Q1. Se * 
    * Huk av på Zidenten du har valgt tidligere og lagt inn grupper på: (f.eks. Z992868)
    * Trykk på pil ‘>’ for å overføre Zidenten til ‘Mine Norg identer’

![Norg DB](assets/focus_norg_db.png)

*  Huk av for Zidenten under ‘Mine Norg identer’
*  Velg ‘Administrer roller’

![Administrer Roller](assets/focus_administrer_roller.png)

* Legg Zidenten din inn i ‘Identer’ ( eks. Z992868)
* Legg til Enheter
* Legg til Roller 
* Du kan skrive inn en og en rolle, eller trykk i feltet og hold enter knappen nede, da velges alle roller
* Trykk ‘Lagre roller’
    * Det tar ca. 15 min til Zidenten er klar til å brukes i f.eks. Dolly

![Legg til Identer](assets/administrer_roller_2.png)

*   *...Autoriseringen går via ABAC og til AXSYS, som gjør valideringen i Q1. Zidenten legges inn i ønsket Q-miljø men må alltid i tillegg legges inn i Q1 miljøet, eller T1 hvis du jobber i T-miljøet. Alle ABAC miljøene i Q går mot AXSYS Q1. og ABAC miljøene i T går mot AXSYS T1. Slik får vi tilgang til å se genererte dokumenter i Gosys.

---

## Endre eksisterende Z-bruker
### Legge til Grupper
* **Velg 'Mine AD-identer'**
* Velg den Z-identen du vil ha grupper på og klikk på den


![Velg Testident](assets/administrasjon_testidenter.png)

* Skriv/legg inn ønskede grupper og trykk 'Legg til'

![Legg Til Ident](assets/oensket_gruppenavn.png)

---

### Legge til Enheter og Roller
* **Velg 'Norg'**
* Huk av for den Z-identen du vil legge til Enheter på
* Trykk 'Administrer roller'

![Mine Identer](assets/mine_identer.png)

* Skriv inn Z-idneten din i 'Identer'
* Velg Enheter
* Velg roller
* Lagre roller

![Lagre Identer](assets/administrer_roller.png)

---

### Utvide utløpsdato for Z-bruker og passord
* **Velg 'Mine AD-identer'**
    * Velg den Z-identen du vil utvide utløpsdato for 
    
![Utvide Utloepsdato](assets/administrasjon_testidenter.png)

* **Utvide utløpsdato for Z-ident**
    * Trykk på 'Utvid Utløpsdato' og utløpsdatoen forlenges med ca.2mnd 
    
![Utvid](assets/utvide_utloepsdato.png)

* **Utvide passord for Z-ident**
    * Skriv inn nytt passord og trykk på 'Endre passord'. Passordet forlenges med ca.2mnd 

![Endre Passord](assets/utvide_passord.png)

---

[Se i GitHUb](https://github.com/navikt/dolly-frontend/){: .btn }