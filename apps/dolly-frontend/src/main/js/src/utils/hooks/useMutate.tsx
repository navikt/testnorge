import { useSWRConfig } from 'swr'

export const REGEX_BACKEND_GRUPPER = /^\/dolly-backend\/api\/v1\/gruppe/
export const REGEX_BACKEND_BRUKER = /^\/dolly-backend\/api\/v1\/bruker\/current/
export const REGEX_BACKEND_BESTILLINGER = /^\/dolly-backend\/api\/v1\/bestilling/
export const REGEX_BACKEND_ORGANISASJONER = /^\/dolly-backend\/api\/v1\/organisasjon/

export const useMatchMutate = () => {
	const { cache, mutate } = useSWRConfig()

	return (matcher: RegExp, ...args: unknown[]) => {
		if (!(cache instanceof Map)) {
			throw new Error('Expected SWR cache to be a Map')
		}

		const keysToMutate: string[] = []
		for (const key of cache.keys()) {
			if (matcher.test(String(key))) {
				keysToMutate.push(key)
			}
		}

		return Promise.all(keysToMutate.map((key) => mutate(key, ...args)))
	}
}
