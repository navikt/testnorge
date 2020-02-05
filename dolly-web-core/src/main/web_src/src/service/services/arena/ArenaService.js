import config from '~/config'
import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getArenaBaseUrl = () => ConfigService.getDatesourceUrl('arena')
const getArenaUrl = () => `${getArenaBaseUrl()}/api/v1`

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
