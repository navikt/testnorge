import Request from '@/service/services/Request'
import { arrayToString } from '@/utils/DataFormatter'

const altinn3TilgangUrl = `/testnav-altinn3-tilgang-service/api/v1/organisasjoner`
const organisasjonMiljoerUrl = `/testnav-altinn3-tilgang-service/api/v1/miljoer/organisasjon`

export default {
	postOrganisasjon(request: any) {
		return Request.post(`${altinn3TilgangUrl}/${request.organisasjonsnummer}?miljoe=${request.miljoe}`, {})
	},
	deleteOrganisasjoner(orgnummer: string) {
		return Request.delete(`${altinn3TilgangUrl}/${orgnummer}`)
	},
	updateOrganisasjon(values: any) {
		const { organisasjonsnummer, miljoe } = values
		return Request.put(`${organisasjonMiljoerUrl}/${organisasjonsnummer}?miljoe=${miljoe}`, {})
	},
}
