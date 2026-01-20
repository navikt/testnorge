import useSWR from 'swr'
import { fetcher, multiFetcherAll } from '@/api'
import { System } from '@/ducks/bestillingStatus/bestillingStatusMapper'

const getBestillingerGruppeUrl = (gruppeId: string | number) =>
	`/dolly-backend/api/v1/bestilling/gruppe/${gruppeId}`

const getMiljoerForGruppeUrl = (gruppeId: string | number) =>
	`/dolly-backend/api/v1/bestilling/gruppe/${gruppeId}/miljoer`

const getIkkeFerdigBestillingerGruppeUrl = (gruppeId: string | number) =>
	`/dolly-backend/api/v1/bestilling/gruppe/${gruppeId}/ikkeferdig`

const getBestillingByIdUrl = (bestillingId: string | number) =>
	`/dolly-backend/api/v1/bestilling/${bestillingId}`

const getMultipleBestillingByIdUrl = (bestillingIdListe: Array<string>) =>
	bestillingIdListe?.map((id) => `/dolly-backend/api/v1/bestilling/${id}`)

export type Bestilling = {
	id: number
	antallIdenter: number
	antallLevert: number
	bestilling: any
	ferdig: boolean
	sistOppdatert: Date
	opprettetFraId: string
	gjenopprettetFraIdent: string
	opprettetFraGruppeId: string
	bruker: {
		brukerId: string
		brukernavn: string
		brukertype: string
		epost: string
	}
	gruppeId: number
	stoppet: boolean
	environments: string[]
	status: System[]
}

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

export const useBestillingerGruppe = (gruppeId: string | number) => {
	if (!gruppeId) {
		return {
			bestillinger: undefined as Bestilling[] | undefined,
			bestillingerById: undefined as Record<string, Bestilling> | undefined,
			loading: false,
			error: 'GruppeId mangler!',
		}
	}

	const { data, isLoading, error } = useSWR<Bestilling[], Error>(
		getBestillingerGruppeUrl(gruppeId),
		fetcher,
	)

	const bestillingerSorted = data
		?.sort?.((a, b) => (a.id < b.id ? 1 : -1))
		.reduce<Record<string, Bestilling>>((acc, curr) => {
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
) => {
	const shouldFetch = !!bestillingId && !erOrganisasjon
	const key: string | null = shouldFetch ? getBestillingByIdUrl(bestillingId) : null

	const { data, isLoading, error } = useSWR<Bestilling, Error>(key, fetcher, {
		refreshInterval: autoRefresh ? 1000 : 0,
		dedupingInterval: autoRefresh ? 1000 : 2000,
	})

	return {
		bestilling: data,
		loading: isLoading,
		error: error,
	}
}

export const useBestilteMiljoer = (
	bestillingIdListe: Array<string> | undefined,
	fagsystem: string,
) => {
	if (!bestillingIdListe || bestillingIdListe?.length < 1) {
		return {
			bestilteMiljoer: undefined as string[] | undefined,
			loading: false,
			error: 'Bestilling-id mangler!',
		}
	}

	const { data, isLoading, error } = useSWR<Array<Bestilling>, Error>(
		getMultipleBestillingByIdUrl(bestillingIdListe),
		multiFetcherAll,
	)

	const miljoer: string[] = []
	data?.forEach?.((bestilling) => {
		bestilling?.environments?.forEach((miljo) => {
			if (!miljoer.includes(miljo) && bestilling.status?.some((s) => s.id === fagsystem)) {
				miljoer.push(miljo)
			}
		})
	})

	return {
		bestilteMiljoer: miljoer,
		loading: isLoading,
		error: error,
	}
}

export const useBestilteMiljoerAlleFagsystemer = (bestillingIdListe: Array<string> | undefined) => {
	if (!bestillingIdListe || bestillingIdListe?.length < 1) {
		return {
			bestilteMiljoer: undefined as string[] | undefined,
			loading: false,
			error: 'Bestilling-id mangler!',
		}
	}

	const { data, isLoading, error } = useSWR<Array<Bestilling>, Error>(
		getMultipleBestillingByIdUrl(bestillingIdListe),
		multiFetcherAll,
	)

	const bestilteMiljoer = [
		...new Set(data?.flatMap((bestilling) => bestilling?.environments ?? [])),
	]

	return {
		bestilteMiljoer: bestilteMiljoer,
		loading: isLoading,
		error: error,
	}
}
