import useSWR from 'swr'
import { fetcher } from '@/api'
import useSWRImmutable from 'swr/immutable'

const getSigrunstubBaseUrl = () => `/testnav-sigrunstub-proxy/api/v1`

export const usePensjonsgivendeInntektKodeverk = () => {
	const { data, isLoading, error } = useSWRImmutable<any, Error>(
		`${getSigrunstubBaseUrl()}/pensjonsgivendeinntektforfolketrygden/kodeverk`,
		fetcher,
	)

	return {
		kodeverk: data,
		loading: isLoading,
		error: error,
	}
}

export const usePensjonsgivendeInntektSkatteordning = () => {
	const { data, isLoading, error } = useSWR<any, Error>(
		`${getSigrunstubBaseUrl()}/pensjonsgivendeinntektforfolketrygden/skatteordning`,
		fetcher,
	)

	return {
		skatteordning: data,
		loading: isLoading,
		error: error,
	}
}
