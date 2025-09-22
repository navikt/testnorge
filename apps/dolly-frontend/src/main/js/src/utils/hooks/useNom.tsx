import useSWR from 'swr'
import api, { fetcher } from '@/api'

const baseUrl = '/testnav-nom-proxy'

export const useNomData = (ident: any) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		ident ? `${baseUrl}/api/v1/dolly/hentRessurs` : null,
		// (url) => fetcher(url, { method: 'POST', params: { ident } }),
		(url) => {
			return api
				.fetchJson(
					url,
					{
						method: 'POST',
						headers: {
							'Content-Type': 'application/json',
						},
					},
					ident,
				)
				.then((response: any) => ({ data: response }))
		},
	)
	return {
		nomData: data,
		loading: isLoading,
		error: error,
	}
}
