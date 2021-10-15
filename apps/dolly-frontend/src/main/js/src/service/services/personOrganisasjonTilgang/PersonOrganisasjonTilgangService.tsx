import Request from '~/service/services/Request'

const personOrgTilgangServiceUrl =
	'/testnav-person-organisasjon-tilgang-service/api/v1/person/organisasjoner'
const orgServiceUrl = '/testnav-person-organisasjon-service/api/v1/person/organisasjoner'

export default {
	getOrganisasjonInfo(orgnummer: string, miljoe: string) {
		return Request.post(`${orgServiceUrl}`, { miljo: miljoe ? miljoe : 'q1' }).then((response) => {
			if (response != null) return response
		})
	},

	getOrganisasjoner() {
		return Request.get(`${personOrgTilgangServiceUrl}`).then((response) => {
			return response
		})
	},

	getOrganisasjon(orgnr: string) {
		return Request.get(`${personOrgTilgangServiceUrl}/${orgnr}`).then((response) => {
			return response
		})
	},
}
