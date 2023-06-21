import useSWR from 'swr'
import { fetcher } from '@/api'

const getQueryParms = (request: any) => {
	const keys = Object.keys(request).filter(
		(key: keyof Request) => request[key] && request[key] !== ''
	)
	if (keys.length === 0) {
		return ''
	}
	return '?' + keys.map((key: keyof Request) => `${key}=${request[key]}`).join('&')
}

const getMatrikkeladresserUrl = (request: any) =>
	`/testnav-adresse-service/api/v1/adresser/matrikkeladresse${getQueryParms(request)}`

export const useMatrikkelAdresser = (request, antall = 10) => {
	const { data, isLoading, error } = useSWR<string[], Error>(
		request ? [getMatrikkeladresserUrl(request), { antall: antall.toString() }] : null,
		([url, headers]) => fetcher(url, headers)
	)

	const ingenAdresseFunnet = error?.message?.includes('Ingen adresse funnet')

	return {
		adresser: data,
		loading: isLoading,
		notFound: ingenAdresseFunnet,
		error: error,
	}
}
