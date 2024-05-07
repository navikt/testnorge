import useSWR from 'swr'
import { fetcher } from '@/api'

export const useFinnesIDolly = (ident: string) => {
	const { data, error, isLoading } = useSWR<boolean, Error>(
		`dolly-backend/api/v1/ident/finnes/${ident}`,
		// @ts-ignore
		fetcher,
	)

	return {
		finnesIDolly: data,
		loading: isLoading,
		error: error,
	}
}
