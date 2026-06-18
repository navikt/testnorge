import useSWR from 'swr'
import { postFetcher } from '@/api'

const kelvinAapBaseUrl = '/testnav-dolly-proxy/kelvin-aap/api/test/'

export const useKelvinAapBehandlingStatus = (ident: string, harKelvinAapBestilling: boolean) => {
	const shouldFetch = Boolean(ident && harKelvinAapBestilling)

	const { data, isLoading, error } = useSWR<any, Error>(
		shouldFetch ? [`${kelvinAapBaseUrl}behandlingStatus`, ident] : null,
		([url, ident]: [string, string]) => postFetcher(url, { ident }),
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	return {
		kelvinAapData: data,
		loading: isLoading,
		error: error,
	}
}
