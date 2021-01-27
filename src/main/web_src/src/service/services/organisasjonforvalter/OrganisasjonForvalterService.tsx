import Request from '~/service/services/Request'

const orgForvalterUrl = '/api/organisasjon-forvalter'

export default {
	getOrganisasjonerInfo(orgnummer: string[]) {
		const endpoint = orgForvalterUrl + '/organisasjon'
		return Request.get(endpoint + '?orgnumre=' + orgnummer).then(response => {
			if (response != null) return response
		})
	}
}
