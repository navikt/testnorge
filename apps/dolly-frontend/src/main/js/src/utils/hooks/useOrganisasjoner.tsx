import useSWR from 'swr'
import { fetcher, multiFetcherAareg, multiFetcherAmelding } from '@/api'
import { Organisasjon, OrganisasjonFasteData } from '@/service/services/organisasjonforvalter/types'
import { Bestillingsinformasjon } from '@/components/bestilling/sammendrag/miljoeStatus/MiljoeStatus'
import { Arbeidsforhold } from '@/components/fagsystem/inntektsmelding/InntektsmeldingTypes'
import { useDollyEnvironments } from '@/utils/hooks/useEnvironments'

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

const getDollyFasteDataOrganisasjoner = (kanHaArbeidsforhold?: boolean) =>
	`/testnav-organisasjon-faste-data-service/api/v1/organisasjoner?gruppe=DOLLY${
		typeof kanHaArbeidsforhold === 'boolean' ? '&kanHaArbeidsforhold=' + kanHaArbeidsforhold : ''
	}`

const getFasteDataOrganisasjon = (orgnummer: string) =>
	orgnummer ? `/testnav-organisasjon-faste-data-service/api/v1/organisasjoner/${orgnummer}` : null

const getArbeidsforholdUrl = (miljoer: string[]) => {
	return miljoer.map((miljoe) => ({
		url: `/testnav-aareg-proxy/${miljoe}/api/v1/arbeidstaker/arbeidsforhold?arbeidsforholdtype=forenkletOppgjoersordning,frilanserOppdragstakerHonorarPersonerMm,maritimtArbeidsforhold,ordinaertArbeidsforhold`,
		miljo: miljoe,
	}))
}

const getAmeldingerUrl = (ident: string, miljoer: string[]) => {
	return miljoer.map((miljoe) => ({
		url: `/oppsummeringsdokument-service/api/v1/oppsummeringsdokumenter/identer/${ident}`,
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
	antallIdenter: number
	antallLevert: number
	bestilling: any
	bruker: any
	gruppeId: number
	ferdig: boolean
	feil?: string
	sistOppdatert: Date
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

	const { data, isLoading, error } = useSWR<Organisasjon[], Error>(
		getOrganisasjonerUrl(brukerId),
		fetcher
	)

	return {
		organisasjoner: data,
		loading: isLoading,
		error: error,
	}
}

export const useDollyFasteDataOrganisasjoner = (kanHaArbeidsforhold?: boolean) => {
	const { data, isLoading, error } = useSWR<OrganisasjonFasteData[], Error>(
		getDollyFasteDataOrganisasjoner(kanHaArbeidsforhold),
		fetcher,
		{ fallbackData: fasteDataFallback }
	)

	return {
		organisasjoner: data,
		loading: isLoading,
		error: error,
	}
}

export const useFasteDataOrganisasjon = (orgnummer: string) => {
	const { data, isLoading, error } = useSWR<OrganisasjonFasteData, Error>(
		getFasteDataOrganisasjon(orgnummer),
		fetcher
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
		}
	)

	const bestillingerSorted = data
		?.sort((bestilling, bestilling2) => (bestilling.id < bestilling2.id ? 1 : -1))
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
	const { data, isLoading, error } = useSWR<Bestillingsstatus[], Error>(
		getOrganisasjonBestillingStatusUrl(bestillingId),
		fetcher,
		{
			refreshInterval: autoRefresh ? 3000 : 0,
			dedupingInterval: autoRefresh ? 3000 : 0,
		}
	)

	return {
		bestillingStatus: data,
		loading: isLoading,
		error: error,
	}
}

export const useArbeidsforhold = (ident: string, harAaregBestilling: boolean, miljoe?: string) => {
	const { dollyEnvironmentList } = useDollyEnvironments()
	const unsupportedEnvironments = ['t13', 'qx']
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
		{ dedupingInterval: 30000 }
	)

	return {
		arbeidsforhold: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: isLoading,
		error: error,
	}
}

export const useAmeldinger = (ident: string, harAaregBestilling: boolean, miljoe?: string) => {
	const { dollyEnvironmentList } = useDollyEnvironments()
	const unsupportedEnvironments = ['t13', 'qx']
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
		[getAmeldingerUrl(ident, miljoer)],
		([url, headers]) => multiFetcherAmelding(url, headers),
		{ dedupingInterval: 30000 }
	)

	return {
		ameldinger: data?.sort((a, b) => a.miljo.localeCompare(b.miljo)),
		loading: isLoading,
		error: error,
	}
}
