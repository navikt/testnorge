import config from '~/config'
import Request from '~/service/services/Request'

const getKrrUrl = () => `${config.services.proxyBackend}/krr`

export default {
	getPerson(ident) {
		const endpoint = `${getKrrUrl()}/person/kontaktinformasjon`
		return Request.post(endpoint, ident)
	}
}
