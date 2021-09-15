import React from 'react'
import Api from '~/api'

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
				<li>
					<button
						onClick={(e) => {
							e.preventDefault()
							Api.fetch('/logout', { method: 'POST' }).then((response) => {
								if (response.redirected) {
									window.location.href = response.url
								}
							})
						}}
					>
						Logg ut
					</button>
				</li>
			</ul>
		</div>
	)
}

export default LoginPage
