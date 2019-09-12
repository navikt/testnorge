import Request from '../Request'
import axios from 'axios'
import ConfigService from '~/service/Config'

export default class KrrService {
	static getKrrUrl() {
		const url = ConfigService.getDatesourceUrl('krr') || 'https://krr-stub.nais.preprod.local'
		return url + '/api/v1'
	}

	static getTestbruker(ident) {
		return Request.getWithoutCredentials(`${this.getKrrUrl()}/person/kontaktinformasjon`, {
			headers: {
				'Nav-Personident': ident,
				'Nav-Call-Id': 'dolly',
				'Nav-Consumer-Id': 'dolly-frontend'
			}
		})
	}

	static updateTestbruker(krrstubId, data) {
		return Request.putWithoutCredentials(
			`${this.getKrrUrl()}/kontaktinformasjon/${krrstubId}`,
			data,
			{
				headers: {
					'Nav-Call-Id': 'dolly',
					'Nav-Consumer-Id': 'dolly-frontend'
				}
			}
		)
	}
}
