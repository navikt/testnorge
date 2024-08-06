import useSWR from 'swr'
import { fetcher } from '@/api'

const getPensjonVedtakUrl = '/testnav-pensjon-testdata-facade-proxy/api/v2/vedtak'

export const usePensjonVedtak = (ident, miljo) => {
	const { data, isLoading, error } = useSWR<string[], Error>(
		[`${getPensjonVedtakUrl}?miljo=${miljo}`, { fnr: ident }],
		([url, headers]) => fetcher(url, headers),
	)

	return {
		vedtakData: data,
		loading: isLoading,
		error: error,
	}
}
