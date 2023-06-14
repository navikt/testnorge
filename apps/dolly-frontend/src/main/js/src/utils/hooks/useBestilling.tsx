import useSWR from 'swr'
import { fetcher, multiFetcherAll } from '@/api'
import { System } from '@/ducks/bestillingStatus/bestillingStatusMapper'
import * as _ from 'lodash-es'

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

export const useBestilteMiljoerForGruppe = (gruppeId: string | number) => {
	if (!gruppeId) {
		return {
			loading: false,
			error: 'GruppeId mangler!',
		}
	}

	const { data, isLoading, error } = useSWR<string[], Error>(
		getMiljoerForGruppeUrl(gruppeId),
		fetcher
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
			loading: false,
			error: 'GruppeId mangler!',
		}
	}

	const { data, isLoading, error } = useSWR<Bestilling[], Error>(
		getBestillingerGruppeUrl(gruppeId),
		fetcher
	)

	const bestillingerSorted = data
		?.sort((bestilling, bestilling2) => (bestilling.id < bestilling2.id ? 1 : -1))
		.reduce((acc: { [key: string]: Bestilling }, curr) => ((acc[curr.id] = curr), acc), {})

	return {
		bestillinger: data,
		bestillingerById: bestillingerSorted,
		loading: isLoading,
		error: error,
	}
}

export const useIkkeFerdigBestillingerGruppe = (
	gruppeId: string | number,
	visning,
	sidetall: number,
	sideStoerrelse: number,
	update: string
) => {
	if (!gruppeId) {
		return {
			loading: false,
			error: 'GruppeId mangler!',
		}
	}

	const updateParam = update ? `?update=${update}` : ''

	const url =
		visning == 'personer'
			? getIkkeFerdigBestillingerGruppeUrl(gruppeId) + updateParam
			: getBestillingerGruppeUrl(gruppeId) + `?page=${sidetall}&pageSize=${sideStoerrelse}`
	const { data, isLoading, error } = useSWR<Bestilling[], Error>(url, fetcher)

	const bestillingerSorted = data
		?.sort((bestilling, bestilling2) => (bestilling.id < bestilling2.id ? 1 : -1))
		.reduce((acc: { [key: string]: Bestilling }, curr) => ((acc[curr.id] = curr), acc), {})

	return {
		bestillinger: data,
		bestillingerById: bestillingerSorted,
		loading: isLoading,
		error: error,
	}
}

export const useBestillingById = (
	bestillingId: string,
	erOrganisasjon = false,
	autoRefresh = false
) => {
	if (!bestillingId) {
		return {
			loading: false,
			error: 'BestillingId mangler!',
		}
	}
	if (erOrganisasjon) {
		return {
			loading: false,
			error: 'Bestilling er org!',
		}
	}
	const { data, isLoading, error } = useSWR<Bestilling, Error>(
		getBestillingByIdUrl(bestillingId),
		fetcher,
		{
			refreshInterval: autoRefresh ? 1000 : null,
			dedupingInterval: autoRefresh ? 1000 : null,
		}
	)

	return {
		bestilling: data,
		loading: isLoading,
		error: error,
	}
}

export const useBestilteMiljoer = (bestillingIdListe: Array<string>, fagsystem: string) => {
	if (!bestillingIdListe || bestillingIdListe?.length < 1) {
		return {
			loading: false,
			error: 'Bestilling-id mangler!',
		}
	}

	const { data, isLoading, error } = useSWR<Array<Bestilling>, Error>(
		getMultipleBestillingByIdUrl(bestillingIdListe),
		multiFetcherAll
	)

	const miljoer = []
	data?.map((bestilling) => {
		bestilling?.environments?.forEach((miljo) => {
			if (!miljoer.includes(miljo) && _.has(bestilling, `bestilling.${fagsystem}`)) {
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