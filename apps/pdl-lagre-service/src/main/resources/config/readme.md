Konfigurasjon og oppsett er hentet fra PDL-repo og Sok-applikasjon

# Opprett / oppdater opensearch index for PDLDokumenter

gjøres automatisk ved oppstart av applikasjonen dersom indeksen ikke finnes.

## Oppdatering av synonymlister

Dagens synonymordlister er hentet fra SSB (kommune og bydel) og wikipedia (landkoder, norsk og engelsk).
Det er sikkert mulig å hente de fra felles kodeverk, det viktige er bare at kodeverkene er oppdaterte og
at man enkelt får bygget opp listene med alle språk.

For øyeblikket har vi også med historiske verdier på bydel og kommune, om disse er nødvendig er usikkert men
det kan være gamle fritekst adresser hvor man har skrevet f.eks. Akershus som nå er Viken eller lignende.

Synonymlistene er bare en kommaseparert liste med alle ord som er synonym for det samme per linje.

Kilder:

<https://www.ssb.no/klass/klassifikasjoner/131>
<https://www.ssb.no/klass/klassifikasjoner/103>

<https://no.wikipedia.org/wiki/ISO_3166-1_alfa-3>
<https://en.wikipedia.org/wiki/ISO_3166-1_alpha-3>