import useSWR from 'swr'
import { fetcher } from '@/api'

const baseUrl = '/testnav-yrkesskade-proxy'
const getKodeverkUrl = (kodeverktype) => `${baseUrl}/api/v1/kodeverk/${kodeverktype}`

export const useYrkesskadeKodeverk = (kodeverktype) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		getKodeverkUrl(kodeverktype),
		fetcher,
		// [getKodeverkUrl(kodeverktype)],
		// (url) => fetcher(url),
	)
	console.log('data: ', data) //TODO - SLETT MEG

	return {
		kodeverkData: data,
		loading: isLoading,
		error: error,
	}
}
