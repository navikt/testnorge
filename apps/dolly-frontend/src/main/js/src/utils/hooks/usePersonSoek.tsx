import useSWR from 'swr'
import { ResponseIdenter, SoekRequest } from '@/pages/dollySoek/DollySoekTypes'
import Request from '@/service/services/Request'

const elasticUrl = '/dolly-backend/api/v1/elastic'

export const useSoekIdenter = (request: SoekRequest) => {
	console.log('request: ', request) //TODO - SLETT MEG
	const { data, isLoading, error, mutate } = useSWR<ResponseIdenter, Error>(
		request ? [`${elasticUrl}/identer`, request] : null,
		([url, headers]) => Request.post(url, headers),
	)

	return {
		result: data?.data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useBestillingerPaaIdent = (ident: string) => {
	const { data, isLoading, error } = useSWR(
		ident ? `${elasticUrl}/bestilling/ident/${ident}` : null,
		(url) => Request.get(url),
	)

	return {
		bestillinger: data,
		loading: isLoading,
		error: error,
	}
}
