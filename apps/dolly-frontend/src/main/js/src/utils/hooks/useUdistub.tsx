import useSWR from 'swr'
import { fetcher } from '@/api'

const udistubUrl = (ident: string) => `/testnav-udistub-proxy/api/v1/person/${ident}`

export const useUdistub = (ident: string, harUdistubBestilling: boolean, retryCount = 8) => {
	const { data, isLoading, error, mutate } = useSWR<any, Error>(
		harUdistubBestilling ? udistubUrl(ident) : null,
		fetcher,
		{ errorRetryCount: retryCount },
	)

	if (!ident) {
		return {
			loading: false,
			error: 'Ident mangler!',
		}
	}

	return {
		udistub: data?.person,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}
