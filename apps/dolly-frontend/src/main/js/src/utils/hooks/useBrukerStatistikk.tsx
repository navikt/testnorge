import useSWR from 'swr'
import { fetcher } from '@/api'
import DollyEndpoints from '@/service/services/dolly/DollyEndpoints'

export type BrukeradferdKriterium = {
	fagsystem: string
	antall: number
	detaljer?: Record<string, number> | null
}

export type BrukerBestillingerDTO = {
	periode?: string | number[] | null
	antallNyeBestillinger?: number | null
	antallGjenopprettedeBestillinger?: number | null
	antallNyePersoner?: number | null
	andelOpprettedeDollyPersoner?: number | null
	andelImporterteTestnorgePersoner?: number | null
	dato?: string | null
	kriterier?: BrukeradferdKriterium[] | null
}

const SWR_OPTIONS = {
	revalidateOnFocus: false,
}

const fetchMedEier = ([url]: [string, string]) => fetcher(url, null, 60000)

export const useBrukerBestillingerOversikt = (eierId?: string) => {
	const { data, isLoading, error } = useSWR<BrukerBestillingerDTO[], Error>(
		eierId ? [DollyEndpoints.brukerBestillinger(), eierId] : null,
		fetchMedEier,
		SWR_OPTIONS,
	)

	return {
		bestillingerOversikt: data ?? [],
		loadingBestillingerOversikt: isLoading,
		bestillingerOversiktError: error,
	}
}

export const useBrukerBestillingerDetaljert = (
	eierId: string | undefined,
	year: number | null,
	month: string | null,
) => {
	const shouldFetch = Boolean(eierId) && year !== null && Boolean(month)
	const { data, isLoading, error } = useSWR<BrukerBestillingerDTO[], Error>(
		shouldFetch
			? [DollyEndpoints.brukerBestillingerDetaljert(year, month as string), eierId as string]
			: null,
		fetchMedEier,
		SWR_OPTIONS,
	)

	return {
		bestillingerDetaljert: data ?? [],
		loadingBestillingerDetaljert: isLoading,
		bestillingerDetaljertError: error,
	}
}

const INGEN_DETALJER: BrukerBestillingerDTO[] = []

export const useBrukerBestillingerDetaljertForMaaneder = (
	eierId: string | undefined,
	maaneder: { year: number; month: string }[],
) => {
	const urls = maaneder.map((maaned) =>
		DollyEndpoints.brukerBestillingerDetaljert(maaned.year, maaned.month),
	)
	const shouldFetch = Boolean(eierId) && urls.length > 0
	const { data, isLoading, error } = useSWR<BrukerBestillingerDTO[], Error>(
		shouldFetch ? ['bruker-bestillinger-detaljert-maaneder', eierId as string, ...urls] : null,
		() =>
			Promise.all(urls.map((url) => fetcher(url, null, 60000))).then((resultater) =>
				resultater.flatMap((rader) => rader ?? []),
			),
		SWR_OPTIONS,
	)

	return {
		bestillingerDetaljert: data ?? INGEN_DETALJER,
		loadingBestillingerDetaljert: isLoading,
		bestillingerDetaljertError: error,
	}
}
