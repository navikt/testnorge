import NavButton from '@/components/ui/button/NavButton/NavButton'
import { Alert } from '@navikt/ds-react'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'
import { top } from '@popperjs/core'
import React from 'react'

const brukerveiledning = (
	<a
		href="https://navikt.github.io/testnorge/applications/dolly/brukerveiledning.html#feil-ved-innlogging"
		target="_blank"
	>
		brukerveiledningen
	</a>
)

const Advarsler = {
	organisation_error:
		'En feil oppsto og du ble logget ut. Sjekk om du er tilknyttet en organisasjon som har tilgang til Dolly. ' +
		'Ta kontakt med en administrator i din organisasjon dersom tilgang mangler eller velg en annen innloggingsmetode. ',
	unknown_error: 'Ukjent feil oppsto og du ble logget ut. ',
	miljoe_error: 'Du er blitt logget ut. Det oppsto et problem med å hente gyldige miljøer. ',
	azure_error:
		'Du er blitt logget ut. Det oppsto et problem med å hente Azure id for innlogget bruker. ',
}

const getAdvarsel: () => string = () => {
	const url = location.href
	if (url.includes('state=')) {
		const urlParts = url.split('state=')
		if (urlParts.length === 1 || !(urlParts[1] in Advarsler)) {
			return null
		} else {
			// @ts-ignore
			return Advarsler[urlParts[1]]
		}
	}
	return null
}

export default () => {
	const advarsel = getAdvarsel()
	const modalHeight = advarsel ? 400 + ((advarsel.length + 70) / 88) * 20 : 350

	const redirectOnClick = (path: string) => (event: React.MouseEvent<HTMLButtonElement>) => {
		event.preventDefault()
		location.replace(location.protocol + '//' + location.host + path)
	}

	return (
		<div className="login-container">
			<div className="login-modal" style={{ height: modalHeight + 'px' }}>
				<h1>Velkommen til Dolly</h1>
				<h3>
					Dolly er NAVs selvbetjeningsløsning for å opprette syntetiske data. I Dolly kan du
					opprette syntetiske personer med forskjellige egenskaper, og tilgjengeliggjøre dataene i
					valgte testmiljøer.
				</h3>
				{advarsel && (
					<Alert variant={'warning'}>
						{advarsel}
						<>Sjekk {brukerveiledning} hvis feilen vedvarer eller ta kontakt med Dolly.</>
					</Alert>
				)}
				<NavButton
					className="login-modal_button-nav"
					variant={'primary'}
					onClick={redirectOnClick('/oauth2/authorization/aad')}
				>
					Logg inn med NAV epost
				</NavButton>
				<NavButton
					className="login-modal_button-bankid"
					variant={'primary'}
					onClick={redirectOnClick('/oauth2/authorization/idporten')}
				>
					Logg inn med BankId
				</NavButton>
				<Hjelpetekst placement={top} requestFeedback={false}>
					For å ta i bruk BankId innlogging må du være tilknyttet en organisasjon som har tilgang
					til Dolly. Ta kontakt med en administrator i din organisasjon hvis tilgang mangler.
				</Hjelpetekst>
			</div>
		</div>
	)
}
