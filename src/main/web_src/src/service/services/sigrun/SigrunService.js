import config from '~/config'
import Request from '~/service/services/Request'

const getSigrunBaseUrl = () => `${config.services.proxyBackend}/sigrun`

export default {
	getPerson(ident) {
		const endpoint = getSigrunBaseUrl() + '/lignetinntekt'
		return Request.get(endpoint, { personidentifikator: ident })
	},
	getSekvensnummer(ident) {
		const endpoint = getSigrunBaseUrl() + '/sekvensnummer/' + ident
		return Request.get(endpoint)
	}
}
