import Request from '../Request'
import ConfigService from '~/service/Config'

const defaultUrl = 'https://sigrun-skd-stub.nais.preprod.local'
const getSigrunBaseUrl = () => ConfigService.getDatesourceUrl('sigrun') || defaultUrl

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
