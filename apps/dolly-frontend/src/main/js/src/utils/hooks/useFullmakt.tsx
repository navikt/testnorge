import useSWR from 'swr'
import { fetcher } from '@/api'

type TemaType = {
	label?: string
	value?: string
}

export const useFullmektig = () => {
	const { data, isLoading, error } = useSWR<TemaType, Error>(
		[
			'/testnav-fullmakt-proxy/api/fullmektig',
			{ accept: 'application/json', 'Content-Type': 'application/json' },
		],
		([url, headers]) => fetcher(url, headers),
	)

	return {
		fullmektig: data,
		loading: isLoading,
		error: error,
	}
}

export const useFullmaktTemaMedHandling = () => {
	const { data, isLoading, error } = useSWR<TemaType, Error>(
		[
			'/testnav-fullmakt-proxy/api/fullmektig/temaMedHandling',
			{ accept: 'application/json', 'Content-Type': 'application/json' },
		],
		([url, headers]) => fetcher(url, headers),
	)

	return {
		omraader: data,
		loading: isLoading,
		error: error,
	}
}
