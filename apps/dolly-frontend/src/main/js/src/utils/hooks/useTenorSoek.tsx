import useSWR from 'swr'
import Request from '@/service/services/Request'
import useSWRImmutable from 'swr/immutable'

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
		{ dedupingInterval: 60000 },
	)

	return {
		person: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useTenorOrganisasjon = (orgNummer?: string) => {
	const { data, isLoading, error, mutate } = useSWR(
		orgNummer
			? [
					`${tenorSearchUrl}/organisasjoner?type=Organisasjon`,
					{
						organisasjonsnummer: orgNummer,
					},
				]
			: null,
		([url, headers]) => Request.post(url, headers),
		{ dedupingInterval: 60000 },
	)

	return {
		organisasjon: data,
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
		{ dedupingInterval: 400 },
	)

	return {
		response: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useTenorOversiktOrganisasjoner = (
	request: any,
	antall = 10,
	side = 0,
	seed?: number | null,
) => {
	const { data, isLoading, error, mutate } = useSWR(
		request
			? [
					`${tenorSearchUrl}/organisasjoner/oversikt?antall=${antall}&side=${side}${seed ? '&seed=' + seed : ''}`,
					request,
				]
			: null,
		([url, headers]) => Request.post(url, headers),
		{ dedupingInterval: 400 },
	)

	return {
		response: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useTenorDomain = (lookup: string) => {
	const { data, isLoading, error } = useSWRImmutable(
		lookup ? `${tenorSearchUrl}/domain?lookup=${lookup}` : null,
		(url) => Request.get(url),
	)

	return {
		domain: data,
		loading: isLoading,
		error: error,
	}
}

export const useTenorOrganisasjonDomain = (lookup: string) => {
	const { data, isLoading, error } = useSWRImmutable(
		lookup ? `${tenorSearchUrl}/organisasjoner/domain?lookup=${lookup}` : null,
		(url) => Request.get(url),
	)

	return {
		domain: data,
		loading: isLoading,
		error: error,
	}
}
