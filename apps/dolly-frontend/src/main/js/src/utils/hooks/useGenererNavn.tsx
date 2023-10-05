import useSWR from 'swr'
import { fetcher } from '@/api'

type Navn = {
	adjektiv: string
	adverb: string
	substantiv: string
}

const GenererNavnBaseUrl = '/generer-navn-service/api/v1/navn'

export const useGenererNavn = () => {
	const { data, isLoading, error, mutate } = useSWR<string[], Error>(
		[GenererNavnBaseUrl],
		([url, headers]) => fetcher(url, headers),
	)

	return {
		data: data,
		navnInfo: {
			value: {
				data: data?.map((element: any) => ({
					fornavn: element.adjektiv,
					mellomnavn: element.adverb,
					etternavn: element.substantiv,
				})),
				loading: isLoading,
			},
		},
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}
