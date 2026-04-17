import { fetcher } from '@/api'
import { AxiosError } from 'axios'
import useSWR from 'swr'

type TsmSykemeldingResponse = {
	sykmeldinger: Array<{
		sykmeldingId: string
		aktivitet: Array<{
			grad?: number
			fom: any
			tom: any
		}>
		ident: string
	}>
}

export const useTsmSykemelding = (ident: string, retryCount = 8) => {
	const shouldFetch = !!ident
	const { data, isLoading, error } = useSWR<TsmSykemeldingResponse, AxiosError<any>>(
		shouldFetch ? ['/testnav-sykemelding-proxy/tsm/api/sykmelding/ident', ident] : null,
		([url, _ident]) =>
			fetcher(url, {
				'X-ident': ident,
			}),
		{ errorRetryCount: retryCount },
	)

	return {
		sykemeldinger: data?.sykmeldinger || [],
		loading: isLoading,
		error: error,
	}
}
