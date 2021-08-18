import Request from '~/service/services/Request'

const udiUrl = '/udi-stub/api/v1'

export default {
	getPerson(ident) {
		const endpoint = `${udiUrl}/person/${ident}`
		return Request.get(endpoint)
	}
}
