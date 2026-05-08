import useSWR from 'swr'
import axios from 'axios'

const kelvinAapBaseUrl = '/testnav-dolly-proxy/kelvin-aap/api/test/'

export const useKelvinAapBehandlingStatus = (ident: string, harKelvinAapBestilling: boolean) => {
	const shouldFetch = Boolean(ident && harKelvinAapBestilling)

	const { data, isLoading, error } = useSWR<any, Error>(
		shouldFetch ? [`${kelvinAapBaseUrl}behandlingStatus`, ident] : null,
		async ([url, ident]: [string, string]) => {
			try {
				const res = await axios.post(
					url,
					{ ident },
					{
						headers: {
							'Content-Type': 'application/json',
						},
					},
				)
				return res.data
			} catch (e: unknown) {
				if (axios.isAxiosError(e) && e.response?.status === 404) {
					return null
				}
				throw e
			}
		},
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	return {
		kelvinAapData: data,
		loading: isLoading,
		error: error,
	}
}
