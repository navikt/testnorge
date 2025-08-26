import useSWR from 'swr'
import { fetcher, multiFetcherAareg } from '@/api'
import {
	Organisasjon,
	OrganisasjonFasteData,
	OrganisasjonForvalterData
} from '@/service/services/organisasjonforvalter/types'
import { Bestillingsinformasjon } from '@/components/bestilling/sammendrag/miljoeStatus/MiljoeStatus'
import { Arbeidsforhold } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'
import * as _ from 'lodash'
import { useMemo } from 'react'

type MiljoDataListe = {
	miljo: string
	data: Array<Arbeidsforhold>
}

const getOrganisasjonerUrl = (brukerId: string) =>
	`/dolly-backend/api/v1/organisasjon?brukerId=${brukerId}`

const getOrganisasjonBestillingerUrl = (brukerId: string) =>
	`/dolly-backend/api/v1/organisasjon/bestilling/bestillingsstatus?brukerId=${brukerId}`

const getOrganisasjonBestillingStatusUrl = (bestillingId: number | string) =>
	`/dolly-backend/api/v1/organisasjon/bestilling?bestillingId=${bestillingId}`

const getDollyFasteDataOrganisasjonerUrl = (kanHaArbeidsforhold?: boolean) =>
	`/testnav-organisasjon-faste-data-service/api/v1/organisasjoner?gruppe=DOLLY${
		typeof kanHaArbeidsforhold === 'boolean' ? '&kanHaArbeidsforhold=' + kanHaArbeidsforhold : ''
	}`

const getFasteDataOrganisasjonUrl = (orgnummer: string) =>
	orgnummer ? `/testnav-organisasjon-faste-data-service/api/v1/organisasjoner/${orgnummer}` : null

const getOrganisasjonForvalterUrl = (orgnummer: string) =>
	orgnummer
		? `/testnav-organisasjon-forvalter/api/v2/organisasjoner/framiljoe?orgnummer=${orgnummer}`
		: null

const getArbeidsforholdUrl = (miljoer: string[]) => {
	return miljoer.map((miljoe) => ({
		url: `/testnav-aareg-proxy/${miljoe}/api/v1/arbeidstaker/arbeidsforhold?arbeidsforholdtype=forenkletOppgjoersordning,frilanserOppdragstakerHonorarPersonerMm,maritimtArbeidsforhold,ordinaertArbeidsforhold`,
		miljo: miljoe,
	}))
}

const fasteDataFallback = [
	{
		orgnummer: '896929119',
		enhetstype: 'BEDR',
		navn: 'Sauefabrikk',
	},
	{
		orgnummer: '947064649',
		enhetstype: 'BEDR',
		navn: 'Sjokkerende elektriker',
	},
	{
		orgnummer: '967170232',
		enhetstype: 'BEDR',
		navn: 'Snill torpedo',
	},
	{
		orgnummer: '972674818',
		enhetstype: 'BEDR',
		navn: 'Pengeløs Sparebank',
	},
	{
		orgnummer: '839942907',
		enhetstype: 'BEDR',
		navn: 'Hårreisende frisør',
	},
	{
		orgnummer: '805824352',
		enhetstype: 'BEDR',
		navn: 'Vegansk slakteri',
	},
	{
		orgnummer: '907670201',
		enhetstype: 'BEDR',
		navn: 'Klonelabben',
	},
]

export type Bestillingsstatus = {
	id: number
	environments: string[]
	antallIdenter?: number
	antallIdenterOpprettet?: number
	antallLevert?: number
	bestilling: any
	bruker: any
	gruppeId: number
	ferdig: boolean
	feil?: any
	sistOppdatert: Date
	opprettetFraGruppeId: number
	gjenopprettetFraIdent: number
	opprettetFraId: number
	status: Bestillingsinformasjon[]
	systeminfo: string
	stoppet: boolean
}

export const useDollyOrganisasjoner = (brukerId?: string) => {
	if (!brukerId) {
		return {
			loading: false,
			error: 'BrukerId mangler!',
		}
	}

	const { data, isLoading, error } = useSWR<Organisasjon[], Error>(
		getOrganisasjonerUrl(brukerId),
		fetcher,
	)

	return {
		organisasjoner: data,
		loading: isLoading,
		error: error,
	}
}

export const useDollyFasteDataOrganisasjoner = (kanHaArbeidsforhold?: boolean) => {
	const { data, isLoading, error } = useSWR<OrganisasjonFasteData[], Error>(
		getDollyFasteDataOrganisasjonerUrl(kanHaArbeidsforhold),
		fetcher,
		{ fallbackData: fasteDataFallback },
	)

	return {
		organisasjoner: data,
		loading: isLoading,
		error: error,
	}
}

export const useFasteDataOrganisasjon = (orgnummer: string) => {
	const { data, isLoading, error } = useSWR<OrganisasjonFasteData, Error>(
		getFasteDataOrganisasjonUrl(orgnummer),
		fetcher,
	)

	if (!orgnummer) {
		return {
			loading: false,
			error: 'Organisasjonsnummer mangler!',
		}
	}

	return {
		organisasjon: data,
		loading: isLoading,
		error: error,
	}
}

const fetchAllOrganisasjoner = async (urls: (string | null)[]) => {
	const validUrls = urls.filter((url) => url !== null) as string[]

	if (validUrls.length === 0) {
		return []
	}

	return await Promise.all(
		validUrls.map((url) =>
			fetcher(url, { 'Content-Type': 'application/json' }).catch((error) => {
				throw error
			}),
		),
	)
}

export const useOrganisasjonForvalter = (orgnummere: (string | undefined)[]) => {
	const filteredOrgnummere = orgnummere.filter((orgnummer) => orgnummer !== undefined)
	const urls = filteredOrgnummere.map((orgnummer) => getOrganisasjonForvalterUrl(orgnummer))

	const { data, isLoading, error } = useSWR<OrganisasjonForvalterData[], Error>(
		urls.length > 0 ? urls : null,
		fetchAllOrganisasjoner,
	)

	const dataFiltered = useMemo(
		() => (data ? data.filter((org) => org !== null && !_.isEmpty(org)) : []),
		[data],
	)

	return {
		organisasjoner: dataFiltered,
		loading: isLoading,
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
	const { data, isLoading, error } = useSWR<Bestillingsstatus[], Error>(
		getOrganisasjonBestillingerUrl(brukerId),
		fetcher,
		{
			refreshInterval: autoRefresh ? 4000 : 0,
			dedupingInterval: autoRefresh ? 4000 : 0,
		},
	)

	const bestillingerSorted = data
		?.sort?.((bestilling, bestilling2) => (bestilling.id < bestilling2.id ? 1 : -1))
		.reduce((acc: { [key: string]: Bestillingsstatus }, curr) => ((acc[curr.id] = curr), acc), {})

	return {
		bestillinger: data,
		bestillingerById: bestillingerSorted,
		loading: isLoading,
		error: error,
	}
}

export const useOrganisasjonBestillingStatus = (
	bestillingId: number | string,
	erOrganisasjon: boolean,
	autoRefresh = false,
) => {
	const shouldFetch = () => {
		return bestillingId && erOrganisasjon
	}

	const { data, isLoading, error } = useSWR<Bestillingsstatus[], Error>(
		shouldFetch() ? getOrganisasjonBestillingStatusUrl(bestillingId) : null,
		fetcher,
		{
			refreshInterval: autoRefresh ? 3000 : 0,
			dedupingInterval: autoRefresh ? 3000 : 0,
		},
	)

	return {
		bestillingStatus: data,
		loading: isLoading,
		error: error,
	}
}

export const useArbeidsforhold = (ident: string, harAaregBestilling: boolean, miljoe?: string) => {
	const { dollyEnvironmentList } = useDollyEnvironments()
	const unsupportedEnvironments = ['qx']
	const filteredEnvironments = dollyEnvironmentList
		?.map((miljoe) => miljoe.id)
		?.filter((miljoe) => !unsupportedEnvironments.includes(miljoe))

	if (!ident) {
		return {
			loading: false,
			error: 'Ident mangler!',
		}
	}

	if (!harAaregBestilling) {
		return {
			loading: false,
		}
	}

	const miljoer = miljoe ? [miljoe] : filteredEnvironments

	const { data, isLoading, error } = useSWR<Array<MiljoDataListe>, Error>(
		[getArbeidsforholdUrl(miljoer), { 'Nav-Personident': ident }],
		([url, headers]) => multiFetcherAareg(url, headers),
	)

	return {
		arbeidsforhold: data?.sort?.((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: isLoading,
		error: error,
	}
}
