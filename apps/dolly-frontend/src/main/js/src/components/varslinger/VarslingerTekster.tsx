import React, { ReactNode } from 'react'
import { BodyLong, Heading, Link, List, VStack } from '@navikt/ds-react'
import { dollySlack } from '@/components/dollySlack/dollySlack'

type VarslingTekst = {
	tittel: string
	innhold: ReactNode
}

export const hentVarslingTekst = (varslingId: string): VarslingTekst | null => {
	if (!varslingId) {
		return null
	}

	const brukerveiledning = (
		<Link href="https://navikt.github.io/testnorge/testnav/latest/index.html" target="_blank">
			her
		</Link>
	)
	const retningslinjer = (
		<Link
			href="https://navikt.github.io/testnorge/testnav/latest/index.html#dolly_retningslinjer"
			target="_blank"
		>
			her
		</Link>
	)

	switch (varslingId) {
		case 'VELKOMMEN_TIL_DOLLY':
			return {
				tittel: 'Velkommen til Dolly',
				innhold: (
					<VStack gap="space-12">
						<Heading level="2" size="small">
							Prinsipper for bruk av Dolly
						</Heading>
						<BodyLong>
							Dolly selvbetjening brukes for å lage egne data til randtilfeller og spesialbehov.
							Syntetiske personer gis egenskaper fra registre og fagsystemer - f.eks. sivilstand,
							inntekt, statsborgerskap. Brukere av Dolly selvbetjening har selv ansvar for å følge
							følgende prinsipper ved oppretting av syntetiske data:
						</BodyLong>
						<List as="ul">
							<List.Item>
								Dolly tilbyr et stort utvalg attributter som kan settes på en person. Selv om det
								kun er syntetiske personer som blir laget i Dolly, finnes det allikevel en risiko
								for å skape gjenkjennbare personer, ved å kombinere verdier som er svært spesifikke
								for en ekte person. Derfor må du aldri ta utgangspunkt i reelle personer når du
								oppretter syntetiske personer - alle verdier som settes for å dekke behovet må være
								tilfeldig valgt.
							</List.Item>
							<List.Item>
								Når du er logget inn i Dolly har du tilgang til alle brukeres grupper og personer.
								Ikke gjør endringer på eller slett andres grupper eller personer uten at dette er
								avtalt med eier.
							</List.Item>
							<List.Item>
								Dolly selvbetjening har personlig innlogging med Azure AD. Ikke del
								påloggingsinformasjonen din med andre brukere. Brukere som mangler tilgang til Dolly
								bes kontakte teamet for å få hjelp til å ordne dette.
							</List.Item>
							<List.Item>
								Snakk med oss! Skulle det være noe, vil vi gjerne at du kontakter oss på #dolly på
								Slack. Innspill, ønsker, meldinger om feil, osv. hjelper oss med å gjøre Dolly
								bedre, og kommer alle brukerne til gode.
							</List.Item>
						</List>
						<BodyLong>
							Du kan lese mer om retningslinjer for bruk av testdata i NAV {retningslinjer}, eller
							under Brukerdokumentasjon i menyen.
						</BodyLong>

						<Heading level="2" size="small">
							Hjelp til å bruke Dolly
						</Heading>
						<BodyLong>
							Om dette er første gang du bruker Dolly, vil du sikkert ta en titt på
							brukerveiledningen vår. Den finner du {brukerveiledning}, eller under
							Brukerdokumentasjon i menyen.
						</BodyLong>
						<BodyLong>
							Kontakt oss gjerne på {dollySlack} evt <a href="mailto:dolly@nav.no">dolly@nav.no</a>dersom det er noe du lurer på eller trenger hjelp
							med.
						</BodyLong>
					</VStack>
				),
			}
		case 'AZURE_AD_INNLOGGING':
			return {
				tittel: 'Ny innlogging i Dolly',
				innhold: (
					<VStack gap="space-12">
						<Heading level="2" size="small">
							Z-bruker er erstattet med personlig innlogging
						</Heading>
						<BodyLong>
							Som du sikkert la merke til da du logget deg inn i Dolly, er den gamle innloggingen
							med Z-bruker fjernet til fordel for personlig innlogging med AzureAD. Dette er gjort
							for å gjøre det lettere å ta i bruk Dolly, gi deg bedre oversikt over dataene dine, og
							lettere kunne hjelpe deg dersom det skulle oppstå en feil.
						</BodyLong>
					</VStack>
				),
			}
		case 'PDL_SOM_MASTER':
			return {
				tittel: 'PDL som master for bestillinger',
				innhold: (
					<VStack gap="space-12">
						<BodyLong>
							Det er gjort en større endring på bestilling av personer i Dolly: Vi har endret master
							for bestillinger fra TPS til PDL. Alle personer som bestilles vil fra nå derfor
							opprettes i PDL istedenfor TPS. Den observante Dolly-bruker vil også se en del
							endringer i bestillingsskjemaet, som er gjort for å tilpasses persondata i PDL.
						</BodyLong>
						<BodyLong>
							Blant annet er sammenknytning av relaterte personer endret, slik at du kan velge en
							eksisterende person som f.eks. partner ved bestilling. Valg av identhistorikk er også
							endret, nå vil du få opprettet en historikk ved valg av "Ny ident" som ligger under
							"Identifikasjon".
						</BodyLong>
						<BodyLong>
							Denne endringen vil også få noen sideeffekter. Om du bruker maler som har
							TPS-attributter vil ikke disse fungere som de skal. Vi anbefaler derfor at du lager en
							ny mal dersom du får varsel om at malen din er ugyldig. Det vil heller ikke være mulig
							å gjøre endringer på opprettede personer som har TPS som master.
						</BodyLong>
						<BodyLong>

						Kontakt oss gjerne på {dollySlack} evt <a href="mailto:dolly@nav.no">dolly@nav.no</a>{' '}dersom du har noen spørsmål eller kommentarer.
						</BodyLong>
					</VStack>
				),
			}
		default:
			return null
	}
}
