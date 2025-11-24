import useSWR from 'swr'
import Request from '@/service/services/Request'

const apiOversiktUrl = '/testnav-oversikt-service/api/v1/apioversikt'

export const useApiOversikt = () => {
	const { data, isLoading, error } = useSWR(apiOversiktUrl, (url) => Request.get(url))

	return {
		apiOversikt: data,
		loading: isLoading,
		error: error,
	}
}
