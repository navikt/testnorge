import Request from '../Request'
import config from '~/config'

// TODO: erstartt med state.config
const uri = config.services.tpsf

export default class TpsfService {
	static getTestbrukere(userArray) {
		if (!userArray) return
		const userString = userArray.join(',')
		const endpoint = uri + '/dolly/testdata/personerdata'

		return Request.get(`${endpoint}?identer=${userString}`)
	}

	static updateTestbruker(userData) {
		if (!userData) return

		const endpoint = uri + '/testdata/updatepersoner'

		return Request.post(endpoint, [userData])
	}
}
