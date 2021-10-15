import Request from '~/service/services/Request'

const orgServiceUrl = '/testnav-person-organisasjon-service/api/v1/person/organisasjoner'

export default {
	getOrganisasjonInfo(orgnummer: string, miljoe: string) {
		return Request.post(`${orgServiceUrl}`, { miljo: miljoe ? miljoe : 'q1' }).then((response) => {
			if (response != null) return response
		})
	},
}
