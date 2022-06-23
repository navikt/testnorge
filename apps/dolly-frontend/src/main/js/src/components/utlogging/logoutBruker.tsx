const logoutBruker = (feilmelding?: string) => {
	window.location.href = '/logout'
	console.error(feilmelding)
}
export default logoutBruker
