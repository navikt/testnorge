import config from '~/config'
import Request from '~/service/services/Request'

const getAaregUrl = () => `${config.services.proxyBackend}/aareg`

export default {
	getArbeidsforhold(ident, miljoe) {
		const endpoint = `${getAaregUrl()}/arbeidsforhold?ident=${ident}&miljoe=${miljoe}`
		return Request.get(endpoint)
	}
}
