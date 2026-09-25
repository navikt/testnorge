# Dollystatus

[Dollystatus](https://dollystatus.intern.dev.nav.no/) sjekker Dollys fagsystemer ved å opprette, verifisere og rydde testdata. Siden krever intern tilgang og innlogging med Nav-bruker via Azure AD.

## Slik kjøres sjekkene

- Sidebesøk starter sjekker dersom cache har utløpt (cache med varighet på én time). Ingen sjekker starter automatisk uten besøk.
- Både vellykkede og feilede resultater caches for å ikke spamme endepunkt. 
- Dersom to eller flere besøkende går inn samtidig, vil de dele pågående kjøring i samme appinstans istedenfor å starte en hver. 
- Manuell omkjøring er tilgjengelig fem minutter etter forrige sjekk.
- Frontend poller hvert andre sekund og viser fremdrift og resultat per fagsystem og miljø, med Q1/Q2 der systemet støtter det. SSE er et mulig forbedringspunkt her, men ble sløyfet for enklere oppsett i første omgang.
- PDL klargjøres først. Deretter kjøres Tags, Kontoregister, Pensjon, AAREG og Inntektstub før øvrige systemer. PDL-opprydding kjøres sist.
- Innen hver fase kjører opptil fire uavhengige grupper samtidig, inkludert tekniske sjekker. Hver gruppe venter på opprydding før neste test starter. Miljøene i samme fagsystem kjøres etter hverandre.
- Pensjon-testene deler én gruppe. Arena og Arbeidssøkerregisteret deler en annen, mens NOM, Skjermingsregister og TPS Messaging egenansatt deler en tredje. Dette hindrer samtidige endringer i relaterte testdata. PDL-opprydding venter på alle gruppene.
- Opprydding får inntil tre nye forsøk ved feil som kan prøves på nytt. Systemer uten egnet opprydding har kun teknisk status for å ikke fylle opp med data.
- Eksisterende data på den dedikerte testidenten ryddes med fagsystemets slette- eller avslutningskall før ny oppretting. Oppryddingen etterkontrolleres, og preflight kjøres på nytt. Ved feil stoppes denne testen med oppryddingsfeil og vanlig Slack-varsling. Oppretting prøves ikke før oppryddingen er bekreftet.

Fagsystemenes klienter og kommandoer ligger i egne mapper under `src/main/java/no/nav/testnav/apps/statusfrontend/fagsystem`. Aktivering, tidsfrister og adresser styres i [application.yml](src/main/resources/application.yml).

Ved feil i fagsystemtestene logger koordinatoren kjørings-ID, fagsystem, miljø, fase, feiltype og HTTP-status når den er tilgjengelig. Opprettings- og oppryddingsfeil logges separat, slik at en oppryddingsfeil ikke skjuler den opprinnelige feilen. Payload, responsbody og token logges ikke.

NOM bruker startdato to dager tilbake og sluttdato i går, slik at testen kan gjentas samme dag. Ved gjenoppretting godtas også startdatoen fra preflight, men bare på samme ressurs-ID og med forventet person og navn. En eksisterende aktiv ressurs avsluttes først. Oppryddingen kontrollerer sluttdato og ressurs-ID før testen fortsetter.

Brregstub rydder rolleoversikten for testidenten og testorganisasjonen. Organisasjonen slettes bare hvis alle registrerte roller tilhører testidenten, uavhengig av hvilken dato restene ble opprettet. Roller for andre personer, manglende miljøstøtte og ugyldige oppslag gir fortsatt stopp, ikke en bredere sletting.

Skjermingsregisterets oppslag returnerer bare aktive skjerminger. Etter opprydding godtas derfor HTTP 404 eller en avsluttet skjerming med forventede testdata. Verifisering etter oppretting krever fortsatt aktiv skjerming. Ved verifiseringstimeout logges siste observerte status som boolske verdier, uten persondata.

**Testidenten er rekvirert og dedikert til Dollystatus.** Personen angitt i `functional-test.pdl.ident` slettes fra pdl-forvalter ved opprydding etter fullført preflight, også hvis den fantes før kjøringen. 

Cache og kjørelås ligger i minnet og nullstilles ved restart. [Nais-manifestet](config.yml) bruker én replika; flere eventuelle instanser deler ikke cache eller kjørelås.

Kjøringshistorikk beholdes i 24 timer etter at kjøringen er ferdig. Utløpte kjøringer fjernes ved oppslag, oppstart eller fullføring av en kjøring, uten en bakgrunnsjobb. Oppslag på utløpte kjørings-ID-er gir HTTP 404. Pågående kjøringer og fagsystemenes statuscache slettes ikke av denne oppryddingen.

## Lokal kjøring

Du trenger Java 25, Node.js, pnpm og tilgang til interne Nav-tjenester. Private npm-pakker krever GitHub-token i brukerens `.npmrc`.

Start backend fra `apps/dollystatus`:

Start frontend i en annen terminal, fra `apps/dollystatus/src/main/js`:

```bash
pnpm install --frozen-lockfile
pnpm start
```

Åpne `http://localhost:3000`. Vite videresender API- og innloggingskall til backend på port 8080. Lokal innlogging går via `dolly-auth-local`.

Lokalprofilen aktiverer **kun PDL** og bruker `testnav-pdl-forvalter-dev`. Deployet profil bruker `testnav-pdl-forvalter` og aktiverer alle konfigurerte sjekker, inkludert tekniske statuser. En vellykket lokal kjøring verifiserer derfor ikke hele den deployede flyten.

## Deploy og Slack

Appen deployes til `dev-gcp`, namespace `dolly`, som `testnav-dollystatus`. [Workflowen](../../.github/workflows/app.dollystatus.yml) bruker `config.yml` og Spring-profilen `prod`.

Før deploy må følgende være på plass:

- Downstream-tjenestene må ha deployede tilgangsregler som tillater `testnav-dollystatus`. Reglene finnes i deres Nais-manifester; det er ikke nok at de ligger i Git.
- Fullmakt krever oppdatert `dolly-proxy` med ingressen `https://fullmakt.intern.dev.nav.no` og TokenX-audience `dev-gcp:repr:fullmakt`.
- Secreten `dollystatus-slack-secret` må finnes i `dev-gcp/dolly`. Manifestet krever den, også når Slack-varsling er deaktivert.
- Slack-agenten må være invitert til kanalen `dolly-status`. Denne gjenbrukes og kan finnes under "add agents and apps" og deretter søke etter "Dolly Rapportering".

Secreten skal inneholde disse verdiene for å aktivere varsling:

| Nøkkel | Verdi |
| --- | --- |
| `SLACK_CHANNEL` | `*CHANNEL ID*` |
| `SLACK_TOKEN` | Bot-token med tillatelse til å sende meldinger i kanalen |
| `DOLLYSTATUS_SLACK_ENABLED` | `true` |

Slack varsler når en utført sjekk feiler, inkludert ved oppryddingsfeil eller teknisk status DOWN. Samme system og miljø varsles ikke på nytt før sjekken har lykkes og deretter feiler igjen. Blokkerte og ikke-kjørte tester gir ikke varsel, og det sendes ingen friskmelding per system.

Etter en full kjøring der alle aktiverte sjekker lykkes, sendes én grønn melding: «Alle dollystatus tester kjørte vellykket!» Dette krever vellykket opprydding og teknisk status UP. Manuelle enkeltkjøringer og kjøringer som hopper over sjekker med gyldig cache, gir ingen grønn melding. Varsling er deaktivert som standard. Pass på at token ikke legges i Git.

Etter deploy: åpne siden med en autentisert bruker og kontroller resultater, opprydding og Slack-varsling. Første besøk starter de aktiverte sjekkene.

## Tester

Fra `apps/dollystatus`:

```bash
./gradlew test dollyValidation
```

Fra `apps/dollystatus/src/main/js`:

```bash
pnpm test
pnpm build
```
