import {
	eachMonthOfInterval,
	format,
	isMonday,
	isValid,
	isWeekend,
	parseISO,
	subDays,
} from 'date-fns'
import { nb } from 'date-fns/locale'
import {
	type DashboardBestillingerDTO,
	type DashboardDollyTeamsDTO,
	type DashboardOrganisasjonerDTO,
	type DashboardOversiktDTO,
	type DashboardTeamsDTO,
} from '@/utils/hooks/useDashboard'
import { MONTH_NAMES, type MonthName, tilOversiktInterval } from './dashboardFeilUtils'

export type PersonTrendPoint = {
	dato: string
	datoVisning: string
	nyeBestillinger: number
	gjenopprettedeBestillinger: number
	nyePersoner: number
	dollyPersoner: number
	testnorgePersoner: number
}

export type MonthlyTeamPoint = {
	interval: string
	intervalVisning: string
	totaltUnikeBrukere: number
	totaltAntallTeams: number
	teams: {
		team: string
		unikeBrukere: number
		beskrivelse?: string
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
	beskrivelse?: string
}

export type MonthlyPersonPoint = {
	interval: string
	intervalVisning: string
	totalt: number
	nye: number
	gjenopprettede: number
}

export const MONTH_SCOPE_LAST_12 = 'LAST_12'
export const MONTH_SCOPE_ALL = 'ALL'

export const QUICK_RANGE_OPTIONS = [
	{ value: 'week', label: 'Siste uke' },
	{ value: 'month', label: 'Siste måned' },
	{ value: 'threeMonths', label: 'Siste 3 måneder' },
	{ value: 'sixMonths', label: 'Siste 6 måneder' },
	{ value: 'year', label: 'Siste år' },
	{ value: 'all', label: 'All historikk' },
] as const

export type QuickRangeValue = (typeof QUICK_RANGE_OPTIONS)[number]['value']

export const isQuickRangeValue = (value: string): value is QuickRangeValue =>
	QUICK_RANGE_OPTIONS.some((option) => option.value === value)

export const monthsInRange = (
	fraDato: string,
	tilDato: string,
): { year: number; month: MonthName }[] => {
	if (!fraDato || !tilDato) {
		return []
	}
	const start = parseISO(fraDato)
	const end = parseISO(tilDato)
	if (!isValid(start) || !isValid(end) || start > end) {
		return []
	}
	return eachMonthOfInterval({ start, end }).map((monthDate) => ({
		year: monthDate.getFullYear(),
		month: MONTH_NAMES[monthDate.getMonth()],
	}))
}

export const asNumber = (value: number | null | undefined) =>
	typeof value === 'number' ? value : 0

export const toDisplayDate = (dato: string) => {
	try {
		return format(parseISO(dato), 'dd.MM.yyyy')
	} catch {
		return dato
	}
}

export type PreviousBusinessPeriod = {
	dates: string[]
	label: string
	title: string
	buttonLabel: string
}

export const getPreviousBusinessPeriod = (referenceDate = new Date()): PreviousBusinessPeriod => {
	const friday = format(subDays(referenceDate, 1), 'yyyy-MM-dd')

	if (isMonday(referenceDate)) {
		const mondayFriday = format(subDays(referenceDate, 3), 'yyyy-MM-dd')
		const mondaySaturday = format(subDays(referenceDate, 2), 'yyyy-MM-dd')
		const mondaySunday = format(subDays(referenceDate, 1), 'yyyy-MM-dd')
		return {
			dates: [mondayFriday, mondaySaturday, mondaySunday],
			label: `${toDisplayDate(mondayFriday)}–${toDisplayDate(mondaySunday)}`,
			title: 'Siste hverdag + helg',
			buttonLabel: 'Siste hverdag + helg',
		}
	}

	if (isWeekend(referenceDate)) {
		const fridayOffset = referenceDate.getDay() === 6 ? 1 : 2
		const previousFriday = format(subDays(referenceDate, fridayOffset), 'yyyy-MM-dd')
		return {
			dates: [previousFriday],
			label: toDisplayDate(previousFriday),
			title: 'Siste hverdag',
			buttonLabel: 'Siste hverdag',
		}
	}

	return {
		dates: [friday],
		label: toDisplayDate(friday),
		title: 'Siste hverdag',
		buttonLabel: 'Siste hverdag',
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

export const fillMissingPersonDays = (
	personer: DashboardBestillingerDTO[],
	fraDato: string,
	tilDato: string,
): DashboardBestillingerDTO[] => {
	if (!fraDato || !tilDato) {
		return [...personer].sort((a, b) => a.dato.localeCompare(b.dato))
	}

	const startDato = fraDato <= tilDato ? fraDato : tilDato
	const endDato = fraDato <= tilDato ? tilDato : fraDato
	const startDate = new Date(`${startDato}T00:00:00`)
	const endDate = new Date(`${endDato}T00:00:00`)

	if (Number.isNaN(startDate.getTime()) || Number.isNaN(endDate.getTime())) {
		return [...personer].sort((a, b) => a.dato.localeCompare(b.dato))
	}

	const personByDate = new Map(personer.map((personData) => [personData.dato, personData]))
	const completedDays: DashboardBestillingerDTO[] = []
	let currentDate = startDate

	while (currentDate <= endDate) {
		const currentDateValue = format(currentDate, 'yyyy-MM-dd')
		const personDataForDay = personByDate.get(currentDateValue)
		completedDays.push(
			personDataForDay ?? {
				dato: currentDateValue,
				antallNyeBestillinger: 0,
				antallGjenopprettedeBestillinger: 0,
				antallNyePersoner: 0,
				andelOpprettedeDollyPersoner: 0,
				andelImporterteTestnorgePersoner: 0,
			},
		)
		currentDate = new Date(currentDate)
		currentDate.setDate(currentDate.getDate() + 1)
	}

	return completedDays
}

const toMonthDisplayDate = (interval: string) => {
	try {
		return format(parseISO(`${interval}-01`), 'MMM yyyy', { locale: nb })
	} catch {
		return interval
	}
}

const normalizeInterval = (interval?: string, dato?: string) => {
	if (interval && /^\d{4}-\d{2}(-\d{2})?$/.test(interval)) {
		return interval.slice(0, 7)
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

const toOrganisasjonBeskrivelse = (
	organisasjonsform?: string,
	organisasjonsnummer?: string,
): string | undefined => {
	const deler = [
		organisasjonsform?.trim(),
		organisasjonsnummer?.trim() ? `org.nr. ${organisasjonsnummer.trim()}` : undefined,
	].filter(Boolean)
	return deler.length > 0 ? deler.join(', ') : undefined
}

export const toMonthlyOrganisasjonPoints = (
	dashboardOrganisasjoner: DashboardOrganisasjonerDTO[],
): MonthlyTeamPoint[] =>
	dashboardOrganisasjoner
		.map((row) => {
			const organisasjoner = (row.organisasjoner ?? []).filter((entry) => entry.navn)
			const antallPerNavn = new Map<string, number>()
			organisasjoner.forEach((entry) =>
				antallPerNavn.set(entry.navn, (antallPerNavn.get(entry.navn) ?? 0) + 1),
			)
			return {
				interval: row.interval,
				intervalVisning: toMonthDisplayDate(row.interval),
				totaltUnikeBrukere: asNumber(row.totaltUnikeBrukere),
				totaltAntallTeams: asNumber(row.totaltAntallOrganisasjoner),
				teams: organisasjoner
					.map((entry) => ({
						team:
							(antallPerNavn.get(entry.navn) ?? 0) > 1 && entry.organisasjonsnummer
								? `${entry.navn} (${entry.organisasjonsnummer})`
								: entry.navn,
						unikeBrukere: asNumber(entry.unikeBrukere),
						beskrivelse: toOrganisasjonBeskrivelse(
							entry.organisasjonsform,
							entry.organisasjonsnummer,
						),
					}))
					.sort((a, b) => b.unikeBrukere - a.unikeBrukere),
			}
		})
		.sort((a, b) => a.interval.localeCompare(b.interval))

export const toMonthlyDollyTeamPoints = (
	dashboardDollyTeams: DashboardDollyTeamsDTO[],
): MonthlyTeamPoint[] =>
	dashboardDollyTeams
		.map((row) => ({
			interval: row.interval,
			intervalVisning: toMonthDisplayDate(row.interval),
			totaltUnikeBrukere: asNumber(row.totaltAntallMedlemmer),
			totaltAntallTeams: asNumber(row.totaltAntallTeams),
			teams: (row.teams ?? [])
				.map((entry) => ({
					team: entry.navn,
					unikeBrukere: asNumber(entry.antallMedlemmer),
					beskrivelse: entry.beskrivelse?.trim() || undefined,
				}))
				.filter((entry) => entry.team)
				.sort((a, b) => b.unikeBrukere - a.unikeBrukere),
		}))
		.sort((a, b) => a.interval.localeCompare(b.interval))

export const toMonthlyPersonPoints = (oversikt: DashboardOversiktDTO[]): MonthlyPersonPoint[] =>
	oversikt
		.map((rad) => {
			const interval = tilOversiktInterval(rad)
			if (!interval) {
				return null
			}
			const nye = asNumber(rad.nye)
			const gjenopprettede = asNumber(rad.gjenopprettede)
			return {
				interval,
				intervalVisning: toMonthDisplayDate(interval),
				totalt: asNumber(rad.totaltAntallPersoner ?? nye + gjenopprettede),
				nye,
				gjenopprettede,
			}
		})
		.filter((point): point is MonthlyPersonPoint => point !== null)
		.sort((a, b) => a.interval.localeCompare(b.interval))

export const toPersonTrendData = (personer: DashboardBestillingerDTO[]): PersonTrendPoint[] =>
	personer.map((personData) => ({
		dato: personData.dato,
		datoVisning: toDisplayDate(personData.dato),
		nyeBestillinger: asNumber(personData.antallNyeBestillinger),
		gjenopprettedeBestillinger: asNumber(personData.antallGjenopprettedeBestillinger),
		nyePersoner: asNumber(personData.antallNyePersoner),
		dollyPersoner: asNumber(personData.andelOpprettedeDollyPersoner),
		testnorgePersoner: asNumber(personData.andelImporterteTestnorgePersoner),
	}))

export const toMonthlyTrendData = (points: MonthlyTeamPoint[]): MonthlyTrendPoint[] =>
	points.map((point) => ({
		interval: point.interval,
		intervalVisning: point.intervalVisning,
		totaltUnikeBrukere: point.totaltUnikeBrukere,
		totaltAntallTeams: point.totaltAntallTeams,
	}))

export const filterMonthlyTeamPoints = <T extends { interval: string }>(
	points: T[],
	scope: typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL,
): T[] => {
	const sortedPoints = [...points].sort((a, b) => a.interval.localeCompare(b.interval))
	return scope === MONTH_SCOPE_ALL ? sortedPoints : sortedPoints.slice(-12)
}

export const toMonthlyIntervalOptions = (points: MonthlyTeamPoint[]) =>
	[...points]
		.sort((a, b) => a.interval.localeCompare(b.interval))
		.map((point) => ({ value: point.interval, label: point.intervalVisning }))

export const toTeamDistributionForInterval = (
	points: MonthlyTeamPoint[],
	interval: string,
): TeamDistributionPoint[] =>
	(points.find((point) => point.interval === interval)?.teams ?? []).map((teamData) => ({
		team: teamData.team,
		unikeBrukere: teamData.unikeBrukere,
		beskrivelse: teamData.beskrivelse,
	}))

export type MonthlyDistributionView = {
	intervalOptions: { value: string; label: string }[]
	yearOptions: string[]
	selectedYear: string
	monthOptions: { value: string; label: string }[]
	selectedPoint: MonthlyTeamPoint | null
	distribution: TeamDistributionPoint[]
}

export const buildMonthlyDistributionView = (
	monthlyPoints: MonthlyTeamPoint[],
	selectedInterval: string,
): MonthlyDistributionView => {
	const intervalOptions = toMonthlyIntervalOptions(monthlyPoints)
	const yearOptions = [...new Set(intervalOptions.map((option) => option.value.slice(0, 4)))].sort(
		(a, b) => a.localeCompare(b),
	)
	const selectedIntervalValue =
		typeof selectedInterval === 'string'
			? selectedInterval
			: selectedInterval && typeof (selectedInterval as { value?: unknown }).value === 'string'
				? (selectedInterval as { value: string }).value
				: ''
	const selectedYear = selectedIntervalValue.slice(0, 4)
	const monthOptions = intervalOptions
		.filter((option) => option.value.startsWith(`${selectedYear}-`))
		.map((option) => ({ ...option, label: option.label.replace(/\s+\d{4}$/, '') }))
	const selectedPoint =
		monthlyPoints.find((point) => point.interval === selectedIntervalValue) ?? null
	const distribution = toTeamDistributionForInterval(monthlyPoints, selectedIntervalValue)
	return { intervalOptions, yearOptions, selectedYear, monthOptions, selectedPoint, distribution }
}

export const makeYearChangeHandler =
	(intervalOptions: { value: string }[], setSelectedInterval: (value: string) => void) =>
	(year: string) => {
		const lastOption = intervalOptions.findLast((option) => option.value.startsWith(`${year}-`))
		if (lastOption) {
			setSelectedInterval(lastOption.value)
		}
	}
