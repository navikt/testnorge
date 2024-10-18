import useSWR from 'swr'
import { fetcher } from '@/api'

const baseUrl = '/testnav-yrkesskade-proxy'
const getKodeverkUrl = (kodeverktype) => `${baseUrl}/api/v1/kodeverk/${kodeverktype}`

export const useYrkesskadeKodeverk = (kodeverktype) => {
	const { data, isLoading, error } = useSWR<any, Error>(getKodeverkUrl(kodeverktype), fetcher)

	return {
		kodeverkData: data?.kodeverk,
		loading: isLoading,
		error: error,
	}
}
