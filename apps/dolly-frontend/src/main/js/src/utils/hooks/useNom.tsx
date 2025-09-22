import useSWR from 'swr'
import axios from 'axios'

const baseUrl = '/testnav-nom-proxy'

export const useNomData = (ident: any) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		ident ? `${baseUrl}/api/v1/dolly/hentRessurs` : null,
		async (url) => {
			const res = await axios.post(url, ident, {
				headers: {
					'Content-Type': 'text/plain',
				},
			})
			return res.data
		},
	)

	return {
		nomData: data,
		loading: isLoading,
		error: error,
	}
}
