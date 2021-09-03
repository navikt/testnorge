import React from 'react'

const LoginPage = () => {
	return (
		<div>
			<h1>Innlogging</h1>
			<ul>
				<li>
					<a href="/oauth2/authorization/aad">Logg inn med NAV e-post</a>
				</li>
				<li>
					<a href="/oauth2/authorization/idporten">Logg inn med BankId</a>
				</li>
			</ul>
		</div>
	)
}

export default LoginPage
