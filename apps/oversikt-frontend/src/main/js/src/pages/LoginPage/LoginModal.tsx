import React, { useState } from 'react'
import { Redirect } from 'react-router-dom'
import { Hovedknapp, Knapp } from 'nav-frontend-knapper'

export default () => {
	const [redirectToNavLogin, setRedirectNav] = useState<boolean>(false)
	const handleNavClick = () => {
		setRedirectNav(!redirectToNavLogin)
	}

	if (redirectToNavLogin) return <Redirect to={'/oauth2/authorization/aad'} />

	return (
		<div className="login-container">
			<div className="login-modal">
				<h1>Velkommen til Dolly</h1>
				<h3>
					En søt liten velkomsttekst med bittelitt info om innlogging. Lorem ipsum dolor sit amet,
					consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna
					aliqua.
				</h3>
				<Hovedknapp className="login-modal_button-nav" type="hoved" onClick={handleNavClick}>
					Logg inn med NAV-epost
				</Hovedknapp>
				<Hovedknapp
					className="login-modal_button-bankid"
					type="hoved"
					disabled={true}
					title="Kommer snart"
				>
					Logg inn med BankId
				</Hovedknapp>
			</div>
		</div>
	)
}
