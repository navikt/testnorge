import Request from '@/service/services/Request'

const getKrrUrl = () => '/testnav-dolly-proxy/krrstub/api/v2'

export default {
	getPerson(ident) {
		const endpoint = `${getKrrUrl()}/person/kontaktinformasjon/soek`
		return Request.post(endpoint, {
			personidentifikator: ident,
		})
	},
	getSdpLeverandoerListe() {
		const endpoint = `${getKrrUrl()}/sdp/leverandoerer`
		return Request.get(endpoint, { 'Nav-Consumer-Id': 'dolly' })
	},
	getSdpLeverandoer(id) {
		const endpoint = `${getKrrUrl()}/sdp/leverandoer/${id}`
		return Request.get(endpoint, { 'Nav-Consumer-Id': 'dolly' })
	},
	slettKontaktinformasjon(id) {
		const endpoint = `${getKrrUrl()}/kontaktinformasjon/${id}`
		return Request.delete(endpoint)
	},
}
