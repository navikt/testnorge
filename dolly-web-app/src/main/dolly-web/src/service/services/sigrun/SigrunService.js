import config from '~/config'
import Request from '~/service/services/Request'
import ConfigService from '~/service/Config'

const getSigrunBaseUrl = () =>
	ConfigService.getDatesourceUrl('sigrun') || config.services.sigrunStubUrl

export default {
	getTestbruker(ident) {
		const endpoint = getSigrunBaseUrl() + '/testdata/les'
		return Request.getWithoutCredentials(endpoint, {
			headers: { personidentifikator: ident }
		})
	},

	getSekvensnummer(ident) {
		const endpoint = getSigrunBaseUrl() + '/api/v0/sekvensnummer/' + ident
		return Request.getWithoutCredentials(endpoint, {
			headers: { personidentifikator: ident }
		})
	},

	updateTestbruker(data) {
		const endpoint = `${getSigrunBaseUrl()}/testdata/oppdater`
		return Request.postWithoutCredentials(endpoint, null, {
			headers: data
		})
	}
}
