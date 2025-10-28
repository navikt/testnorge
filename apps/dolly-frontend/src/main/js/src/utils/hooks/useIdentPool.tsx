import useSWR from 'swr'
import Request from '@/service/services/Request'

const identPoolUrl = (ident: string) => `/testnav-ident-pool/api/v2/ident/valider/${ident}`

export const useValiderIdent = (ident: string) => {
	if (!ident) {
		return {
			loading: false,
			error: 'Ident mangler!',
		}
	}

	const { data, isLoading, error } = useSWR(identPoolUrl(ident), (url: string) => Request.get(url))

	return {
		validering: data,
		loading: isLoading,
		error: error,
	}
}
