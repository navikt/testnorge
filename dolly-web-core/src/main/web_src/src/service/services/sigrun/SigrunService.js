import config from '~/config'
import Request from '~/service/services/Request'

const getSigrunBaseUrl = () => `${config.services.dollyBackend}/sigrun`

export default {
	getPerson(ident) {
		const endpoint = getSigrunBaseUrl() + '/lignetinntekt'
		return Request.getWithoutCredentials(endpoint, {
			headers: { personidentifikator: ident }
		})
	},

	getSekvensnummer(ident) {
		const endpoint = getSigrunBaseUrl() + '/sekvensnummer/' + ident
		return Request.getWithoutCredentials(endpoint, {
			headers: { personidentifikator: ident }
		})
	}
}
