import Request from '@/service/services/Request'
import logoutBruker from '@/components/utlogging/logoutBruker'

const personOrgTilgangServiceUrl =
	'/testnav-altinn3-tilgang-service/api/v1/brukertilgang'

export default {
	getOrganisasjoner() {
		return Request.get(`${personOrgTilgangServiceUrl}`)
			.catch((error) => {
				console.warn('Noe gikk galt ved henting av tilganger for bruker')
				console.error(error)
				logoutBruker('person_org_error')
			})
			.then((response) => {
				return response
			})
	},
}
