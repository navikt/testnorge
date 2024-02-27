import useSWR from 'swr'
import { fetcher } from '@/api'

const getNavigerUrl = (ident) => `/dolly-backend/api/v1/ident/naviger/${ident}`

export const useNaviger = (ident) => {
	const { data, isLoading, error, mutate } = useSWR(
		ident?.match(/^\d{11}$/) ? getNavigerUrl(ident) : null,
		fetcher,
		{
			shouldRetryOnError: false,
		},
	)

	return {
		result: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}
