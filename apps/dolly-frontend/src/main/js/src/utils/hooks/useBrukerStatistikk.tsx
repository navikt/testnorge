import useSWR from 'swr'
import { fetcher } from '@/api'
import DollyEndpoints from '@/service/services/dolly/DollyEndpoints'

export type BrukeradferdKriterium = {
	fagsystem: string
	antall: number
	detaljer?: Record<string, number> | null
}

export type MinSideBestillingerDTO = {
	periode?: string | number[] | null
	antallNyBestillinger?: number | null
	antallGjenopprettinger?: number | null
	antallNyePersoner?: number | null
	dato?: string | null
	kriterier?: BrukeradferdKriterium[] | null
}

const SWR_OPTIONS = {
	revalidateOnFocus: false,
	revalidateIfStale: false,
	dedupingInterval: 60000,
}

export const useBrukerBestillingerOversikt = () => {
	const { data, isLoading, error } = useSWR<MinSideBestillingerDTO[], Error>(
		DollyEndpoints.brukerBestillinger(),
		(url: string) => fetcher(url, null, 60000),
		SWR_OPTIONS,
	)

	return {
		bestillingerOversikt: data ?? [],
		loadingBestillingerOversikt: isLoading,
		bestillingerOversiktError: error,
	}
}

export const useBrukerBestillingerDetaljert = (year: number | null, month: string | null) => {
	const shouldFetch = year !== null && Boolean(month)
	const { data, isLoading, error } = useSWR<MinSideBestillingerDTO[], Error>(
		shouldFetch ? DollyEndpoints.brukerBestillingerDetaljert(year, month as string) : null,
		(url: string) => fetcher(url, null, 60000),
		SWR_OPTIONS,
	)

	return {
		bestillingerDetaljert: data ?? [],
		loadingBestillingerDetaljert: isLoading,
		bestillingerDetaljertError: error,
	}
}
