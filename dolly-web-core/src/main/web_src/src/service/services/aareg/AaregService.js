import config from '~/config'
import Request from '~/service/services/Request'

const getAaregUrl = () => `${config.services.proxyBackend}/aareg`

const options = {
	headers: {
		'Nav-Call-Id': 'dolly-frontend',
		'Nav-Consumer-Id': 'dolly'
	}
}

export default {
	getArbeidsforhold(ident, miljoe) {
		const endpoint = `${getAaregUrl()}/arbeidsforhold?ident=${ident}&miljoe=${miljoe}`
		return Request.getWithoutCredentials(endpoint, options)
	}
}
