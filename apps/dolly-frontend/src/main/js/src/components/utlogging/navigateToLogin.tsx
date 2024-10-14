export const navigateToLogin = (feilmelding?: string) => {
	console.error('Ukjent feil i Dolly, feilmelding: ' + feilmelding)
	window.location.href = '/login'
}
