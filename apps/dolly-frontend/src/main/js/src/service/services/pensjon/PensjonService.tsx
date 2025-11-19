import Request from '@/service/services/Request'
import { v4 as _uuid } from 'uuid'

const getPensjonUrl = () => `/testnav-dolly-proxy/pensjon/api/v1`
const headers = { 'Nav-Call-Id': _uuid(), 'Nav-Consumer-Id': 'dolly', Authorization: 'dolly' }

export default {
	getTpOrdninger() {
		const endpoint = `${getPensjonUrl()}/tp/ordning`
		return Request.get(endpoint, headers)
	},
}
