import useSWR from 'swr'
import { fetcher } from '~/api'

const getMiljoerUrl = '/testnav-miljoer-service/api/v1/miljoer'

const prefetchedMiljoer = ['t0', 't1', 't3', 't4', 't5', 't13', 'q1', 'q2', 'q4', 'q5', 'qx']

export const useDollyEnvironments = () => {
	const { data, error } = useSWR<string[], Error>(getMiljoerUrl, fetcher, {
		fallbackData: prefetchedMiljoer,
	})

	return {
		dollyEnvironments: data,
		loading: !error && !data,
		error: error,
	}
}
