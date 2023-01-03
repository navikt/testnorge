import Request from '@/service/services/Request'

const TPS_MESSAGING_URL = `/testnav-tps-messaging-service/api/v1/personer`

const getTpsMessagingUrl = (ident, miljoe) => `${TPS_MESSAGING_URL}/${ident}?miljoer=${miljoe}`
const getTpsMessagingUrlAllEnvs = (ident) => `${TPS_MESSAGING_URL}/${ident}`

export default {
	getTpsPersonInfo(ident, miljoe) {
		return Request.get(getTpsMessagingUrl(ident, miljoe))
	},
	getTpsPersonInfoAllEnvs(ident) {
		return Request.get(getTpsMessagingUrlAllEnvs(ident))
	},
	deleteBankkontoNorsk(ident) {
		return Request.delete(`${TPS_MESSAGING_URL}/${ident}/bankkonto-norsk`)
	},
	deleteBankkontoUtenlandsk(ident) {
		return Request.delete(`${TPS_MESSAGING_URL}/${ident}/bankkonto-utenlandsk`)
	},
	deleteSkjerming(ident) {
		return Request.delete(`${TPS_MESSAGING_URL}/${ident}/egenansatt`)
	},
}
