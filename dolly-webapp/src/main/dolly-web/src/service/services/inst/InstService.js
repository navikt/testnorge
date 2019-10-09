import Request from '../Request'
import ConfigService from '~/service/Config'

const defaultUrl = 'https://testnorge-inst.nais.preprod.local'

const getInstBaseUrl = () => ConfigService.getDatesourceUrl('inst') || defaultUrl
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
