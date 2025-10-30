import Request from '@/service/services/Request'

export default {
	getInntektsinformasjon(ident) {
		const endpoint = `/testnav-dolly-proxy/inntektstub/api/v2/inntektsinformasjon?historikk=true&norske-identer=${ident}`
		return Request.get(endpoint)
	},
	validate(values) {
		return Request.post('/testnav-dolly-proxy/inntektstub/api/v2/valider', values).then(
			(value) => value.data
		)
	},
}
