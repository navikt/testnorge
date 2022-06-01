import useSWR from 'swr'
import { fetcher } from '~/api'
import { Organisasjon } from '~/service/services/organisasjonforvalter/types'
import { isEmpty } from 'lodash'

const getOrganisasjonerUrl = (organisasjoner: string[]) =>
	`/testnav-organisasjon-forvalter/api/v2/organisasjoner?orgnumre=${organisasjoner}`

const getOrganisasjonerForBrukerUrl = (brukerId: string) =>
	`/testnav-organisasjon-forvalter/api/v2/organisasjoner/alle?brukerId=${brukerId}`

const getOrganisasjonBestillingerUrl = (brukerId: string) =>
	`/dolly-backend/api/v1/organisasjon/bestillingsstatus?brukerId=${brukerId}`

const getOrganisasjonBestillingStatusUrl = (organisasjoner: string[]) =>
	`/testnav-organisasjon-forvalter/api/v2/organisasjoner/ordrestatus?orgnumre=${organisasjoner}`

export type Bestillingsstatus = {
	id: number
	environments: string[]
	antallIdenter: number
	antallLevert: number
	bestilling: any
	bruker: any
	gruppeId: number
	ferdig: boolean
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

export const useOrganisasjonBestilling = (brukerId: string, autoRefresh: boolean = false) => {
	if (!brukerId) {
		return {
			loading: false,
			error: 'BrukerId mangler!',
		}
	}
	const { data, error } = useSWR<Bestillingsstatus[], Error>(
		getOrganisasjonBestillingerUrl(brukerId),
		fetcher,
		autoRefresh && { refreshInterval: 1000 }
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
	orgnummer: string[],
	autoRefresh: boolean = false
) => {
	if (!orgnummer) {
		return {
			loading: false,
			error: 'Orgnummer mangler!',
		}
	}
	const { data, error } = useSWR<Bestillingsstatus[], Error>(
		getOrganisasjonBestillingStatusUrl(orgnummer),
		fetcher,
		autoRefresh && { refreshInterval: 1000 }
	)

	return {
		bestillingStatus: data,
		loading: !error && !data,
		error: error,
	}
}
