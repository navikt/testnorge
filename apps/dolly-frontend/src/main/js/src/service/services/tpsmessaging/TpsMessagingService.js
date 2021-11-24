import Request from '~/service/services/Request'

const getTpsMessagingUrl = (ident, miljoe) =>
	`/testnav-tps-messaging-service/api/v1/personer/${ident}?miljoer=${miljoe}`

export default {
	getTpsPersonInfo(ident, miljoe) {
		return Request.get(getTpsMessagingUrl(ident, miljoe))
	},
}
