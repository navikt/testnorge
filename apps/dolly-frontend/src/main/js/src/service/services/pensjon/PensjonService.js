import Request from '~/service/services/Request'
import { v4 as _uuid } from 'uuid'

const getPensjonUrl = () => `/testnav-pensjon-testdata-proxy/api/v1` //TODO: reverte denne
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
}
