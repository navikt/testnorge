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

	static createFoedselsmelding(userData) {
		this.getTpsfUrl()
		const endpoint = url + '/tpsmelding/foedselsmelding'
		return Request.post(endpoint, userData)
	}

	static getKontaktInformasjon(fnr, env) {
		const endpoint =
			'https://tps-forvalteren-u2.nais.preprod.local:443/api/tps/kontaktinformasjon?fnr=' +
			fnr +
			'&environment=' +
			env
		return Request.get(endpoint)
	}

	static createDoedsmelding(userData) {
		this.getTpsfUrl()
		const endpoint = url + '/tpsmelding/doedsmelding'
		return Request.post(endpoint, userData)
	}
}
