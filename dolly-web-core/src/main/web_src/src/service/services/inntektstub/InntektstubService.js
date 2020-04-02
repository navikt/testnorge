import config from '~/config'
import Request from '~/service/services/Request'

const getInntektstubUrl = () => `${config.services.proxyBackend}/inntektstub`

export default {
	getInntektsinformasjon(ident) {
		const endpoint = `${getInntektstubUrl()}/inntektsinformasjon?norske-identer=${ident}`
		return Request.get(endpoint)
	}
}
