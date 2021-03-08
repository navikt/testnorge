import Request from '~/service/services/Request'

const orgForvalterUrl = '/api/organisasjon-forvalter/organisasjon'

export default {
	getOrganisasjonerInfo(orgnummer: string[]) {
		return Request.get(orgForvalterUrl + '?orgnumre=' + orgnummer).then(response => {
			if (response != null) return response
		})
	},
	getOrganisasjonerMiljoeInfo(orgnummer: string) {
		const endpoint = orgForvalterUrl + '/import'
		return Request.get(endpoint + '?orgnummer=' + orgnummer).then(response => {
			if (response != null) return response
		})
	}
}
