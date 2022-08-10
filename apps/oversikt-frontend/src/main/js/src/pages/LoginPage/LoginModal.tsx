import { Knapp } from '@navikt/dolly-komponenter/lib'
import React from 'react'

export default () => {
	const getOnClick = (path: string) => (event: React.MouseEvent<HTMLButtonElement>) => {
		event.preventDefault()
		window.location.href = path
	}

	return (
		<div className="login-container">
			<div className="login-modal">
				<h1>Oversikt login</h1>
				<p>Velg hvordan du ønsker å logge inn</p>
				<p>
					BankId innlogging er basert på Digdir testbrukere (
					<a target="_blank" href="https://docs.digdir.no/idporten_testbrukere.html">
						Trykk her
					</a>
					).
				</p>
				<Knapp className="login-modal_button-nav" onClick={getOnClick('/oauth2/authorization/aad')}>
					Logg inn med NAV-epost
				</Knapp>
				<Knapp
					className="login-modal_button-bankid"
					onClick={getOnClick('/oauth2/authorization/idporten')}
				>
					Logg inn med BankId
				</Knapp>
			</div>
		</div>
	)
}
