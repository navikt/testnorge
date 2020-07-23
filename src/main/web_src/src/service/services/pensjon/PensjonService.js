import config from '~/config'
import Request from '~/service/services/Request'

const getPensjonUrl = () => `${config.services.proxyBackend}/popp`

export default {
	getPoppInntekt(ident, miljoe) {
		const endpoint = `${getPensjonUrl()}/inntekt?fnr=${ident}&miljo=${miljoe}`
		return Request.get(endpoint)
	},

	getTilgjengeligeMiljoer() {
		const endpoint = `${getPensjonUrl()}/miljo`
		return Request.get(endpoint)
	}
}
