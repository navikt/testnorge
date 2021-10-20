import Api from '~/api'

export default {
	addToSession(orgnummer: string) {
		Api.fetch(`/api/v1/session/user?organisasjonsnummer=${orgnummer}`, { method: 'PUT' })
	},
	clear() {
		Api.fetch('/api/v1/session/user', { method: 'DELETE' })
	},
}
