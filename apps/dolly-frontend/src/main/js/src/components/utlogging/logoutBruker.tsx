const logoutBruker = (feilmelding?: string) => {
	const runningLocalOrIdporten =
		window.location.hostname.includes('localhost') || window.location.hostname.includes('idporten')

	window.location.href = runningLocalOrIdporten ? '/logout' : '/oauth2/logout'
	console.error(feilmelding)
}
export default logoutBruker
