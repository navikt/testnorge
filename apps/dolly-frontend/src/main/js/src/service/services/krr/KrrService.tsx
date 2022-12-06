import Request from '@/service/services/Request'
import { v4 as _uuid } from 'uuid'

const getKrrUrl = () => '/testnav-krrstub-proxy/api/v2'

export default {
	getPerson(ident) {
		const endpoint = `${getKrrUrl()}/person/kontaktinformasjon`
		return Request.get(endpoint, {
			'Nav-Personident': ident,
			'Nav-Call-Id': _uuid(),
			'Nav-Consumer-Id': 'dolly',
		})
	},
	getSdpLeverandoerListe() {
		const endpoint = `${getKrrUrl()}/sdp/leverandoerer`
		return Request.get(endpoint, { 'Nav-Call-Id': _uuid(), 'Nav-Consumer-Id': 'dolly' })
	},
	getSdpLeverandoer(id) {
		const endpoint = `${getKrrUrl()}/sdp/leverandoer/${id}`
		return Request.get(endpoint, { 'Nav-Call-Id': _uuid(), 'Nav-Consumer-Id': 'dolly' })
	},
	slettKontaktinformasjon(id) {
		const endpoint = `${getKrrUrl()}/kontaktinformasjon/${id}`
		return Request.delete(endpoint, {
			'Nav-Call-Id': _uuid(),
		})
	},
}
