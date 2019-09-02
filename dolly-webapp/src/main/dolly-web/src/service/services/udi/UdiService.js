import Request from '../Request'
// import axios from 'axios'
import ConfigService from '~/service/Config'

export default class UdiService {
	static getUdiUrl() {
		const url = ConfigService.getDatesourceUrl('udi') || 'https://udi-stub.nais.preprod.local'
		return url + '/api/v1'
	}

	static getTestbruker(ident) {
		return Request.getWithoutCredentials(`${this.getUdiUrl()}/person`, {
			headers: {
				'Nav-Personident': ident
			}
		})
	}
}
