import useSWR from 'swr'
import { fetcher } from '@/api'

import { sideStoerrelseLocalStorageKey } from '@/pages/gruppeOversikt/GruppeOversikt'

const getNavigerUrl = (ident, pageSize) =>
	`/dolly-backend/api/v1/ident/naviger/${ident}${pageSize ? '?pageSize=' + pageSize : ''}`

export const useNaviger = (ident) => {
	const pageSize = localStorage.getItem(sideStoerrelseLocalStorageKey)

	const { data, isLoading, error, mutate } = useSWR(
		ident?.match(/^\d{11}$/) ? getNavigerUrl(ident, pageSize) : null,
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
