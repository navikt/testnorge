import useSWR from 'swr'
import { fetcher } from '@/api'

type AdresseRequest = {
	matrikkelId?: string
	adressenavn?: string
	husnummer?: number
	husbokstav?: string
	postnummer?: string
	poststed?: string
	kommunenummer?: string
	kommunenavn?: string
	bydelsnummer?: string
	bydelsnavn?: string
	tilleggsnavn?: string
	fritekst?: string
}

type MatrikkelAdresseRequest = {
	matrikkelId?: string
	kommunenummer?: string
	gaardsnummer?: string
	bruksnummer?: string
	postnummer?: string
	poststed?: string
	tilleggsnavn?: string
}

const getQueryParms = (request: any) => {
	const keys = Object.keys(request).filter(
		(key: keyof Request) => request[key] && request[key] !== '',
	)
	if (keys.length === 0) {
		return ''
	}
	return '?' + keys.map((key: keyof Request) => `${key}=${request[key]}`).join('&')
}

const getAdresserUrl = (request: any) =>
	`/testnav-adresse-service/api/v1/adresser/vegadresse${getQueryParms(request)}`

const getMatrikkeladresserUrl = (request: any) =>
	`/testnav-adresse-service/api/v1/adresser/matrikkeladresse${getQueryParms(request)}`

export const useAdresser = (request: AdresseRequest, antall: number = 10) => {
	const { data, isLoading, error } = useSWR<string[], Error>(
		request ? [getAdresserUrl(request), { antall: antall.toString() }] : null,
		([url, headers]) => fetcher(url, headers),
	)

	const ingenAdresseFunnet = error?.message?.includes('Ingen adresse funnet')

	return {
		adresser: data,
		loading: isLoading,
		notFound: ingenAdresseFunnet,
		error: error,
	}
}

export const useMatrikkelAdresser = (request: MatrikkelAdresseRequest, antall: number = 10) => {
	const { data, isLoading, error } = useSWR<string[], Error>(
		request ? [getMatrikkeladresserUrl(request), { antall: antall.toString() }] : null,
		([url, headers]) => fetcher(url, headers),
	)

	const ingenAdresseFunnet = error?.message?.includes('Ingen adresse funnet')

	return {
		adresser: data,
		loading: isLoading,
		notFound: ingenAdresseFunnet,
		error: error,
	}
}
