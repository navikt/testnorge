import useSWR from 'swr'
import { fetcher, multiFetcherAll } from '~/api'
import { System } from '~/ducks/bestillingStatus/bestillingStatusMapper'
import _has from 'lodash/has'

const getBestillingerGruppeUrl = (gruppeId: string | number) =>
	`/dolly-backend/api/v1/bestilling/gruppe/${gruppeId}`

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
	const { data, error } = useSWR<Bestilling[], Error>(url, fetcher)

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
	const { data, error } = useSWR<Bestilling, Error>(getBestillingByIdUrl(bestillingId), fetcher, {
		refreshInterval: autoRefresh ? 2000 : null,
		dedupingInterval: autoRefresh ? 2000 : null,
	})

	return {
		bestilling: data,
		loading: !error && !data,
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

	const { data, error } = useSWR<Array<Bestilling>, Error>(
		[getMultipleBestillingByIdUrl(bestillingIdListe)],
		multiFetcherAll
	)
	console.log('data: ', data) //TODO - SLETT MEG
	const miljoer = []
	data?.map((bestilling) => {
		// console.log('bestilling: ', bestilling) //TODO - SLETT MEG
		bestilling?.environments?.forEach((miljo) => {
			// if (!miljoer.includes(miljo) && bestilling.bestilling[fagsystem]) {
			if (!miljoer.includes(miljo) && _has(bestilling, `bestilling.${fagsystem}`)) {
				miljoer.push(miljo)
			}
		})
	})

	return {
		bestilteMiljoer: miljoer,
		loading: !error && !data,
		error: error,
	}
}
