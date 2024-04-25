import Request from '@/service/services/Request'

const TPS_MESSAGING_URL = `/testnav-tps-messaging-service/api/v1/personer`

export default {
	getTpsPersonInfo(ident, miljoe) {
		return Request.post(`${TPS_MESSAGING_URL}/ident?miljoer=${miljoe}`, { ident: ident })
	},
	getTpsPersonInfoAllEnvs(ident) {
		return Request.post(`${TPS_MESSAGING_URL}/ident`, { ident: ident })
	},
	deleteBankkontoNorsk(ident) {
		return Request.delete(`${TPS_MESSAGING_URL}/${ident}/bankkonto-norsk`)
	},
	deleteBankkontoUtenlandsk(ident) {
		return Request.delete(`${TPS_MESSAGING_URL}/${ident}/bankkonto-utenlandsk`)
	},
}
