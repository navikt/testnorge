import Request from '@/service/services/Request'

const orgForvalterUrl = '/testnav-organisasjon-forvalter/api/v2/organisasjoner'

export const normalizeMiljoeResponse = (data: unknown): Record<string, unknown> => {
	if (Array.isArray(data)) {
		return Object.assign({}, ...data)
	}
	if (data && typeof data === 'object') {
		return data as Record<string, unknown>
	}
	return {}
}

export default {
	getOrganisasjonerMiljoeInfo(orgnummer: string) {
		const endpoint = orgForvalterUrl + '/framiljoe'
		return Request.get(endpoint + '?orgnummer=' + orgnummer).then((response) => {
			if (response != null) {
				return { data: normalizeMiljoeResponse(response.data) }
			}
		})
	},
}
