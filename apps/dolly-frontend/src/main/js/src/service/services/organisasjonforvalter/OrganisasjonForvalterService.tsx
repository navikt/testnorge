import Request from '~/service/services/Request'
import Api from '~/api'
import { Organisasjon } from '~/service/services/organisasjonforvalter/types'

const orgForvalterUrl = '/testnav-organisasjon-forvalter/api/v2/organisasjoner'

export default {
	getOrganisasjonerInfo(orgnummer: string[]) {
		return Request.get(orgForvalterUrl + '?orgnumre=' + orgnummer).then((response) => {
			if (response != null) return response
		})
	},
	getOrganisasjonerOrdrestatus(orgnummer: string[]) {
		const endpoint = orgForvalterUrl + '/ordrestatus'
		return Request.get(endpoint + '?orgnumre=' + orgnummer).then((response) => {
			if (response != null) return response
		})
	},
	getOrganisasjonerMiljoeInfo(orgnummer: string) {
		const endpoint = orgForvalterUrl + '/framiljoe'
		return Request.get(endpoint + '?orgnummer=' + orgnummer).then((response) => {
			if (response != null) return response
		})
	},
	getVirksomheterPaaBruker(brukerid: string) {
		const endpoint = orgForvalterUrl + '/virksomheter'
		return Request.get(endpoint + '?brukerid=' + brukerid).then((response) => {
			if (response != null) return response
		})
	},

	getAlleOrganisasjonerPaaBruker(): Promise<Organisasjon[]> {
		const endpoint = orgForvalterUrl + '/alle'
		return Api.fetchJson(endpoint, {
			method: 'GET',
		})
	},
}
