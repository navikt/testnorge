import useSWR from 'swr'
import Request from '@/service/services/Request'

const identPoolUrl = '/testnav-ident-pool/api/v2/ident/validerflere'

export const useValiderIdenter = (identer: string) => {
	const { data, isLoading, error, mutate } = useSWR(
		identer ? [identPoolUrl, { identer }] : null,
		([url, body]) => Request.post(url, body),
		{ dedupingInterval: 400 },
	)

	return {
		validering: data?.data,
		loading: isLoading,
		error,
		mutate,
	}
}
