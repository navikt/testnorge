import Api from '~/api'

export default {
	addToSession(orgnummer: string) {
		return Api.fetch(`/session/user?organisasjonsnummer=${orgnummer}`, { method: 'PUT' })
	},
	clear() {
		return Api.fetch('/session/user', { method: 'DELETE' })
	},
}
