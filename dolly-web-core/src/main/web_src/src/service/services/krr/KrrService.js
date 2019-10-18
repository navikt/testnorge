import config from '~/config'
import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getKrrBaseUrl = () => ConfigService.getDatesourceUrl('krr') || config.services.krrStubUrl
const getKrrUrl = () => `${getKrrBaseUrl()}/api/v1`

export default {
	getTestbruker(ident) {
		const endpoint = `${getKrrUrl()}/person/kontaktinformasjon`
		return Request.getWithoutCredentials(endpoint, {
			headers: {
				'Nav-Personident': ident,
				'Nav-Call-Id': 'dolly',
				'Nav-Consumer-Id': 'dolly-frontend'
			}
		})
	},

	updateTestbruker(krrstubId, data) {
		const endpoint = `${getKrrUrl()}/kontaktinformasjon/${krrstubId}`
		return Request.putWithoutCredentials(endpoint, data, {
			headers: {
				'Nav-Call-Id': 'dolly',
				'Nav-Consumer-Id': 'dolly-frontend'
			}
		})
	}
}
