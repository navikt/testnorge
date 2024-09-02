import Request from '@/service/services/Request'
import { arrayToString } from '@/utils/DataFormatter'

const organisasjonTilgangUrl = `/testnav-organisasjon-tilgang-service/api/v1/organisasjoner`

export default {
	postOrganisasjon(request: any) {
		return Request.post(`${organisasjonTilgangUrl}`, request)
	},
	deleteOrganisasjoner(orgnummer: string) {
		return Request.delete(`${organisasjonTilgangUrl}/${orgnummer}`)
	},
	updateOrganisasjon(values: any) {
		const { organisasjonsnummer, gyldigTil, miljoe } = values
		return Request.put(`${organisasjonTilgangUrl}`, {
			organisasjonsnummer,
			gyldigTil,
			miljoe: arrayToString(miljoe).replaceAll(' ', ''),
		})
	},
}
