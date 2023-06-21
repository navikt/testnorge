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
	// if (!request) {
	// 	return {
	// 		loading: false,
	// 	}
	// }
	// if (!gruppeId) {
	//     return {
	//         loading: false,
	//         error: 'GruppeId mangler!',
	//     }
	// }

	console.log('request: ', request) //TODO - SLETT MEG
	// console.log('getMatrikkeladresserUrl(request): ', getMatrikkeladresserUrl(request)) //TODO - SLETT MEG

	// const { data, isLoading, error } = useSWR<string[], Error>(
	// 	// getMatrikkeladresserUrl(request),
	// 	'/testnav-adresse-service/api/v1/adresser/matrikkeladresse?kommunenummer=5403',
	// 	fetcher
	// )
	const { data, isLoading, error } = useSWR<string[], Error>(
		request ? [getMatrikkeladresserUrl(request), { antall: antall.toString() }] : null,
		([url, headers]) => fetcher(url, headers)
		// {antall: antall.toString()}
	)
	console.log('data: ', data) //TODO - SLETT MEG
	console.log('isLoading: ', isLoading) //TODO - SLETT MEG
	console.log('error: ', error) //TODO - SLETT MEG
	console.log('typeof error: ', typeof error) //TODO - SLETT MEG

	const ingenAdresseFunnet = error?.message?.includes('Ingen adresse funnet')
	console.log('error?: ', error?.message) //TODO - SLETT MEG
	console.log('ingenAdresseFunnet: ', ingenAdresseFunnet) //TODO - SLETT MEG

	return {
		adresser: data,
		loading: isLoading,
		notFound: ingenAdresseFunnet,
		error: error,
	}
}
