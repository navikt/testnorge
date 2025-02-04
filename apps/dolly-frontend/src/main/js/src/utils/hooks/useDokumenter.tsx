import useSWR from 'swr'
import { fetcher } from '@/api'

export const useDokumenterFraMal = (malId: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		malId ? `/dolly-backend/api/v1/dokument/mal/${malId}` : null,
		fetcher,
	)

	return {
		dokumenter: data,
		loading: isLoading,
		error: error,
	}
}
