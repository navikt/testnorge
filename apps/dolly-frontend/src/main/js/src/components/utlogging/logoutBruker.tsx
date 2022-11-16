const logoutBruker = (feilmelding?: string) => {
	const extractFeilmelding = (stackTrace: string) => {
		if (stackTrace?.includes('miljoer')) {
			return 'miljoe_error'
		} else if (stackTrace?.includes('current')) {
			return 'azure_error'
		} else {
			return 'unknown_error'
		}
	}

	window.location.href =
		'/logout' + (feilmelding ? '?state=' + extractFeilmelding(feilmelding) : '')
	console.error(feilmelding)
}
export default logoutBruker
