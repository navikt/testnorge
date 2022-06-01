import { useSWRConfig } from 'swr'

export const REGEX_BACKEND_GRUPPER = /^\/dolly-backend\/api\/v1\/gruppe\//
export const REGEX_BACKEND_BESTILLINGER = /^\/dolly-backend\/api\/v1\/bestillinger\//

export const useMatchMutate = () => {
	const { cache, mutate } = useSWRConfig()
	return (matcher: RegExp, ...args: any) => {
		if (!(cache instanceof Map)) {
			throw new Error('matchMutate krever at cache provider er av type Map')
		}

		const keys = []

		for (const key of cache.keys()) {
			if (matcher.test(key)) {
				keys.push(key)
			}
		}

		const mutations = keys.map((key) => mutate(key, ...args))
		return Promise.all(mutations)
	}
}
