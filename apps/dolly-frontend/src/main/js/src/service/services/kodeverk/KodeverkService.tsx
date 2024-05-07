import Request from '@/service/services/Request'

const kodeverkUrl = '/testnav-kodeverk-service/api/v1/kodeverk'

export default {
	getKodeverkByNavn(kodeverk: string) {
		return Request.get(`${kodeverkUrl}/${kodeverk}`)
	},
}
