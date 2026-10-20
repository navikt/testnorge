import useSWR from 'swr'
import { fetcher } from '@/api'
import { Mal, OversiktResponse } from '@/utils/hooks/useMaler'

const templateSearchServiceUrl = '/testnav-template-search-service/api/v1/tenor/maler/personer'

export const useSoekMalerOversikt = () => {
	const { data, isLoading, error, mutate } = useSWR<OversiktResponse, Error>(
		`${templateSearchServiceUrl}/oversikt`,
		fetcher,
	)
	return {
		brukere: data?.brukereMedMaler,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}

export const useSoekMalerBruker = (brukerId?: string) => {
	const { data, isLoading, error, mutate } = useSWR<Mal[], Error>(
		brukerId ? `${templateSearchServiceUrl}/brukerId/${brukerId}` : null,
		fetcher,
	)

	return {
		maler: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}
