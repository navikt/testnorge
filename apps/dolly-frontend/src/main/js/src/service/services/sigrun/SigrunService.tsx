import Request from '@/service/services/Request'

const getSigrunBaseUrl = () => `/testnav-sigrunstub-proxy/api/v1`

export default {
	getPerson(ident) {
		const endpoint = getSigrunBaseUrl() + '/lignetinntekt'
		return Request.get(endpoint, { personidentifikator: ident })
	},
	getPensjonsgivendeInntekt(ident) {
		const endpoint = getSigrunBaseUrl() + '/pensjonsgivendeinntektforfolketrygden'
		return Request.get(endpoint, { norskident: ident })
	},
	getSekvensnummer(ident) {
		const endpoint = getSigrunBaseUrl() + '/sekvensnummer/' + ident
		return Request.get(endpoint)
	},
}
