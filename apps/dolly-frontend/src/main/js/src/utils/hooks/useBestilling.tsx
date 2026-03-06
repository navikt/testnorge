import useSWR from 'swr'
import { fetcher } from '@/api'
import { Bestilling } from '@/types/bestilling'

export type { Bestilling }

const getBestillingerGruppeUrl = (gruppeId: string | number) =>
	`/dolly-backend/api/v1/bestilling/gruppe/${gruppeId}`

const getMiljoerForGruppeUrl = (gruppeId: string | number) =>
	`/dolly-backend/api/v1/bestilling/gruppe/${gruppeId}/miljoer`

const getIkkeFerdigBestillingerGruppeUrl = (gruppeId: string | number) =>
	`/dolly-backend/api/v1/bestilling/gruppe/${gruppeId}/ikkeferdig`

const getBestillingByIdUrl = (bestillingId: string | number) =>
	`/dolly-backend/api/v1/bestilling/${bestillingId}`

const getBatchMiljoerUrl = (bestillingIdListe: Array<string>) =>
	`/dolly-backend/api/v1/bestilling/miljoer?bestillingIds=${bestillingIdListe.join(',')}`

type VisningType = 'personer' | 'liste' | string

export const useBestilteMiljoerForGruppe = (gruppeId: string | number) => {
	if (!gruppeId) {
		return {
			miljoer: undefined as string[] | undefined,
			loading: false,
			error: 'GruppeId mangler!',
		}
	}

	const { data, isLoading, error } = useSWR<string[], Error>(
		getMiljoerForGruppeUrl(gruppeId),
		fetcher,
	)

	return {
		miljoer: data,
		loading: isLoading,
		error: error,
	}
}

export const useIkkeFerdigBestillingerGruppe = (
	gruppeId: string | number,
	visning: VisningType,
	sidetall: number,
	sideStoerrelse: number,
	update?: string,
) => {
	if (!gruppeId) {
		return {
			bestillinger: undefined as Bestilling[] | undefined,
			bestillingerById: undefined as Record<string, Bestilling> | undefined,
			loading: false,
			error: 'GruppeId mangler!',
		}
	}

	const updateParam = update ? `?update=${update}` : ''
	const url =
		visning === 'personer'
			? getIkkeFerdigBestillingerGruppeUrl(gruppeId) + updateParam
			: getBestillingerGruppeUrl(gruppeId) + `?page=${sidetall}&pageSize=${sideStoerrelse}`

	const { data, isLoading, error } = useSWR<Bestilling[], Error>(url, fetcher)

	const bestillingerSorted = data
		?.sort?.((a, b) => (a.id < b.id ? 1 : -1))
		?.reduce?.<Record<string, Bestilling>>((acc, curr) => {
			acc[curr.id] = curr
			return acc
		}, {})

	return {
		bestillinger: data,
		bestillingerById: bestillingerSorted,
		loading: isLoading,
		error: error,
	}
}

export const useBestillingById = (
	bestillingId: string | number,
	erOrganisasjon = false,
	autoRefresh = false,
	refreshInterval = 1000,
) => {
	const shouldFetch = !!bestillingId && !erOrganisasjon
	const key: string | null = shouldFetch ? getBestillingByIdUrl(bestillingId) : null

	const { data, isLoading, error, mutate } = useSWR<Bestilling, Error>(key, fetcher, {
		refreshInterval: autoRefresh ? refreshInterval : 0,
		dedupingInterval: autoRefresh ? refreshInterval : 2000,
	})

	return {
		bestilling: data,
		loading: isLoading,
		error: error,
		mutate,
	}
}

export const useBestilteMiljoer = (
	bestillingIdListe: Array<string> | undefined,
	_fagsystem?: string,
) => {
	const shouldFetch = !!bestillingIdListe && bestillingIdListe.length > 0

	const { data, isLoading, error } = useSWR<string[], Error>(
		shouldFetch ? getBatchMiljoerUrl(bestillingIdListe) : null,
		fetcher,
	)

	return {
		bestilteMiljoer: data ?? [],
		loading: isLoading,
		error: shouldFetch ? error : 'Bestilling-id mangler!',
	}
}

export const useBestilteMiljoerAlleFagsystemer = (bestillingIdListe: Array<string> | undefined) => {
	const shouldFetch = !!bestillingIdListe && bestillingIdListe.length > 0

	const { data, isLoading, error } = useSWR<string[], Error>(
		shouldFetch ? getBatchMiljoerUrl(bestillingIdListe) : null,
		fetcher,
	)

	return {
		bestilteMiljoer: data ?? [],
		loading: isLoading,
		error: shouldFetch ? error : 'Bestilling-id mangler!',
	}
}
