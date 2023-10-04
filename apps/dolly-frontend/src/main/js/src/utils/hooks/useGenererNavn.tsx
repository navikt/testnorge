import useSWR from 'swr'
import { fetcher } from '@/api'

type Navn = {
	adjektiv: string
	adverb: string
	substantiv : string
}

const GenererNavnBaseUrl = '/generer-navn-service/api/v1/navn'

export const useGenererNavn = () => {
	const { data, isLoading, error , mutate} = useSWR<string[], Error>(
		[GenererNavnBaseUrl],
		([url, headers]) => fetcher(url, headers),
	)
	console.log(data)
	const personnavn = []
	data?.flat().forEach((element : any) => {
		personnavn.push({
			fornavn: element.adjektiv,
			mellomnavn: element.adverb,
			etternavn: element.substantiv
		})
	})

	return {
		navnInfo: personnavn,
		loading: isLoading,
		error: error,
		mutate: mutate
	}
}
