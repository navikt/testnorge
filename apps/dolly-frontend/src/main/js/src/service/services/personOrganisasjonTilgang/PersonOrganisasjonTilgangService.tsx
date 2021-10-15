import Request from '~/service/services/Request'

type Bruker = {
	id?: string
}

const personOrgTilgangServiceUrl =
	'/testnav-person-organisasjon-tilgang-service/api/v1/person/organisasjoner'

export default {
	getOrganisasjoner() {
		return Request.get(`${personOrgTilgangServiceUrl}`).then((response) => {
			if (response != null) return response
		})
	},

	getOrganisasjon(orgnr: string) {
		return Request.get(`${personOrgTilgangServiceUrl}/${orgnr}`).then((response) => {
			if (response != null) return response
		})
	},
}
