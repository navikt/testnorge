import config from '~/config'
import Request from '~/service/services/Request'

const getArenaUrl = () => `${config.services.proxyBackend}/arena`

const options = {
	headers: {
		'Nav-Call-Id': 'dolly-frontend',
		'Nav-Consumer-Id': 'dolly'
	}
}

export default {
	getPerson(ident) {
		const endpoint = `${getArenaUrl()}/bruker?filter-personident=${ident}&page=0`
		return Request.getWithoutCredentials(endpoint, options)
	},

	getTilgjengeligeMiljoer() {
		const endpoint = `${getArenaUrl()}/miljoe`
		return Request.getWithoutCredentials(endpoint, options)
	}
}
