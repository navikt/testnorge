import Request from '~/service/services/Request'

const miljoeUrl = '/api/testnorge-miljoer-service'

export default {
	getAktiveMiljoer() {
		const endpoint = miljoeUrl + '/miljoer'
		return Request.get(endpoint)
	}
}
