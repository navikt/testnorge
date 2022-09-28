import useSWR from 'swr'
import { fetcher } from '~/api'
import { Organisasjon } from '~/service/services/organisasjonforvalter/types'
import { isEmpty } from 'lodash'
import { Bestillingsinformasjon } from '~/components/bestilling/sammendrag/miljoeStatus/MiljoeStatus'

const getOrganisasjonerUrl = (organisasjoner: string[]) =>
	`/testnav-organisasjon-forvalter/api/v2/organisasjoner?orgnumre=${organisasjoner}`

const getOrganisasjonerForBrukerUrl = (brukerId: string) =>
	`/testnav-organisasjon-forvalter/api/v2/organisasjoner/alle?brukerId=${brukerId}`

const getOrganisasjonBestillingerUrl = (brukerId: string) =>
	`/dolly-backend/api/v1/organisasjon/bestilling/bestillingsstatus?brukerId=${brukerId}`

const getOrganisasjonBestillingStatusUrl = (bestillingId: number) =>
	`/dolly-backend/api/v1/organisasjon/bestilling?bestillingId=${bestillingId}`

export type Bestillingsstatus = {
	id: number
	environments: string[]
	antallIdenter: number
	antallLevert: number
	bestilling: any
	bruker: any
	gruppeId: number
	ferdig: boolean
	feil?: string
	status: Bestillingsinformasjon[]
	systeminfo: string
	stoppet: boolean
}

export const useOrganisasjonerForBruker = (brukerId: string) => {
	if (!brukerId) {
		return {
			loading: false,
			error: 'BrukerId mangler!',
		}
	}
	const { data, error } = useSWR<Organisasjon[], Error>(
		getOrganisasjonerForBrukerUrl(brukerId),
		fetcher
	)

	return {
		organisasjoner: data,
		loading: !error && !data,
		error: error,
	}
}

export const useOrganisasjoner = (organisasjoner: string[]) => {
	if (!organisasjoner || isEmpty(organisasjoner)) {
		return {
			loading: false,
			error: 'Organisasjoner mangler!',
		}
	}

	const { data, error } = useSWR<Organisasjon[], Error>(
		getOrganisasjonerUrl(organisasjoner),
		fetcher
	)

	return {
		organisasjoner: data,
		loading: !error && !data,
		error: error,
	}
}

export const useOrganisasjonBestilling = (brukerId: string, autoRefresh = false) => {
	if (!brukerId) {
		return {
			loading: false,
			error: 'BrukerId mangler!',
		}
	}
	const { data, error } = useSWR<Bestillingsstatus[], Error>(
		getOrganisasjonBestillingerUrl(brukerId),
		fetcher,
		{ refreshInterval: autoRefresh ? 3000 : 0 }
	)

	const bestillingerSorted = data
		?.sort((bestilling, bestilling2) => (bestilling.id < bestilling2.id ? 1 : -1))
		.reduce((acc: { [key: string]: Bestillingsstatus }, curr) => ((acc[curr.id] = curr), acc), {})

	return {
		bestillinger: data,
		bestillingerById: bestillingerSorted,
		loading: !error && !data,
		error: error,
	}
}

export const useOrganisasjonBestillingStatus = (
	bestillingId: number,
	erOrganisasjon: boolean,
	autoRefresh = false
) => {
	if (!erOrganisasjon) {
		return {
			loading: false,
			error: 'Bestilling er ikke org!',
		}
	}
	if (!bestillingId) {
		return {
			loading: false,
			error: 'BestillingId mangler!',
		}
	}
	const { data, error } = useSWR<Bestillingsstatus[], Error>(
		getOrganisasjonBestillingStatusUrl(bestillingId),
		fetcher,
		{ refreshInterval: autoRefresh ? 3000 : 0 }
	)

	return {
		bestillingStatus: data,
		loading: !error && !data,
		error: error,
	}
}
