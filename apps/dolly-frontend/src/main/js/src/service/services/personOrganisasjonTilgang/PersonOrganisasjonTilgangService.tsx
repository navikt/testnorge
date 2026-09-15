import Request from '@/service/services/Request'

const personOrgTilgangServiceUrl = '/altinn/organisasjoner'

export default {
	getOrganisasjoner() {
		return Request.get(`${personOrgTilgangServiceUrl}`)
	},
}
