import useSWR from 'swr'
import { postFetcher } from '@/api'

const baseUrl = '/testnav-nom-proxy'

export const useNomData = (ident: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		ident ? [`${baseUrl}/api/v1/dolly/hentRessurs`, ident] : null,
		([url, identValue]: [string, string]) =>
			postFetcher(url, identValue, {
				'Content-Type': 'text/plain',
			}),
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	return {
		nomData: data?.message?.includes('finnes ikke') ? null : data,
		loading: isLoading,
		error: error,
	}
}
