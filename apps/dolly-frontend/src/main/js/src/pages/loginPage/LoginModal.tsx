import React from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Alertstripe from 'nav-frontend-alertstriper'
import NavHjelpeTekst from 'nav-frontend-hjelpetekst'

const Advarsler = {
	organisation_error:
		'En feil oppsto og du ble logget ut. Sjekk om du er tilknyttet en organisasjon som har tilgang til Dolly. ' +
		'Ta kontakt med en administrator i din organisasjon dersom tilgang mangler eller velg en annen innloggingsmetode. ' +
		'Ta kontakt med Dolly hvis feilen vedvarer.',
	unknown_error:
		'Ukjent feil oppsto og du ble logget ut. Ta kontakt med Dolly hvis feilen vedvarer.',
}

const getAdvarsel: () => string = () => {
	let url = location.href
	if (url.includes('state=')) {
		let urlParts = url.split('state=')
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
	const modalHeight = advarsel ? 400 + (advarsel.length / 88) * 20 : 350

	const redirectOnClick = (path: string) => (event: React.MouseEvent<HTMLButtonElement>) => {
		event.preventDefault()
		location.replace(location.protocol + '//' + location.host + path)
	}

	return (
		<div className="login-container">
			<div className="login-modal" style={{ height: modalHeight + 'px' }}>
				<h1>Velkommen til Dolly</h1>
				<h3>
					Dolly er NAVs selvbetjeningsløsning for å opprette syntetiske testdata. I Dolly kan du
					opprette syntetiske testpersoner med forskjellige egenskaper, og tilgjengeliggjøre
					testdataene i valgte testmiljøer.
				</h3>
				{advarsel && <Alertstripe type="advarsel">{advarsel}</Alertstripe>}
				<NavButton
					className="login-modal_button-nav"
					type="hoved"
					onClick={redirectOnClick('/oauth2/authorization/aad')}
				>
					Logg inn med NAV epost
				</NavButton>
				<NavButton
					className="login-modal_button-bankid"
					type="hoved"
					onClick={redirectOnClick('/oauth2/authorization/idporten')}
				>
					Logg inn med BankId
				</NavButton>
				<NavHjelpeTekst>
					For å ta i bruk BankId innlogging må du være tilknyttet en organisasjon som har tilgang
					til Dolly. Ta kontakt med en administrator i din organisasjon hvis tilgang mangler.
				</NavHjelpeTekst>
			</div>
		</div>
	)
}
