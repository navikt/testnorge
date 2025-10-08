import useSWR from 'swr'
import axios from 'axios'

const baseUrl = '/testnav-nom-proxy'

export const useNomData = (ident: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		ident ? [`${baseUrl}/api/v1/dolly/hentRessurs`, ident] : null,
		async ([url, identValue]) => {
			const res = await axios.post(url, identValue, {
				headers: {
					'Content-Type': 'text/plain',
				},
			})
			return res.data
		},
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	return {
		nomData: data,
		loading: isLoading,
		error: error,
	}
}
