import Request from '@/service/services/Request'

const personOrgTilgangServiceUrl =
	'/testnav-person-organisasjon-tilgang-service/api/v1/person/organisasjoner'

export default {
	getOrganisasjoner() {
		return Request.get(`${personOrgTilgangServiceUrl}`).then((response) => {
			return response
		})
	},
}
