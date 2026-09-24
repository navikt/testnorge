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

export const tenorSlettPersonMal = async (id: number) => {
	try {
		const response = await Request.delete(`${tenorSearchUrl}/maler/personer/${id}`)
		if (!response.ok) {
			throw new Error(response.statusText)
		}
		return response
	} catch (error) {
		console.error(error)
		throw error
	}
}

export const tenorEndrePersonMal = (id: number, malNavn: string) => {
	return Request.patch(`${tenorSearchUrl}/maler/personer/${id}`, { malNavn: malNavn })
}
