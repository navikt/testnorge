import Request from '~/service/services/Request'

const orgServiceUrl = '/testnav-organisasjon-service/api/v1/organisasjoner'

export default {
	getOrganisasjonInfo(orgnummer: string, miljoe: string) {
		return Request.get(`${orgServiceUrl}/${orgnummer}`, { miljo: miljoe ? miljoe : 'q1' }).then(
			response => {
				if (response != null) return response
			}
		)
	}
}
