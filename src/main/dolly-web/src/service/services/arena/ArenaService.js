import config from '~/config'
import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getArenaBaseUrl = () =>
	ConfigService.getDatesourceUrl('arena') || config.services.arenaForvalterUrl
const getArenaUrl = () => `${getArenaBaseUrl()}/api/v1`

const options = {
	headers: {
		'Nav-Call-Id': 'dolly-frontend',
		'Nav-Consumer-Id': 'dolly'
	}
}

export default {
	getTestbruker(ident) {
		const endpoint = `${getArenaUrl()}/bruker?filter-personident=${ident}&page=0`
		return Request.getWithoutCredentials(endpoint, options)
	},

	getTilgjengeligeMiljoe() {
		const endpoint = `${getArenaUrl()}/miljoe`
		return Request.getWithoutCredentials(endpoint, options)
	}
}
