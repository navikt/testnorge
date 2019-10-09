import Request from '../Request'
import ConfigService from '~/service/Config'

const defaultUrl = 'https://arena-forvalteren.nais.preprod.local'

const getArenaBaseUrl = () => ConfigService.getDatesourceUrl('arena') || defaultUrl
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
