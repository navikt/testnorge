import useSWR from 'swr'
import { identpoolFetcher } from '@/api'

const identPoolUrl = '/testnav-ident-pool/api/v2/ident/validerflere'

export const useValiderIdenter = (identer: string) => {
	const { data, isLoading, error, mutate } = useSWR(
		identer ? [identPoolUrl, { identer }] : null,
		([url, body]) => identpoolFetcher(url, body),
		{ dedupingInterval: 400 },
	)

	return {
		validering: data,
		loading: isLoading,
		error,
		mutate,
	}
}
