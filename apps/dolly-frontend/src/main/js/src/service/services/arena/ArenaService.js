import { v4 as _uuid } from 'uuid'
import Request from '~/service/services/Request'

const getArenaUrl = () => '/testnav-arena-forvalteren-proxy/api/v1'

export default {
	getPerson(ident) {
		const endpoint = `${getArenaUrl()}/bruker?filter-personident=${ident}&page=0`
		return Request.get(endpoint, { 'Nav-Call-Id': _uuid(), 'Nav-Consumer-Id': 'dolly' })
	},

	getTilgjengeligeMiljoer() {
		const endpoint = `${getArenaUrl()}/miljoe`
		return Request.get(endpoint, { 'Nav-Call-Id': _uuid(), 'Nav-Consumer-Id': 'dolly' })
	}
}
