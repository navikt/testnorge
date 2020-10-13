import React from 'react'

type VarslingId = {
	varslingId: string
}

export const VarslingerTekster = ({ varslingId }: VarslingId) => {
	if (!varslingId) return null

	const dollySlack = (
		<a href="https://nav-it.slack.com/archives/CA3P9NGA2" target="_blank">
			#dolly
		</a>
	)
	const brukerveiledning = (
		<a href="https://navikt.github.io/dolly-frontend/" target="_blank">
			her
		</a>
	)
	const vilkaar = (
		<a href="https://confluence.adeo.no/pages/viewpage.action?pageId=313339435" target="_blank">
			her
		</a>
	)

	switch (varslingId) {
		case 'VELKOMMEN_TIL_DOLLY':
			return (
				<>
					<h1>Velkommen til Dolly</h1>

					<h2>Vilkår for bruk av Dolly</h2>
					<p>
						Før du tar i bruk Dolly ønsker vi at du leser og setter deg inn i brukervilkårene våre.
						De finner du {vilkaar}, eller under Dokumentasjon i menyen.
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
		default:
			return null
	}
}
