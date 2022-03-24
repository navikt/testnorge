import React from 'react'
import { dollySlack } from '~/components/dollySlack/dollySlack'

type VarslingId = {
	varslingId: string
}

export const VarslingerTekster = ({ varslingId }: VarslingId) => {
	if (!varslingId) return null

	const brukerveiledning = (
		<a
			href="https://navikt.github.io/testnorge/applications/dolly/brukerveiledning"
			target="_blank"
		>
			her
		</a>
	)
	const retningslinjer = (
		<a href="https://navikt.github.io/testnorge/applications/dolly/retningslinjer" target="_blank">
			her
		</a>
	)

	switch (varslingId) {
		case 'VELKOMMEN_TIL_DOLLY':
			return (
				<>
					<h1>Velkommen til Dolly</h1>

					<h2>Prinsipper for bruk av Dolly</h2>
					<p>
						Dolly selvbetjening brukes for å lage egne testdata til randtilfeller og spesialbehov.
						Syntetiske personer gis egenskaper fra registre og fagsystemer - f.eks. sivilstand,
						inntekt, statsborgerskap. Brukere av Dolly selvbetjening har selv ansvar for å følge
						følgende prinsipper ved oppretting av syntetiske testdata:
					</p>
					<ul>
						<li>
							Dolly tilbyr et stort utvalg attributter som kan settes på en person. Selv om det kun
							er syntetiske personer som blir laget i Dolly, finnes det allikevel en risiko for å
							skape gjenkjennbare personer, ved å kombinere verdier som er svært spesifikke for en
							ekte person. Derfor må du aldri ta utgangspunkt i reelle personer når du oppretter
							syntetiske testpersoner - alle verdier som settes for å dekke behovet må være
							tilfeldig valgt.
						</li>
						<li>
							Når du er logget inn i Dolly har du tilgang til alle brukeres testdatagrupper og
							testpersoner. Ikke gjør endringer på eller slett andres testdatagrupper eller
							testpersoner uten at dette er avtalt med eier.
						</li>
						<li>
							Dolly selvbetjening har personlig innlogging med Azure AD. Ikke del
							påloggingsinformasjonen din med andre brukere. Brukere som mangler tilgang til Dolly
							bes kontakte teamet for å få hjelp til å ordne dette.
						</li>
						<li>
							Snakk med oss! Skulle det være noe, vil vi gjerne at du kontakter oss på #dolly på
							Slack. Innspill, ønsker, meldinger om feil, osv. hjelper oss med å gjøre Dolly bedre,
							og kommer alle brukerne til gode.
						</li>
					</ul>
					<p>
						Du kan lese mer om retningslinjer for bruk av testdata i NAV {retningslinjer}, eller
						under Dokumentasjon i menyen.
					</p>

					<h2>Hjelp til å bruke Dolly</h2>
					<p>
						Om dette er første gang du bruker Dolly, vil du sikkert ta en titt på brukerveiledningen
						vår. Den finner du {brukerveiledning}, eller under Dokumentasjon i menyen.
					</p>
					<p>
						Kontakt oss gjerne på {dollySlack} dersom det er noe du lurer på eller trenger hjelp
						med.
					</p>
				</>
			)
		case 'AZURE_AD_INNLOGGING':
			return (
				<>
					<h1>Ny innlogging i Dolly</h1>

					<h2>Z-bruker er erstattet med personlig innlogging</h2>
					<p>
						Som du sikkert la merke til da du logget deg inn i Dolly, er den gamle innloggingen med
						Z-bruker fjernet til fordel for personlig innlogging med AzureAD. Dette er gjort for å
						gjøre det lettere å ta i bruk Dolly, gi deg bedre oversikt over testdataene dine, og
						lettere kunne hjelpe deg dersom det skulle oppstå en feil.
					</p>

					<h2>Hvor er testpersonene dine?</h2>
					<p>
						Ikke bekymre deg - testpersonene dine er ikke slettet! Men fordi de er koblet til
						Z-brukeren din må du importere dem til din personlige brukerkonto for å få tilgang til
						dem. Dette kan du gjøre første gang i testdatagruppe-oversikten, eller når som helst på
						Min side.
					</p>
				</>
			)
		//TODO: Legg inn denne varslingen i varslinger-service
		case 'PDL_SOM_MASTER':
			return (
				<>
					<h1>PDL som master for bestillinger</h1>

					<p>
						I dag er det gjort en større endring på bestilling av testpersoner i Dolly: Vi har
						endret master for bestillinger fra TPS til PDL. Alle testpersoner som bestilles vil fra
						nå derfor opprettes i PDL istedenfor TPS. Den observante Dolly-bruker vil også se en del
						endringer i bestillingsskjemaet, som er gjort for å tilpasses persondata i PDL.
					</p>
					<p>
						Denne endringen vil få noen sideeffekter. Om du bruker maler som har TPS-attributter vil
						ikke disse fungere som de skal. Vi anbefaler derfor at du lager en ny mal dersom du får
						varsel om at malen din er ugyldig. Det vil heller ikke være mulig å gjøre endringer på
						opprettede personer som har TPS som master.
					</p>
					<p>Kontakt oss gjerne på {dollySlack} dersom du har noen spørsmål eller kommentarer.</p>
				</>
			)
		default:
			return null
	}
}
