import Request from '~/service/services/Request'
import { v4 as _uuid } from 'uuid'

const getPensjonUrl = () => `/testnav-pensjon-testdata-facade-proxy/api/v1`
const headers = { 'Nav-Call-Id': _uuid(), 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly' }

export default {
	getPoppInntekt(ident, miljoe) {
		const endpoint = `${getPensjonUrl()}/inntekt?fnr=${ident}&miljo=${miljoe}`
		return Request.get(endpoint, headers)
	},

	getTilgjengeligeMiljoer() {
		const endpoint = `${getPensjonUrl()}/miljo`
		return Request.get(endpoint, headers)
	},

	getTpOrdninger() {
		const endpoint = `${getPensjonUrl()}/tp/ordning`
		return Request.get(endpoint, headers)
	},
}
