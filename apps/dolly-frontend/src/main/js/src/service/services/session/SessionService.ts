import Api from '~/api'

export default {
	addToSession(orgnummer: string) {
		Api.fetch(`/session/user?organisasjonsnummer=${orgnummer}`, { method: 'PUT' })
	},
	clear() {
		Api.fetch('/session/user', { method: 'DELETE' })
	},
}
