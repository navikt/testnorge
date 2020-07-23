import config from '~/config'
import Request from '~/service/services/Request'

const getArenaUrl = () => `${config.services.proxyBackend}/arena`

export default {
	getPerson(ident) {
		const endpoint = `${getArenaUrl()}/bruker?filter-personident=${ident}&page=0`
		return Request.get(endpoint)
	},

	getTilgjengeligeMiljoer() {
		const endpoint = `${getArenaUrl()}/miljoe`
		return Request.get(endpoint)
	}
}
