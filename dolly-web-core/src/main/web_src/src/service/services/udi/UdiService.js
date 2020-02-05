import config from '~/config'
import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getUdiBaseUrl = () => ConfigService.getDatesourceUrl('udi')
const getUdiUrl = () => `${getUdiBaseUrl()}/api/v1`

export default {
	getPerson(ident) {
		const endpoint = `${getUdiUrl()}/person/${ident}`
		return Request.getWithoutCredentials(endpoint)
	}
}
