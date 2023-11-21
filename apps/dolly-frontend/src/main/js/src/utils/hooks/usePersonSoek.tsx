import useSWR from 'swr'
import { ResponseIdenter, SoekRequest } from '@/pages/dollySoek/DollySoekTypes'
import Request from '@/service/services/Request'

const elasticUrl = '/dolly-backend/api/v1/elastic'

export const useSoekIdenter = (request: SoekRequest) => {
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
