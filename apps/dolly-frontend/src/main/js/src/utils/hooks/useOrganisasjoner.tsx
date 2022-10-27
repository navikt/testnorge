import useSWR from 'swr'
import { fetcher } from '~/api'
import { Organisasjon } from '~/service/services/organisasjonforvalter/types'
import { Bestillingsinformasjon } from '~/components/bestilling/sammendrag/miljoeStatus/MiljoeStatus'
import { Arbeidsforhold } from '~/components/fagsystem/inntektsmelding/InntektsmeldingTypes'

const getOrganisasjonerUrl = (brukerId: string) =>
	`/dolly-backend/api/v1/organisasjon?brukerId=${brukerId}`

const getOrganisasjonBestillingerUrl = (brukerId: string) =>
	`/dolly-backend/api/v1/organisasjon/bestilling/bestillingsstatus?brukerId=${brukerId}`

const getOrganisasjonBestillingStatusUrl = (bestillingId: number | string) =>
	`/dolly-backend/api/v1/organisasjon/bestilling?bestillingId=${bestillingId}`

const getArbeidsforholdUrl = (miljoe: string) =>
	`/testnav-aaregister-proxy/${miljoe}/api/v1/aareg/arbeidsforhold`

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

export const useOrganisasjoner = (brukerId: string) => {
	if (!brukerId) {
		return {
			loading: false,
			error: 'BrukerId mangler!',
		}
	}

	const { data, error } = useSWR<Organisasjon[], Error>(getOrganisasjonerUrl(brukerId), fetcher)

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
		{ refreshInterval: autoRefresh ? 4000 : 0 }
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
	bestillingId: number | string,
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

export const useArbeidsforhold = (ident: string, miljoe: string) => {
	if (!ident || !miljoe) {
		return {
			loading: false,
			error: 'Ident eller miljø mangler!',
		}
	}

	const { data, error } = useSWR<Arbeidsforhold[], Error>(
		[getArbeidsforholdUrl(miljoe), { ident: ident }],
		fetcher
	)

	return {
		arbeidsforhold: data,
		loading: !error && !data,
		error: error,
	}
}
