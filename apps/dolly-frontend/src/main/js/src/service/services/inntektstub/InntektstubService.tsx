import Request from '@/service/services/Request'

const baseUrl = '/testnav-dolly-proxy/inntektstub/api/v2'

export default {
	getInntektsinformasjon(ident) {
		const endpoint = `${baseUrl}/inntektsinformasjon?historikk=true&norske-identer=${ident}`
		return Request.get(endpoint)
	},
	validate(values) {
		return Request.post(`${baseUrl}/valider`, values).then((value) => value.data)
	},
	deleteInntektsinformasjon(ident, aarMaaned, virksomhet) {
		return Request.delete(
			`${baseUrl}/inntektsinformasjon`,
			{ 'Content-Type': 'application/json' },
			{
				norskIdent: ident,
				aarMaaned: aarMaaned,
				virksomhet: virksomhet,
			},
		)
	},
}
