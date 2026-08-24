import useSWR from 'swr'
import { fetcher, postFetcher } from '@/api'
import useSWRImmutable from 'swr/immutable'

const baseUrl = '/testnav-dolly-proxy/oppfoelgingsvedtak14a/veilarbvedtaksstotte'

export const useOppfoelgingsvedtak14a = (fnr: string, harBestilling: boolean) => {
	const shouldFetch = Boolean(fnr && harBestilling)

	const { data, isLoading, error } = useSWR<any, Error>(
		shouldFetch ? [`${baseUrl}/api/v1/test/vedtak/hent-vedtak`, fnr] : null,
		([url, fnr]: [string, string]) => postFetcher(url, { fnr }),
		{ errorRetryCount: 0, revalidateOnFocus: false },
	)

	return {
		oppfoelgingsvedtak14aData: data,
		loading: isLoading,
		error: error,
	}
}

export const useKodeverkOppfoelgingsvedtak14a = (kodeverk: string) => {
	const { data, isLoading, error } = useSWRImmutable<Record<any, any>, Error>(
		`${baseUrl}/open/api/v2/kodeverk/${kodeverk}`,
		fetcher,
	)

	const options =
		!data || isLoading
			? []
			: data.map((item: any) => ({
					value: item.kode,
					label: item.beskrivelse,
					gammelKode: item.gammelKode,
				}))

	return { options, loading: isLoading, error }
}
