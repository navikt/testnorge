import Request from '~/service/services/Request'
import { v4 as _uuid } from 'uuid'

const getInstUrl = () => `/testnav-inst-service/api/v1`

export default {
	getPerson(ident, env) {
		const endpoint = `${getInstUrl()}/ident?identer=${ident}&miljoe=${env}`
		return Request.get(endpoint, { 'Nav-Call-Id': _uuid(), 'Nav-Consumer-Id': 'dolly' })
	},
}
