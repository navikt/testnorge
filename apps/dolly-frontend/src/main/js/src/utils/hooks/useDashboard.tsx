import useSWR from 'swr'
import { fetcher } from '@/api'
import DollyEndpoints from '@/service/services/dolly/DollyEndpoints'

export type DashboardPersonerDTO = {
	dato: string
	personerTotalt: number
	nye: number
	gjenopprettede: number
	pdlFeil: number
	andreFeil: number
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

export const useDashboard = () => {
	const {
		data: dashboardPersoner,
		isLoading: loadingDashboardPersoner,
		error: dashboardPersonerError,
		mutate: mutateDashboardPersoner,
	} = useSWR<DashboardPersonerDTO[], Error>(
		DollyEndpoints.dashboardPersoner(),
		(url) => fetcher(url, null, 30000),
		{
			revalidateOnFocus: false,
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
		dedupingInterval: 15000,
	})

	return {
		dashboardPersoner: dashboardPersoner ?? [],
		dashboardTeams: dashboardTeams ?? [],
		dashboardOrganisasjoner: dashboardOrganisasjoner ?? [],
		dashboardDollyTeams: dashboardDollyTeams ?? [],
		loadingDashboardPersoner,
		loadingDashboardTeams,
		loadingDashboardOrganisasjoner,
		loadingDashboardDollyTeams,
		dashboardPersonerError,
		dashboardTeamsError,
		dashboardOrganisasjonerError,
		dashboardDollyTeamsError,
		reloadDashboard: () =>
			Promise.all([
				mutateDashboardPersoner(),
				mutateDashboardTeams(),
				mutateDashboardOrganisasjoner(),
				mutateDashboardDollyTeams(),
			]),
	}
}
