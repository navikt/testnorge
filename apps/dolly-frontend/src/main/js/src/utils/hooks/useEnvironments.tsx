import { fetcher } from '@/api'
import useSWRImmutable from 'swr/immutable'
import useSWR from 'swr'

const getMiljoerUrl = '/testnav-miljoer-service/api/v1/miljoer'
const getPensjonMiljoerUrl = '/testnav-pensjon-testdata-facade-proxy/api/v1/miljo'
const getArenaMiljoerUrl = '/testnav-arena-forvalteren-proxy/api/v1/miljoe'
const getInstMiljoerUrl = '/testnav-inst-proxy/api/v1/environment'
const getDokarkivMiljoerUrl = '/testnav-dokarkiv-proxy/rest/miljoe'

const prefetchedMiljoer = ['t3', 't13', 'q1', 'q2', 'q4', 'q5', 'qx']
const prefetchedPensjonMiljoer = ['q1', 'q2']
const prefetchedArenaMiljoer = ['q1', 'q2', 'q4']
const prefetchedInstMiljoer = { institusjonsoppholdEnvironments: ['q1', 'q2'] }
const prefetchedDokarkivMiljoer = ['q1', 'q2']

type InstResponse = {
	institusjonsoppholdEnvironments: string[]
	kdiEnvironments?: string[]
}

export const useDollyEnvironments = () => {
	const { data, isLoading, error } = useSWRImmutable<string[], Error>(getMiljoerUrl, fetcher, {
		fallbackData: prefetchedMiljoer,
	})

	const environmentsSortedByType = _getEnvironmentsSortedByType(data)
	return {
		dollyEnvironments: environmentsSortedByType,
		dollyEnvironmentList: environmentsSortedByType.Q?.concat(environmentsSortedByType?.T),
		loading: isLoading,
		error: error,
	}
}

export const usePensjonEnvironments = () => {
	const { data, isLoading, error } = useSWR<string[], Error>(getPensjonMiljoerUrl, fetcher, {
		fallbackData: prefetchedPensjonMiljoer,
	})

	return {
		pensjonEnvironments: data,
		loading: isLoading,
		error: error,
	}
}

export const useArenaEnvironments = () => {
	const { data, isLoading, error } = useSWR<string[], Error>(
		[getArenaMiljoerUrl, { 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly' }],
		([url, headers]) => fetcher(url, headers),
		{
			fallbackData: prefetchedArenaMiljoer,
		}
	)

	return {
		arenaEnvironments: data,
		loading: isLoading,
		error: error,
	}
}

export const useInstEnvironments = () => {
	const { data, isLoading, error } = useSWR<InstResponse, Error>(getInstMiljoerUrl, fetcher, {
		fallbackData: prefetchedInstMiljoer,
	})

	return {
		instEnvironments: data?.institusjonsoppholdEnvironments,
		loading: isLoading,
		error: error,
	}
}

export const useDokarkivEnvironments = () => {
	const { data, isLoading, error } = useSWR<string[], Error>(
		[getDokarkivMiljoerUrl, { 'Nav-Call-Id': 'dolly', 'Nav-Consumer-Id': 'dolly' }],
		([url, headers]) => fetcher(url, headers),
		{
			fallbackData: prefetchedDokarkivMiljoer,
		}
	)

	return {
		dokarkivEnvironments: data,
		loading: isLoading,
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

		sorterteEnvs.map((_current: any, idx: number) => (envs[idx].id = sorterteEnvs[idx]))
		sorterteEnvs.map(
			(_current: any, idx: number) => (envs[idx].label = sorterteEnvs[idx].toUpperCase())
		)
	})
	return sortedByType
}
