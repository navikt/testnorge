import Request from '~/service/services/Request'

const udiUrl = '/api/udi-stub'

export default {
	getPerson(ident) {
		const endpoint = `${udiUrl}/person/${ident}`
		return Request.get(endpoint)
	}
}
