import Request from '../Request'
import axios from 'axios'
import ConfigService from '~/service/Config'

export default class SigrunService {
	static getSigrunUrl() {
		return ConfigService.getDatesourceUrl('sigrun') || 'https://sigrun-skd-stub.nais.preprod.local'
	}

	static getTestbruker(ident) {
		const endpoint = this.getSigrunUrl() + '/testdata/les'

		return Request.getWithoutCredentials(endpoint, {
			headers: { personidentifikator: ident }
		})
	}

	static getTestbrukere(identArray) {
		const promiseArr = identArray.map(ident => {
			return this.getTestbruker(ident)
		})

		return Promise.all(promiseArr).then(resArr => {
			let data = []
			resArr.filter(res => res.data.length > 0).forEach(res => {
				data = data.concat(res.data)
			})
			return data
		})
	}

	static getSekvensnummer(ident) {
		const endpoint = this.getSigrunUrl() + '/api/v0/sekvensnummer/' + ident
		return Request.getWithoutCredentials(endpoint, {
			headers: { personidentifikator: ident }
		})
	}

	static updateTestbruker(data) {
		const endpoint = this.getSigrunUrl() + '/testdata/oppdater'

		return Request.postWithoutCredentials(endpoint, null, {
			headers: data
		})
	}
}
