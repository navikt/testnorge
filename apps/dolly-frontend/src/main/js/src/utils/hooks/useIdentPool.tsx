import useSWR from 'swr'
import { fetcher } from '@/api'

const identPoolUrl = (ident: string) => `/testnav-ident-pool/api/v2/ident/valider/${ident}`

export const useValiderIdent = (ident: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(ident ? identPoolUrl(ident) : null, fetcher)

	return {
		validering: data,
		loading: isLoading,
		error,
	}
}
