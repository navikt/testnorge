import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import NavButton from '~/components/ui/button/NavButton/NavButton'

export default () => {
	const [redirectToNavLogin, setRedirectNav] = useBoolean()
	const handleNavClick = () => {
		setRedirectNav()
	}

	if (redirectToNavLogin) {
		let url = location.protocol + '//' + location.host + '/oauth2/authorization/aad'
		location.replace(url)
	}

	return (
		<div className="login-container">
			<div className="login-modal">
				<h1>Velkommen til Dolly</h1>
				<h3>
					En søt liten velkomsttekst med bittelitt info om innlogging. Lorem ipsum dolor sit amet,
					consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna
					aliqua.
				</h3>
				<NavButton className="login-modal_button-nav" type="hoved" onClick={handleNavClick}>
					Logg inn med NAV-epost
				</NavButton>
				<NavButton
					className="login-modal_button-bankid"
					type="hoved"
					disabled={true}
					title="Kommer snart"
				>
					Logg inn med BankId
				</NavButton>
			</div>
		</div>
	)
}
