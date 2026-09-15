import Request from '@/service/services/Request'

const tenorSearchUrl = '/testnav-template-search-service/api/v1/tenor'

export const tenorOpprettPersonMal = (verdier: any, malNavn: string) => {
	if (!malNavn) {
		return null
	}
	return Request.post(`${tenorSearchUrl}/maler/personer`, {
		malNavn: malNavn,
		soekKriterier: verdier,
	})
}
