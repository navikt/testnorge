import useSWR from 'swr'
import { fetcher } from '@/api'

const getNavigerUrl = (ident) => `/dolly-backend/api/v1/ident/naviger/${ident}`

export const useNaviger = (ident) => {
	const { data, isLoading, error, mutate } = useSWR(ident ? getNavigerUrl(ident) : null, fetcher)

	return {
		result: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}
