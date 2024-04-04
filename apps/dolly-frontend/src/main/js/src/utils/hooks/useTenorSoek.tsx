import useSWR from 'swr'
import Request from '@/service/services/Request'

const tenorSearchUrl = '/testnav-tenor-search-service/api/v1/tenor/testdata'

export const useTenorIdent = (ident: string) => {
	const { data, isLoading, error, mutate } = useSWR(
		ident
			? [
					`${tenorSearchUrl}?kilde=FREG&type=AlleFelter`,
					{
						identifikator: ident,
					},
				]
			: null,
		([url, headers]) => Request.post(url, headers),
	)

	return {
		person: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useTenorOversikt = (request: any, antall = 10, side = 0, seed?: number | null) => {
	const { data, isLoading, error, mutate } = useSWR(
		request
			? [
					`${tenorSearchUrl}/oversikt?antall=${antall}&side=${side}${seed ? '&seed=' + seed : ''}`,
					request,
				]
			: null,
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

	return {
		domain: data,
		loading: isLoading,
		error: error,
	}
}
