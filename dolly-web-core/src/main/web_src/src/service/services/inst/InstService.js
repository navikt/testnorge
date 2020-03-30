import config from '~/config'
import Request from '~/service/services/Request'

const getInstUrl = () => `${config.services.proxyBackend}/inst`

export default {
	getPerson(ident, env) {
		const endpoint = `${getInstUrl()}/ident?identer=${ident}&miljoe=${env}`
		const options = {
			headers: {
				'Nav-Call-Id': 'dolly',
				'Nav-Consumer-Id': 'dolly-frontend'
			}
		}
		return Request.getWithoutCredentials(endpoint, options)
	},

	getTilgjengeligeMiljoer() {
		const endpoint = `${getInstUrl()}/miljoer`
		return Request.getWithoutCredentials(endpoint)
	}
}
