import useSWR from 'swr'
import { fetcher } from '@/api'

const getStatistikkUrl = (brukerId: string) =>
	`/dolly-backend/api/v1/statistikk?brukerId=${brukerId}`

type DollyStatistikk = {
	antallBestillinger: number
	antallIdenter: number
}

export const useCurrentBrukerStatistikk = (brukerId: string) => {
	const { data, isLoading, error } = useSWR<DollyStatistikk, Error>(
		getStatistikkUrl(brukerId),
		fetcher,
	)

	return {
		dollyStatistikk: data,
		loading: isLoading,
		error: error,
	}
}
