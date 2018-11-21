import Request from '../Request'
import axios from 'axios'

export default class KrrService {
	static getKrrUrl() {
		// TODO: Gjør dette dynamisk når backend har en dynamisk api request
		return 'https://krr-stub.nais.preprod.local/api/v1'
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
