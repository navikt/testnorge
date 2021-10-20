import React from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'
import Alertstripe from 'nav-frontend-alertstriper'

const Advarsler = {
	organisation_error:
		'Du ble logget inn med BankId, men er ikke tilknyttet noen organisasjoner som har ' +
		'tilgang til Dolly. Ta kontakt med administrator i din organisasjon dersom du ønsker tilgang' +
		', eller velg en annen innloggingsmetode.',
	unknown_error: 'Ukjent feil oppsto. Ta kontakt med Dolly hvis denne feilen vedvarer.',
}

const getAdvarsel: () => string = () => {
	let url = location.href
	if (url.includes('login?')) {
		let urlParts = url.split('login?')
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
			</div>
		</div>
	)
}
