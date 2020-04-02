import config from '~/config'
import Request from '~/service/services/Request'

const getInstUrl = () => `${config.services.proxyBackend}/inst`

export default {
	getPerson(ident, env) {
		const endpoint = `${getInstUrl()}/ident?identer=${ident}&miljoe=${env}`
		return Request.get(endpoint)
	},

	getTilgjengeligeMiljoer() {
		const endpoint = `${getInstUrl()}/miljoer`
		return Request.get(endpoint)
	}
}
