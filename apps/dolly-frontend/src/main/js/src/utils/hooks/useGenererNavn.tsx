import useSWR from 'swr'
import { fetcher } from '@/api'

const GenererNavnBaseUrl = '/generer-navn-service/api/v1/navn'

export const useGenererNavn = (manuelleValg?: any | undefined) => {
	const { data, isLoading, error, mutate } = useSWR<string[], Error>(
		[GenererNavnBaseUrl],
		([url, headers]) => fetcher(url, headers),
	)

	if (manuelleValg && data) {
		manuelleValg.map((navn: any) => {
			if (!data.includes(navn)) {
				data.push(navn)
			}
		})
	}

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
