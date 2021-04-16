import Request from '~/service/services/Request'

const miljoeUrl = '/api/testnav-miljoer-service'

export default {
	getAktiveMiljoer() {
		const endpoint = miljoeUrl + '/miljoer'
		return Request.get(endpoint)
	}
}
