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

export const useDashboard = () => {
	const {
		data: dashboardPersoner,
		isLoading: loadingDashboardPersoner,
		error: dashboardPersonerError,
		mutate: mutateDashboardPersoner,
	} = useSWR<DashboardPersonerDTO[], Error>(DollyEndpoints.dashboardPersoner(), fetcher, {
		revalidateOnFocus: false,
		dedupingInterval: 15000,
	})

	const {
		data: dashboardTeams,
		isLoading: loadingDashboardTeams,
		error: dashboardTeamsError,
		mutate: mutateDashboardTeams,
	} = useSWR<DashboardTeamsDTO[], Error>(DollyEndpoints.dashboardTeams(), fetcher, {
		revalidateOnFocus: false,
		dedupingInterval: 15000,
	})

	return {
		dashboardPersoner: dashboardPersoner ?? [],
		dashboardTeams: dashboardTeams ?? [],
		loadingDashboardPersoner,
		loadingDashboardTeams,
		dashboardPersonerError,
		dashboardTeamsError,
		reloadDashboard: () => Promise.all([mutateDashboardPersoner(), mutateDashboardTeams()]),
	}
}
