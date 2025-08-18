import useSWR from 'swr'
import { fetcher } from '@/api'

const teamUrl = '/dolly-backend/api/v1/team'

export const useHentAlleTeam = () => {
	const { data, isLoading, error, mutate } = useSWR<any, Error>(teamUrl, fetcher)

	return {
		alleTeam: data,
		loading: isLoading,
		error: error,
		mutate: mutate,
	}
}
