import Request from '@/service/services/Request'

const organisasjonTilgangUrl = `/testnav-organisasjon-tilgang-service/api/v1/organisasjoner`

export default {
	postOrganisasjon(request: any) {
		return Request.post(`${organisasjonTilgangUrl}`, request)
	},
	deleteOrganisasjoner(orgnummer: string) {
		return Request.delete(`${organisasjonTilgangUrl}/${orgnummer}`)
	},
}
