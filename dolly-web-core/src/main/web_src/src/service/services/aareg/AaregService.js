import config from '~/config'
import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getAaregBaseUrl = () =>
	ConfigService.getDatesourceUrl('aaregdataUrl') || config.services.aaregUrl
const getAaregUrl = () => `${getAaregBaseUrl()}/api/v1/arbeidsforhold`

const options = {
	headers: {
		'Nav-Call-Id': 'dolly-frontend',
		'Nav-Consumer-Id': 'dolly'
	}
}

export default {
	getArbeidsforhold(ident, miljoe) {
		const endpoint = `${getAaregUrl()}?ident=${ident}&miljoe=${miljoe}`
		return Request.get(endpoint, options)
	}
}
