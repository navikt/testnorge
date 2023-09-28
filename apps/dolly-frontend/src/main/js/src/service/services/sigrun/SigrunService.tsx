import Request from '@/service/services/Request'

const getSigrunBaseUrl = () => `/testnav-sigrunstub-proxy/api/v1`

export default {
	getPerson(ident) {
		const endpoint = getSigrunBaseUrl() + '/lignetinntekt'
		return Request.get(endpoint, { personidentifikator: ident })
	},
	//TODO: endepunkt boer ha valgfritt inntektsaar, fjern den naar ok
	getPensjonsgivendeInntekt(ident, inntektsaar = '2023') {
		const endpoint = getSigrunBaseUrl() + '/pensjonsgivendeinntektforfolketrygden'
		return Request.get(endpoint, { norskident: ident, inntektsaar: inntektsaar })
	},
	getSekvensnummer(ident) {
		const endpoint = getSigrunBaseUrl() + '/sekvensnummer/' + ident
		return Request.get(endpoint)
	},
}
