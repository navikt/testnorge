import { format, parseISO, subDays, subMonths } from 'date-fns'
import { nb } from 'date-fns/locale'
import { DashboardPersonerDTO, DashboardTeamsDTO } from '@/utils/hooks/useDashboard'

export type PersonTrendPoint = {
	dato: string
	datoVisning: string
	personerTotalt: number
	nye: number
	gjenopprettede: number
	pdlFeil: number
	andreFeil: number
}

export type MonthlyTeamPoint = {
	interval: string
	intervalVisning: string
	totaltUnikeBrukere: number
	totaltAntallTeams: number
	teams: {
		team: string
		unikeBrukere: number
	}[]
}

export type MonthlyTrendPoint = {
	interval: string
	intervalVisning: string
	totaltUnikeBrukere: number
	totaltAntallTeams: number
}

export type TeamDistributionPoint = {
	team: string
	unikeBrukere: number
}

export const MONTH_SCOPE_LAST_12 = 'LAST_12'
export const MONTH_SCOPE_ALL = 'ALL'

export const asNumber = (value: number | null | undefined) =>
	typeof value === 'number' ? value : 0

export const toDisplayDate = (dato: string) => {
	try {
		return format(parseISO(dato), 'dd.MM.yyyy')
	} catch {
		return dato
	}
}

export const withinDateRange = (dato: string, fraDato: string, tilDato: string) => {
	if (!fraDato && !tilDato) {
		return true
	}

	if (!fraDato) {
		return dato <= tilDato
	}

	if (!tilDato) {
		return dato >= fraDato
	}

	const startDato = fraDato <= tilDato ? fraDato : tilDato
	const endDato = fraDato <= tilDato ? tilDato : fraDato
	return dato >= startDato && dato <= endDato
}

const toMonthDisplayDate = (interval: string) => {
	try {
		return format(parseISO(`${interval}-01`), 'MMM yyyy', { locale: nb })
	} catch {
		return interval
	}
}

const normalizeInterval = (interval?: string, dato?: string) => {
	if (interval && /^\d{4}-\d{2}$/.test(interval)) {
		return interval
	}

	if (dato && /^\d{4}-\d{2}-\d{2}$/.test(dato)) {
		return dato.slice(0, 7)
	}

	return null
}

const toTeamsFromLegacyEntries = (dashboardTeam: DashboardTeamsDTO) => {
	const teamMap = new Map<string, number>()

	;(dashboardTeam.entries ?? []).forEach((entry) => {
		const teamNames = (entry.teams ?? []).filter((teamName) => teamName)
		teamNames.forEach((teamName) => {
			const previousValue = teamMap.get(teamName) ?? 0
			teamMap.set(teamName, previousValue + asNumber(entry.antall))
		})
	})

	return Array.from(teamMap.entries()).map(([team, unikeBrukere]) => ({ team, unikeBrukere }))
}

export const toMonthlyTeamPoints = (dashboardTeams: DashboardTeamsDTO[]): MonthlyTeamPoint[] =>
	dashboardTeams
		.map((teamData) => {
			const interval = normalizeInterval(teamData.interval, teamData.dato)
			if (!interval) {
				return null
			}

			const teamsFromResponse =
				teamData.teams?.map((teamDataPoint) => ({
					team: teamDataPoint.team,
					unikeBrukere: asNumber(teamDataPoint.unikeBrukere),
				})) ?? []

			const teams =
				teamsFromResponse.length > 0 ? teamsFromResponse : toTeamsFromLegacyEntries(teamData)
			const totaltUnikeBrukere =
				teamData.totaltUnikeBrukere ?? teams.reduce((acc, team) => acc + team.unikeBrukere, 0)
			const totaltAntallTeams =
				teamData.totaltAntallTeams ?? new Set(teams.map((team) => team.team)).size

			return {
				interval,
				intervalVisning: toMonthDisplayDate(interval),
				totaltUnikeBrukere: asNumber(totaltUnikeBrukere),
				totaltAntallTeams: asNumber(totaltAntallTeams),
				teams: teams
					.filter((teamDataPoint) => teamDataPoint.team)
					.sort((a, b) => b.unikeBrukere - a.unikeBrukere),
			}
		})
		.filter((point): point is MonthlyTeamPoint => Boolean(point))
		.sort((a, b) => a.interval.localeCompare(b.interval))

export const toPersonTrendData = (personer: DashboardPersonerDTO[]): PersonTrendPoint[] =>
	personer.map((personData) => ({
		dato: personData.dato,
		datoVisning: toDisplayDate(personData.dato),
		personerTotalt: asNumber(personData.personerTotalt),
		nye: asNumber(personData.nye),
		gjenopprettede: asNumber(personData.gjenopprettede),
		pdlFeil: asNumber(personData.pdlFeil),
		andreFeil: asNumber(personData.andreFeil),
	}))

export const toMonthlyTrendData = (points: MonthlyTeamPoint[]): MonthlyTrendPoint[] =>
	points.map((point) => ({
		interval: point.interval,
		intervalVisning: point.intervalVisning,
		totaltUnikeBrukere: point.totaltUnikeBrukere,
		totaltAntallTeams: point.totaltAntallTeams,
	}))

export const filterMonthlyTeamPoints = (
	points: MonthlyTeamPoint[],
	scope: typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL,
) => (scope === MONTH_SCOPE_ALL ? points : points.slice(-12))

export const toMonthlyIntervalOptions = (points: MonthlyTeamPoint[]) =>
	[...points]
		.sort((a, b) => b.interval.localeCompare(a.interval))
		.map((point) => ({ value: point.interval, label: point.intervalVisning }))

export const toTeamDistributionForInterval = (
	points: MonthlyTeamPoint[],
	interval: string,
): TeamDistributionPoint[] =>
	(points.find((point) => point.interval === interval)?.teams ?? []).map((teamData) => ({
		team: teamData.team,
		unikeBrukere: teamData.unikeBrukere,
	}))

export const createDashboardMockData = (
	_cycle: number,
): {
	dashboardPersoner: DashboardPersonerDTO[]
	dashboardTeams: DashboardTeamsDTO[]
} => {
	const dashboardPersoner = Array.from({ length: 300 }, (_, index) => {
		const dayOffset = 89 - index
		const dato = format(subDays(new Date(), dayOffset), 'yyyy-MM-dd')
		const nye = Math.floor(Math.random() * 60) + 10
		const gjenopprettede = Math.floor(Math.random() * 40) + 5
		const pdlFeil = Math.floor(Math.random() * 41)
		const andreFeil = Math.floor(Math.random() * 41)
		const personerTotalt = nye + gjenopprettede

		return {
			dato,
			personerTotalt,
			nye,
			gjenopprettede,
			pdlFeil,
			andreFeil,
		}
	})

	const teamNames = [
		'Team Dolly',
		'Team Schmetchy',
		'Team Aurora',
		'Team Hydra',
		'Team Orion',
		'Team Vega',
		'Team Atlas',
		'Team Nova',
	]

	const dashboardTeams = Array.from({ length: 24 }, (_, index) => {
		const monthOffset = 23 - index
		const interval = format(subMonths(new Date(), monthOffset), 'yyyy-MM')
		const activeTeamCount = Math.floor(Math.random() * 5) + 4

		const teams = Array.from({ length: activeTeamCount }, (_, teamIndex) => {
			const nameIndex = Math.floor(Math.random() * teamNames.length)
			return {
				team: teamNames[nameIndex],
				unikeBrukere: Math.floor(Math.random() * 45) + 6,
			}
		})

		const totaltUnikeBrukere = teams.reduce((sum, team) => sum + team.unikeBrukere, 0)

		return {
			interval,
			totaltUnikeBrukere,
			totaltAntallTeams: activeTeamCount,
			teams,
		}
	})

	return { dashboardPersoner, dashboardTeams }
}
