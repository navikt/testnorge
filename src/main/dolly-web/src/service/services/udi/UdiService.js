import Request from '../Request'
import ConfigService from '~/service/Config'

const defaultUrl = 'https://udi-stub.nais.preprod.local'
const getUdiBaseUrl = () => ConfigService.getDatesourceUrl('udi') || defaultUrl
const getUdiUrl = () => `${getUdiBaseUrl()}/api/v1`

export default {
	getTestbruker(ident) {
		const endpoint = `${getUdiUrl()}/person/${ident}`
		return Request.getWithoutCredentials(endpoint)
	}
}
