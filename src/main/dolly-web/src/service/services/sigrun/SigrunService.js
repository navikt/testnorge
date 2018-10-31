import Request from '../Request'
import axios from 'axios'

export default class SigrunService {
	static getSigrunUrl() {
		// TODO: Gjør dette dynamisk når backend har en dynamisk api request
		return 'https://sigrun-skd-stub.nais.preprod.local'
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
				console.log(res)
				data = data.concat(res.data)
			})
			return data
		})
	}
}
