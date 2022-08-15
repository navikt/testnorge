const logoutBruker = (feilmelding?: string) => {
	window.location.href = '/logout' + (feilmelding ? '?state=' + feilmelding : '')
	console.error(feilmelding)
}
export default logoutBruker
