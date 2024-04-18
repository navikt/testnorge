import Request from '@/service/services/Request'

const TPS_MESSAGING_URL_V1 = `/testnav-tps-messaging-service/api/v1/personer`
const TPS_MESSAGING_URL_V2 = `/testnav-tps-messaging-service/api/v2/personer`

const getTpsMessagingUrl_V2 = (miljoe) => `${TPS_MESSAGING_URL_V2}/ident?miljoer=${miljoe}`
const getTpsMessagingUrlAllEnvs_V2 = () => `${TPS_MESSAGING_URL_V2}/ident`

export default {
	getTpsPersonInfo(ident, miljoe) {
		return Request.post(getTpsMessagingUrl_V2(miljoe), ident)
	},
	getTpsPersonInfoAllEnvs(ident) {
		return Request.post(getTpsMessagingUrlAllEnvs_V2(), ident)
	},
	deleteBankkontoNorsk(ident) {
		return Request.delete(`${TPS_MESSAGING_URL_V1}/${ident}/bankkonto-norsk`)
	},
	deleteBankkontoUtenlandsk(ident) {
		return Request.delete(`${TPS_MESSAGING_URL_V1}/${ident}/bankkonto-utenlandsk`)
	},
}
