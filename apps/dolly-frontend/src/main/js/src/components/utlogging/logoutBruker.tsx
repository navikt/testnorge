const logoutBruker = (feilmelding?: string) => {
	const runningLocal = window.location.hostname.includes('localhost')

	const extractFeilmelding = (stackTrace: string) => {
		if (stackTrace?.includes('miljoer')) {
			return 'miljoe_error'
		} else if (stackTrace?.includes('current')) {
			return 'azure_error'
		} else {
			return 'unknown_error'
		}
	}

	window.location.href = runningLocal
		? '/logout' + (feilmelding ? '?state=' + extractFeilmelding(feilmelding) : '')
		: '/oauth2/logout'
	console.error(feilmelding)
}
export default logoutBruker
