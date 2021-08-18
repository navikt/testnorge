import Request from '~/service/services/Request'
import { v4 as _uuid } from 'uuid'

const getAaregUrl = () => '/testnav-testnorge-aareg-proxy/api/v1'

export default {
	getArbeidsforhold(ident, miljoe) {
		const endpoint = `${getAaregUrl()}/arbeidsforhold?ident=${ident}&miljoe=${miljoe}`
		return Request.get(endpoint, { 'Nav-Call-Id': _uuid(), 'Nav-Consumer-Id': 'dolly' })
	}
}
