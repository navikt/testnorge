const logoutBruker = (feilmelding?: string) => {
	const runningLocalOrIdporten =
		window.location.hostname.includes('localhost') || window.location.hostname.includes('idporten')

	const extractFeilmelding = (stackTrace: string) => {
		if (stackTrace?.includes('miljoer')) {
			return 'miljoe_error'
		} else if (stackTrace?.includes('current')) {
			return 'azure_error'
		} else {
			return 'unknown_error'
		}
	}

	window.location.href = runningLocalOrIdporten
		? '/logout' + (feilmelding ? '?state=' + extractFeilmelding(feilmelding) : '')
		: '/oauth2/logout'
	console.error(feilmelding)
}
export default logoutBruker
