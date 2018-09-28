import Request from '../Request'
import store from '~/Store'
var url = ''

export default class TpsfService {
	static getTpsfUrl() {
		url = store.getState().config.dollyApi.url + '/api/v1'
	}

	static getTestbrukere(userArray) {
		this.getTpsfUrl()
		if (!userArray) return
		const userString = userArray.join(',')
		const endpoint = url + '/dolly/testdata/personerdata'

		return Request.get(`${endpoint}?identer=${userString}`)
	}

	static updateTestbruker(userData) {
		if (!userData) return
		const endpoint = url + '/testdata/updatepersoner'
		return Request.post(endpoint, [userData])
	}

	static createFoedselmelding(userData) {
		console.log(userData)
		// TODO: get real res from TPSF
		return Promise.resolve('status OK')
	}
}
