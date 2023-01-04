import Request from '@/service/services/Request'

const orgForvalterUrl = '/testnav-organisasjon-forvalter/api/v2/organisasjoner'

export default {
	getOrganisasjonerMiljoeInfo(orgnummer: string) {
		const endpoint = orgForvalterUrl + '/framiljoe'
		return Request.get(endpoint + '?orgnummer=' + orgnummer).then((response) => {
			if (response != null) {
				return response
			}
		})
	},
}
