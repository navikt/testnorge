import Request from '../Request'
import axios from 'axios'

export default class KrrService {
	static getKrrUrl() {
		// TODO: Gjør dette dynamisk når backend har en dynamisk api request
		return 'https://krr-stub.nais.preprod.local/api/v1/kontaktinformasjon'
	}

	static getTestbruker(bruker, ident) {
		return Request.getWithoutCredentials(this.getKrrUrl(), {
			headers: { 'Nav-Personident': ident, 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': bruker }
		})
	}
}
