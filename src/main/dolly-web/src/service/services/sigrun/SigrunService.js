import Request from '../Request'
import axios from 'axios'
import ConfigService from '~/service/Config'

export default class SigrunService {
	static getSigrunUrl() {
		const url =
			ConfigService.getDatesourceUrl('sigrun') || 'https://sigrun-skd-stub.nais.preprod.local'
		return url
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

	static updateTestbruker(data) {
		const endpoint = this.getSigrunUrl() + '/testdata/oppdater'

		return Request.postWithoutCredentials(endpoint, null, {
			headers: data
		})
	}
}
