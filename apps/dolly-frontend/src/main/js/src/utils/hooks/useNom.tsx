import useSWR from 'swr'
import axios from 'axios'

const baseUrl = '/testnav-nom-proxy'

export const useNomData = (ident: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		ident ? [`${baseUrl}/api/v1/dolly/hentRessurs`, ident] : null,
		async ([url, identValue]: [string, string]) => {
			try {
				const res = await axios.post(url, identValue, {
					headers: {
						'Content-Type': 'text/plain',
					},
				})
				return res.data
			} catch (e: any) {
				if (axios.isAxiosError(e) && e.response?.status === 404) {
					return null
				}
				throw e
			}
		},
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	return {
		nomData: data?.message?.includes('finnes ikke') ? null : data,
		loading: isLoading,
		error: error,
	}
}
