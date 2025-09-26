import { fetcher } from '@/api'
import useSWR from 'swr'

export const useHentLagredeSoek = (soekType: string) => {
	const { data, isLoading, error } = useSWR<any, Error>(
		`/dolly-backend/api/v1/soek?soekType=${soekType}`,
		fetcher,
	)

	return {
		lagredeSoek: data,
		loading: isLoading,
		error: error,
	}
}
