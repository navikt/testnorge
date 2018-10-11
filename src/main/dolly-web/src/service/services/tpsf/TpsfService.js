import Request from '../Request'
import store from '~/Store'
var url = ''
var baseUrl = ''

export default class TpsfService {
	static getTpsfUrl() {
		url = store.getState().config.dollyApi.url + '/api/v1'
	}

	static getBaseUrl() {
		baseUrl = store.getState().config.dollyApi.url
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
		this.getBaseUrl()
		const endpoint = baseUrl + '/api/tps/kontaktinformasjon?fnr=' + fnr + '&environment=' + env
		return Request.get(endpoint)
	}

	static createDoedsmelding(userData) {
		this.getTpsfUrl()
		const endpoint = url + '/tpsmelding/doedsmelding'
		return Request.post(endpoint, userData)
	}

	static generateAddress(query) {
		this.getTpsfUrl()
		const endpoint = `${url}/gyldigadresse/tilfeldig?maxAntall=1${query}`
		return Request.get(endpoint)
	}
}
