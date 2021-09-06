import React from 'react'
import NavButton from '~/components/ui/button/NavButton/NavButton'

export default () => {
	return (
		<div className="login-container">
			<div className="login-modal">
				<h1>Velkommen til Dolly</h1>
				<h3>
					En søt liten velkomsttekst med bittelitt info om innlogging. Lorem ipsum dolor sit amet,
					consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna
					aliqua.
				</h3>
				<NavButton className="login-modal_buttons" type="hoved" onClick={() => {}}>
					Logg inn med NAV-epost
				</NavButton>
				<NavButton className="login-modal_buttons" type="hoved" onClick={() => {}} disabled={true}>
					Logg inn med BankId
				</NavButton>
			</div>
		</div>
	)
}
