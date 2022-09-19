import useSWR from 'swr'
import { fetcher } from '~/api'
import { System } from '~/ducks/bestillingStatus/bestillingStatusMapper'

const getBestillingerGruppeUrl = (gruppeId: string | number) =>
	`/dolly-backend/api/v1/bestilling/gruppe/${gruppeId}`

const getBestillingByIdUrl = (bestillingId: string | number) =>
	`/dolly-backend/api/v1/bestilling/${bestillingId}`

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

export const useBestillingerGruppe = (gruppeId: string | number) => {
	if (!gruppeId) {
		return {
			loading: false,
			error: 'GruppeId mangler!',
		}
	}
	const { data, error } = useSWR<Bestilling[], Error>(getBestillingerGruppeUrl(gruppeId), fetcher)

	const bestillingerSorted = data
		?.sort((bestilling, bestilling2) => (bestilling.id < bestilling2.id ? 1 : -1))
		.reduce((acc: { [key: string]: Bestilling }, curr) => ((acc[curr.id] = curr), acc), {})

	return {
		bestillinger: data,
		bestillingerById: bestillingerSorted,
		loading: !error && !data,
		error: error,
	}
}

export const useBestillingById = (bestillingId: string, autoRefresh = false) => {
	if (!bestillingId) {
		return {
			loading: false,
			error: 'bestillingId mangler!',
		}
	}
	const { data, error } = useSWR<Bestilling, Error>(getBestillingByIdUrl(bestillingId), fetcher, {
		refreshInterval: autoRefresh ? 2000 : null,
	})

	return {
		bestilling: data,
		loading: !error && !data,
		error: error,
	}
}
