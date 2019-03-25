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
		const endpoint = this.getTpsfUrl() + '/dolly/testdata/hentpersoner'

		// Må bruke post-request pga maxString-limit på en GET-request
		return Request.post(endpoint, userArray)
	}

	static updateTestbruker(userData) {
		if (!userData) return
		const endpoint = this.getTpsfUrl() + '/testdata/updatepersoner'
		return Request.post(endpoint, [userData])
	}

	static checkpersoner(userArray) {
		if (!userArray) return
		const endpoint = this.getTpsfUrl() + '/dolly/testdata/checkpersoner'
		return Request.post(endpoint, userArray)
	}

	static sendToTps(data) {
		if (!data) return
		const endpoint = this.getTpsfUrl() + '/dolly/testdata/tilTpsFlere'
		return Request.post(endpoint, data)
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

	static getTilgjengligeMiljoer() {
		const endpoint = `${this.getTpsfUrl()}/environments`
		return Request.get(endpoint)
	}
}
