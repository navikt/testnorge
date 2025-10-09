# Testnav dokumentasjon

## Hovedkonsepter

| Konsept                   | Fil / mappe                     |
|---------------------------|---------------------------------|
| Antora komponent          | `antora.yml`                    |
| Playbook (build-konfig)   | `antora-playbook.yml`           |
| Navigasjon                | `modules/ROOT/nav.adoc`         |
| Single page (alt innhold) | `modules/ROOT/pages/all.adoc`   |
| Innholdssider             | `modules/ROOT/pages/**`         |
| Bilder / assets           | `modules/ROOT/assets/images/**` |
| NPM / build               | `package.json`                  |

Single page `all.adoc` inkluderer alle andre sider og brukes som startside (med venstre meny og søk). Du kan fortsatt
navigere til enkeltsider direkte hvis ønskelig, men siden dokumentasjonen ikke er spesielt omfattende, bør en enkelt
side være nok.

## Bygg docs lokalt

Forutsetter Node 18 eller nyere.

```bash
cd docs
npm install
npm run build
```

Output genereres i `docs/build/docs/`. Åpne `index.html` i en nettleser.

## Søk

Søk leveres av Lunr via `@antora/lunr-extension` og er aktivert i playbook. Indeksen bygges automatisk ved
`npm run build`.

## Legge til ny side

1. Opprett ny fil under `modules/ROOT/pages/` (bruk underkatalog hvis naturlig)
2. Legg til innhold i AsciiDoc (`.adoc`)
3. (Valgfritt) Legg inn anker øverst: `[[mitt-anker]]`
4. Inkluder i `all.adoc` med passende `include::` og `leveloffset`
5. Oppdater `nav.adoc` hvis du vil ha den i menyen (bruk `xref:` og eventuelt anker)
6. Kjør build og verifiser

Eksempel include i `all.adoc`:

```asciidoc
[[min-seksjon]]
== Min seksjon

include::path/til/ny-fil.adoc[leveloffset=+1]
```

## Retningslinjer

- Bruk `xref:` for interne lenker
- Unngå Markdown i innholdssider (kun AsciiDoc). Denne README er et unntak for utviklere.
- Bilder legges i `modules/ROOT/assets/images/<underkategori>/` og refereres med `image::<underkategori>/fil.png[]`

## Feilsøking

| Problem             | Løsning                                                                |
|---------------------|------------------------------------------------------------------------|
| Mangler søk         | Sjekk at `@antora/lunr-extension` er installert og definert i playbook |
| Døde interne lenker | Sjekk `xref:`-mål og at fil finnes                                     |
| Mangler stil / UI   | Nettverksfeil mot UI bundle URL eller blokkert last                    |

## Ressurser

Inspirasjon til oppsettet tatt fra pdl sine docs og repo: https://pdl-docs.ansatt.nav.no/ekstern/index.html

Antora dokumentasjon: https://docs.antora.org/antora/2.3/