import Request from '~/service/services/Request'

const orgServiceUrl = '/api/organisasjon-service/organisasjoner'

export default {
	getOrganisasjonInfo(orgnummer: string) {
		return Request.get(`${orgServiceUrl}/${orgnummer}`, { miljo: 'q1' }).then(response => {
			if (response != null) return response
		})
	}
}
