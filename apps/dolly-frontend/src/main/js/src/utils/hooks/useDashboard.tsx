import useSWR from 'swr'
import { fetcher } from '@/api'
import DollyEndpoints from '@/service/services/dolly/DollyEndpoints'

export type DashboardBestillingerDTO = {
	dato: string
	bestillinger: number
	personerTotalt: number
	nye: number
	gjenopprettede: number
	navIdenter: number
	testnorgeIdenter: number
}

export type DashboardTeamEntryDTO = {
	teams: string[]
	antall: number
}

export type DashboardMonthlyTeamEntryDTO = {
	team: string
	unikeBrukere: number
}

export type DashboardTeamsDTO = {
	dato?: string
	entries?: DashboardTeamEntryDTO[]
	interval?: string
	teams?: DashboardMonthlyTeamEntryDTO[]
	totaltUnikeBrukere?: number
	totaltAntallTeams?: number
}

export type DashboardOrganisasjonEntryDTO = {
	organisasjonsnummer: string
	navn: string
	organisasjonsform: string
	unikeBrukere: number
}

export type DashboardDollyTeamEntryDTO = {
	navn: string
	beskrivelse: string
	antallMedlemmer: number
}

export type DashboardOrganisasjonerDTO = {
	interval: string
	totaltUnikeBrukere: number
	totaltAntallOrganisasjoner: number
	organisasjoner: DashboardOrganisasjonEntryDTO[]
}

export type DashboardDollyTeamsDTO = {
	interval: string
	totaltAntallMedlemmer: number
	totaltAntallTeams: number
	teams: DashboardDollyTeamEntryDTO[]
}

export type DashboardOversiktDTO = {
	aar: number
	maaned: string
	totaltAntallPersoner: number
	nye: number
	gjenopprettede: number
}

export type FeilVerdi = string | number | boolean | null | Record<string, unknown> | unknown[]

export type DashboardFeilSummertRad = { bestillingDato: string } & Record<string, number | string>

export type DashboardFeilDetaljertRad = {
	sistOppdatert: string
	bestillingId: number
	ident: string
	master: string
} & Record<string, FeilVerdi>

export const useDashboard = () => {
	const currentDate = new Date()
	const currentYear = currentDate.getFullYear()
	const currentMonth = [
		'JANUARY',
		'FEBRUARY',
		'MARCH',
		'APRIL',
		'MAY',
		'JUNE',
		'JULY',
		'AUGUST',
		'SEPTEMBER',
		'OCTOBER',
		'NOVEMBER',
		'DECEMBER',
	][currentDate.getMonth()]

	const {
		data: dashboardBestillinger,
		isLoading: loadingDashboardBestillinger,
		error: dashboardBestillingerError,
		mutate: mutateDashboardBestillinger,
	} = useSWR<DashboardBestillingerDTO[], Error>(
		DollyEndpoints.dashboardBestillinger(currentYear, currentMonth),
		(url) => fetcher(url, null, 30000),
		{
			revalidateOnFocus: false,
			revalidateIfStale: false,
			dedupingInterval: 15000,
		},
	)

	const {
		data: dashboardTeams,
		isLoading: loadingDashboardTeams,
		error: dashboardTeamsError,
		mutate: mutateDashboardTeams,
	} = useSWR<DashboardTeamsDTO[], Error>(DollyEndpoints.dashboardTeams(), fetcher, {
		revalidateOnFocus: false,
		revalidateIfStale: false,
		dedupingInterval: 15000,
	})

	const {
		data: dashboardOrganisasjoner,
		isLoading: loadingDashboardOrganisasjoner,
		error: dashboardOrganisasjonerError,
		mutate: mutateDashboardOrganisasjoner,
	} = useSWR<DashboardOrganisasjonerDTO[], Error>(
		DollyEndpoints.dashboardOrganisasjoner(),
		(url) => fetcher(url, null, 30000),
		{
			revalidateOnFocus: false,
			revalidateIfStale: false,
			dedupingInterval: 15000,
		},
	)

	const {
		data: dashboardDollyTeams,
		isLoading: loadingDashboardDollyTeams,
		error: dashboardDollyTeamsError,
		mutate: mutateDashboardDollyTeams,
	} = useSWR<DashboardDollyTeamsDTO[], Error>(DollyEndpoints.dashboardDollyTeams(), fetcher, {
		revalidateOnFocus: false,
		revalidateIfStale: false,
		dedupingInterval: 15000,
	})

	const {
		data: dashboardOversikt,
		isLoading: loadingDashboardOversikt,
		error: dashboardOversiktError,
		mutate: mutateDashboardOversikt,
	} = useSWR<DashboardOversiktDTO[], Error>(DollyEndpoints.dashboardOversikt(), fetcher, {
		revalidateOnFocus: false,
		revalidateIfStale: false,
		dedupingInterval: 15000,
	})

	return {
		dashboardBestillinger: dashboardBestillinger ?? [],
		dashboardTeams: dashboardTeams ?? [],
		dashboardOrganisasjoner: dashboardOrganisasjoner ?? [],
		dashboardDollyTeams: dashboardDollyTeams ?? [],
		dashboardOversikt: dashboardOversikt ?? [],
		loadingDashboardBestillinger,
		loadingDashboardTeams,
		loadingDashboardOrganisasjoner,
		loadingDashboardDollyTeams,
		loadingDashboardOversikt,
		dashboardBestillingerError,
		dashboardTeamsError,
		dashboardOrganisasjonerError,
		dashboardDollyTeamsError,
		dashboardOversiktError,
		reloadDashboard: () =>
			Promise.all([
				mutateDashboardBestillinger(),
				mutateDashboardTeams(),
				mutateDashboardOrganisasjoner(),
				mutateDashboardDollyTeams(),
				mutateDashboardOversikt(),
			]),
	}
}

export const useDashboardFeilSummert = (year: number | null, month: string | null) => {
	const shouldFetch = year !== null && Boolean(month)
	const {
		data,
		isLoading: loadingFeilSummert,
		error: feilSummertError,
	} = useSWR<DashboardFeilSummertRad[], Error>(
		shouldFetch ? DollyEndpoints.dashboardFeilSummert(year, month as string) : null,
		(url) => fetcher(url, null, 30000),
		{
			revalidateOnFocus: false,
			revalidateIfStale: false,
			dedupingInterval: 15000,
		},
	)

	return { feilSummert: data ?? [], loadingFeilSummert, feilSummertError }
}

export const useDashboardFeilDetaljert = (
	year: number | null,
	month: string | null,
	day: number | null,
) => {
	const shouldFetch = year !== null && Boolean(month) && day !== null
	const {
		data,
		isLoading: loadingFeilDetaljert,
		error: feilDetaljertError,
	} = useSWR<DashboardFeilDetaljertRad[], Error>(
		shouldFetch ? DollyEndpoints.dashboardFeilDetaljert(year, month as string, day) : null,
		(url) => fetcher(url, null, 30000),
		{
			revalidateOnFocus: false,
			revalidateIfStale: false,
			dedupingInterval: 15000,
		},
	)

	return { feilDetaljert: data ?? [], loadingFeilDetaljert, feilDetaljertError }
}

export const useDashboardFeilForDager = (dager: { year: number; month: string; day: number }[]) => {
	const urls = dager.map((dag) =>
		DollyEndpoints.dashboardFeilDetaljert(dag.year, dag.month, dag.day),
	)
	const shouldFetch = urls.length > 0
	const {
		data,
		isLoading: loadingFeilForDager,
		error: feilForDagerError,
	} = useSWR<DashboardFeilDetaljertRad[], Error>(
		shouldFetch ? ['feil-for-dager', ...urls] : null,
		() =>
			Promise.all(urls.map((url) => fetcher(url, null, 30000))).then((resultater) =>
				resultater.flatMap((rader) => rader ?? []),
			),
		{
			revalidateOnFocus: false,
			revalidateIfStale: false,
			dedupingInterval: 15000,
		},
	)

	return { feilForDager: data ?? [], loadingFeilForDager, feilForDagerError }
}
