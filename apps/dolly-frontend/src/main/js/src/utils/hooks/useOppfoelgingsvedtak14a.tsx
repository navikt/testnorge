import useSWR from 'swr'
import { postFetcher } from '@/api'

export const useOppfoelgingsvedtak14a = (fnr: string, harBestilling: boolean) => {
	const shouldFetch = Boolean(fnr && harBestilling)

	const { data, isLoading, error } = useSWR<any, Error>(
		shouldFetch
			? [
					'/testnav-dolly-proxy/oppfoelgingsvedtak14a/veilarbvedtaksstotte/api/v1/test/vedtak/hent-vedtak',
					fnr,
				]
			: null,
		([url, fnr]: [string, string]) => postFetcher(url, { fnr }),
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	return {
		oppfoelgingsvedtak14aData: data,
		loading: isLoading,
		error: error,
	}
}
