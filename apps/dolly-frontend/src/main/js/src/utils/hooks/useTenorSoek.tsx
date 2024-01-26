import useSWR from 'swr'
import Request from '@/service/services/Request'

const tenorSearchUrl = '/testnav-tenor-search-service/api/v1/tenor/testdata'

export const useTenorSoek = (
	type: string,
	request: any,
	kilde?: string,
	fields?: string,
	seed?: number,
) => {
	const { data, isLoading, error, mutate } = useSWR(
		request ? [`${tenorSearchUrl}?type=${type}`, request] : null,
		([url, headers]) => Request.post(url, headers),
	)

	return {
		response: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useTenorDomain = (lookup: string) => {
	const { data, isLoading, error } = useSWR(
		lookup ? `${tenorSearchUrl}/domain?lookup=${lookup}` : null,
		(url) => Request.get(url),
	)
	// console.log('data: ', data) //TODO - SLETT MEG

	return {
		domain: data,
		loading: isLoading,
		error: error,
	}
}
