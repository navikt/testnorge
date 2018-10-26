import Request from '../Request'
import ConfigService from '~/service/Config'

export default class TpsfService {
	static getTpsfUrl() {
		return ConfigService.getDatesourceUrl('tpsf') + '/api/v1'
	}

	static getBaseUrl() {
		return ConfigService.getDatesourceUrl('tpsf')
	}

	static getTestbrukere(userArray) {
		if (!userArray) return
		const userString = userArray.join(',')
		const endpoint = this.getTpsfUrl() + '/dolly/testdata/personerdata'

		return Request.get(`${endpoint}?identer=${userString}`)
	}

	static updateTestbruker(userData) {
		if (!userData) return
		const endpoint = this.getTpsfUrl() + '/testdata/updatepersoner'
		return Request.post(endpoint, [userData])
	}

	static createFoedselsmelding(userData) {
		const endpoint = this.getTpsfUrl() + '/tpsmelding/foedselsmelding'
		return Request.post(endpoint, userData)
	}

	static getKontaktInformasjon(fnr, env) {
		const endpoint =
			this.getBaseUrl() + '/api/tps/kontaktinformasjon?fnr=' + fnr + '&environment=' + env
		return Request.get(endpoint)
	}

	static createDoedsmelding(userData) {
		const endpoint = this.getTpsfUrl() + '/tpsmelding/doedsmelding'
		return Request.post(endpoint, userData)
	}

	static getMiljoerByFnr(fnr) {
		const endpoint = this.getTpsfUrl() + '/testdata/tpsStatus?identer=' + fnr
		return Request.get(endpoint)
	}

	static generateAddress(query) {
		const endpoint = `${this.getTpsfUrl()}/gyldigadresse/tilfeldig?maxAntall=1${query}`
		return Request.get(endpoint)
	}
}
