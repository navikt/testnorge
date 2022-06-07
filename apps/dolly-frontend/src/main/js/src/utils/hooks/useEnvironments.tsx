import useSWR from 'swr'
import { fetcher } from '~/api'

const getMiljoerUrl = '/testnav-miljoer-service/api/v1/miljoer'

const prefetchedMiljoer = ['t0', 't1', 't3', 't4', 't5', 't13', 'q1', 'q2', 'q4', 'q5', 'qx']

export const useDollyEnvironments = () => {
	const { data, error } = useSWR<string[], Error>(getMiljoerUrl, fetcher, {
		fallbackData: prefetchedMiljoer,
	})

	return {
		dollyEnvironments: _getEnvironmentsSortedByType(data),
		loading: !error && !data,
		error: error,
	}
}

export const _getEnvironmentsSortedByType = (envArray: any[]) => {
	const sortedByType = envArray?.reduce((prev, curr) => {
		const label = curr.toUpperCase()
		const envType = label.charAt(0)
		if (prev[envType]) {
			prev[envType].push({ id: curr, label })
		} else {
			prev[envType] = [{ id: curr, label }]
		}
		return prev
	}, {})

	Object.keys(sortedByType).forEach((key) => {
		const envs = sortedByType[key]
		const sorterteNummer = envs.map((env: { id: string }) => env.id.match(/.{1,3}/g))
		const sorterteEnvs = sorterteNummer
			.map((v: any[]) => v[0])
			.sort(function (a: string, b: string) {
				const prev = parseInt(a.substring(1))
				const current = parseInt(b.substring(1))
				if (prev > current) {
					return 1
				}
				if (prev < current || isNaN(current)) {
					return -1
				}
				return 0
			})

		sorterteEnvs.map((idx: number) => (envs[idx].id = sorterteEnvs[idx]))
		sorterteEnvs.map((idx: number) => (envs[idx].label = sorterteEnvs[idx].toUpperCase()))
	})
	return sortedByType
}
