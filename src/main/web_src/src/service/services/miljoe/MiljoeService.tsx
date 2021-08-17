import Request from '~/service/services/Request'

const miljoeUrl = '/testnav-miljoer-service/api/v1'

export default {
	getAktiveMiljoer() {
		const endpoint = miljoeUrl + '/miljoer'
		return Request.get(endpoint)
	}
}
