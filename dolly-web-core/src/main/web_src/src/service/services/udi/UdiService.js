import config from '~/config'
import Request from '~/service/services/Request'

const getUdiUrl = () => `${config.services.proxyBackend}/udi`

export default {
	getPerson(ident) {
		const endpoint = `${getUdiUrl()}/person/${ident}`
		return Request.get(endpoint)
	}
}
