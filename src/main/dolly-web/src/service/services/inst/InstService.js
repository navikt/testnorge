import config from '~/config'
import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getInstBaseUrl = () => ConfigService.getDatesourceUrl('inst') || config.services.instdataUrl
const getInstUrl = () => `${getInstBaseUrl()}/api/v1`

export default {
	getTestbruker(ident, env) {
		const endpoint = `${getInstUrl()}/ident?identer=${ident}&miljoe=${env}`
		const options = {
			headers: {
				NavCallId: 'dolly',
				NavConsumerId: 'dolly-frontend'
			}
		}
		return Request.getWithoutCredentials(endpoint, options)
	},

	getTilgjengeligeMiljoer() {
		const endpoint = `${getInstUrl()}/miljoer`
		return Request.getWithoutCredentials(endpoint)
	}
}
