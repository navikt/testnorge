import { useEffect, useState } from 'react'
import { format, isValid, parseISO, subDays, subYears } from 'date-fns'
import { useDashboard } from '@/utils/hooks/useDashboard'
import {
	asNumber,
	fillMissingPersonDays,
	filterMonthlyTeamPoints,
	getPreviousBusinessPeriod,
	MONTH_SCOPE_ALL,
	MONTH_SCOPE_LAST_12,
	toDisplayDate,
	toMonthlyDollyTeamPoints,
	toMonthlyIntervalOptions,
	toMonthlyOrganisasjonPoints,
	toMonthlyTeamPoints,
	toMonthlyTrendData,
	toPersonTrendData,
	toTeamDistributionForInterval,
	withinDateRange,
} from './dashboardUtils'
import {
	createMonthlyTeamDistributionChartOptions,
	createMonthlyTeamTrendChartOptions,
	createPersonTrendChartOptions,
	createPreviousDayChartOptions,
	createPreviousDayErrorBreakdownChartOptions,
} from './dashboardChartOptions'
import { createDashboardMockData } from './dashboardMockData'

export const QUICK_RANGE_OPTIONS = [
	{ value: 'week', label: 'Siste uke' },
	{ value: 'month', label: 'Siste måned' },
	{ value: 'threeMonths', label: 'Siste 3 måneder' },
	{ value: 'sixMonths', label: 'Siste 6 måneder' },
	{ value: 'year', label: 'Siste år' },
] as const

type QuickRangeValue = (typeof QUICK_RANGE_OPTIONS)[number]['value']

const isQuickRangeValue = (value: string): value is QuickRangeValue =>
	QUICK_RANGE_OPTIONS.some((option) => option.value === value)

const DAY_SCOPE_YESTERDAY = 'YESTERDAY'
const DAY_SCOPE_TODAY = 'TODAY'

export const useDashboardData = () => {
	const {
		dashboardPersoner,
		dashboardTeams,
		dashboardOrganisasjoner,
		dashboardDollyTeams,
		loadingDashboardPersoner,
		loadingDashboardTeams,
		loadingDashboardOrganisasjoner,
		loadingDashboardDollyTeams,
		dashboardPersonerError,
		dashboardTeamsError,
		dashboardOrganisasjonerError,
		dashboardDollyTeamsError,
		reloadDashboard,
	} = useDashboard()

	const [fraDato, setFraDato] = useState(() => format(subDays(new Date(), 6), 'yyyy-MM-dd'))
	const [tilDato, setTilDato] = useState(() => format(new Date(), 'yyyy-MM-dd'))
	const [selectedQuickRange, setSelectedQuickRange] = useState<QuickRangeValue | null>('week')
	const [monthScope, setMonthScope] = useState<typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL>(
		MONTH_SCOPE_LAST_12,
	)
	const [organisasjonMonthScope, setOrganisasjonMonthScope] = useState<
		typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL
	>(MONTH_SCOPE_LAST_12)
	const [selectedInterval, setSelectedInterval] = useState('')
	const [selectedOrganisasjonInterval, setSelectedOrganisasjonInterval] = useState('')
	const [selectedDollyTeamsInterval, setSelectedDollyTeamsInterval] = useState('')
	const [selectedDayScope, setSelectedDayScope] = useState<
		typeof DAY_SCOPE_YESTERDAY | typeof DAY_SCOPE_TODAY
	>(DAY_SCOPE_YESTERDAY)
	const [mockModeEnabled, setMockModeEnabled] = useState(false)
	const [mockData, setMockData] = useState(() => createDashboardMockData(0))

	const activeDashboardPersoner = mockModeEnabled ? mockData.dashboardPersoner : dashboardPersoner
	const activeDashboardTeams = mockModeEnabled ? mockData.dashboardTeams : dashboardTeams
	const activeDashboardOrganisasjoner = mockModeEnabled
		? mockData.dashboardOrganisasjoner
		: dashboardOrganisasjoner
	const activeDashboardDollyTeams = mockModeEnabled
		? mockData.dashboardDollyTeams
		: dashboardDollyTeams

	const latestAvailableDateValue = activeDashboardPersoner
		.map((personData) => personData.dato)
		.filter((dateValue): dateValue is string => Boolean(dateValue))
		.sort((a, b) => a.localeCompare(b))
		.at(-1)
	const latestAvailableDate = latestAvailableDateValue
		? parseISO(latestAvailableDateValue)
		: undefined
	const quickRangeAnchorDate =
		latestAvailableDate && isValid(latestAvailableDate) ? latestAvailableDate : new Date()

	const todayDate = format(new Date(), 'yyyy-MM-dd')
	const previousBusinessPeriod = getPreviousBusinessPeriod(new Date())
	const selectedDayDates =
		selectedDayScope === DAY_SCOPE_TODAY ? [todayDate] : previousBusinessPeriod.dates
	const previousDayPeriodData = activeDashboardPersoner
		.filter((personData) => selectedDayDates.includes(personData.dato))
		.sort((a, b) => a.dato.localeCompare(b.dato))
	const selectedDayDisplayLabel =
		selectedDayScope === DAY_SCOPE_TODAY ? toDisplayDate(todayDate) : previousBusinessPeriod.label
	const selectedDayPeriodTitle =
		selectedDayScope === DAY_SCOPE_TODAY ? 'I dag' : previousBusinessPeriod.title
	const selectedDayButtonLabel = previousBusinessPeriod.buttonLabel

	const previousDaySummary = previousDayPeriodData.reduce(
		(acc, personData) => {
			acc.nye += asNumber(personData.nye)
			acc.gjenopprettede += asNumber(personData.gjenopprettede)
			acc.pdlFeil += asNumber(personData.pdlFeil)
			acc.andreFeil += asNumber(personData.andreFeil)
			return acc
		},
		{
			nye: 0,
			gjenopprettede: 0,
			pdlFeil: 0,
			andreFeil: 0,
			nyeInklGjenopprettede: 0,
			totaltFeil: 0,
		},
	)
	const previousDaySummaryWithTotals = {
		...previousDaySummary,
		nyeInklGjenopprettede: previousDaySummary.nye + previousDaySummary.gjenopprettede,
		totaltFeil: previousDaySummary.pdlFeil + previousDaySummary.andreFeil,
	}

	const previousDayChartOptions = createPreviousDayChartOptions({
		nye: previousDaySummaryWithTotals.nye,
		gjenopprettede: previousDaySummaryWithTotals.gjenopprettede,
	})
	const previousDayErrorBreakdownChartOptions = createPreviousDayErrorBreakdownChartOptions(
		previousDaySummaryWithTotals.pdlFeil,
		previousDaySummaryWithTotals.andreFeil,
	)

	const filteredPersoner = activeDashboardPersoner
		.filter((personData) => withinDateRange(personData.dato, fraDato, tilDato))
		.sort((a, b) => a.dato.localeCompare(b.dato))
	const completeFilteredPersoner = fillMissingPersonDays(filteredPersoner, fraDato, tilDato)

	const summary = completeFilteredPersoner.reduce(
		(acc, personData) => {
			acc.personerTotalt += asNumber(personData.personerTotalt)
			acc.nye += asNumber(personData.nye)
			acc.gjenopprettede += asNumber(personData.gjenopprettede)
			acc.pdlFeil += asNumber(personData.pdlFeil)
			acc.andreFeil += asNumber(personData.andreFeil)
			return acc
		},
		{ personerTotalt: 0, nye: 0, gjenopprettede: 0, pdlFeil: 0, andreFeil: 0 },
	)

	const personTrendData = toPersonTrendData(completeFilteredPersoner)
	const personTrendChartOptions = createPersonTrendChartOptions(personTrendData)

	// --- Organisasjoner ---
	const organisasjonMonthlyPoints = toMonthlyOrganisasjonPoints(activeDashboardOrganisasjoner)
	const filteredOrganisasjonPoints = filterMonthlyTeamPoints(
		organisasjonMonthlyPoints,
		organisasjonMonthScope,
	)
	const organisasjonIntervalOptions = toMonthlyIntervalOptions(filteredOrganisasjonPoints)
	const organisasjonYearOptions = [
		...new Set(organisasjonIntervalOptions.map((option) => option.value.slice(0, 4))),
	].sort((a, b) => b.localeCompare(a))
	const selectedOrganisasjonYear = selectedOrganisasjonInterval.slice(0, 4)
	const organisasjonMonthOptions = organisasjonIntervalOptions
		.filter((option) => option.value.startsWith(`${selectedOrganisasjonYear}-`))
		.map((option) => ({ ...option, label: option.label.replace(/\s+\d{4}$/, '') }))
	const selectedOrganisasjonPoint =
		filteredOrganisasjonPoints.find((point) => point.interval === selectedOrganisasjonInterval) ??
		null
	const organisasjonDistribution = toTeamDistributionForInterval(
		filteredOrganisasjonPoints,
		selectedOrganisasjonInterval,
	)
	const organisasjonMonthlyTrendChartOptions = createMonthlyTeamTrendChartOptions(
		toMonthlyTrendData(filteredOrganisasjonPoints),
		{
			description:
				'Linjediagram med månedlig utvikling i unike brukere og antall organisasjoner fra organisasjonsendepunktet.',
			secondSeriesName: 'Antall organisasjoner',
		},
	)
	const organisasjonDistributionChartOptions =
		createMonthlyTeamDistributionChartOptions(organisasjonDistribution)

	useEffect(() => {
		if (organisasjonIntervalOptions.length === 0) {
			setSelectedOrganisasjonInterval('')
			return
		}
		const selectedExists = organisasjonIntervalOptions.some(
			(option) => option.value === selectedOrganisasjonInterval,
		)
		if (!selectedExists) {
			setSelectedOrganisasjonInterval(organisasjonIntervalOptions[0].value)
		}
	}, [organisasjonIntervalOptions, selectedOrganisasjonInterval])

	// --- Dolly Teams ---
	const dollyTeamsMonthlyPoints = toMonthlyDollyTeamPoints(activeDashboardDollyTeams)
	const filteredDollyTeamsPoints = [...dollyTeamsMonthlyPoints]
	const dollyTeamsIntervalOptions = toMonthlyIntervalOptions(filteredDollyTeamsPoints)
	const dollyTeamsYearOptions = [
		...new Set(dollyTeamsIntervalOptions.map((option) => option.value.slice(0, 4))),
	].sort((a, b) => b.localeCompare(a))
	const selectedDollyTeamsYear = selectedDollyTeamsInterval.slice(0, 4)
	const dollyTeamsMonthOptions = dollyTeamsIntervalOptions
		.filter((option) => option.value.startsWith(`${selectedDollyTeamsYear}-`))
		.map((option) => ({ ...option, label: option.label.replace(/\s+\d{4}$/, '') }))
	const selectedDollyTeamsPoint =
		filteredDollyTeamsPoints.find((point) => point.interval === selectedDollyTeamsInterval) ?? null
	const dollyTeamsDistribution = toTeamDistributionForInterval(
		filteredDollyTeamsPoints,
		selectedDollyTeamsInterval,
	)
	const dollyTeamsMonthlyTrendChartOptions = createMonthlyTeamTrendChartOptions(
		toMonthlyTrendData(filteredDollyTeamsPoints),
		{
			description:
				'Linjediagram med månedlig utvikling i unike brukere og antall dolly teams fra dollyteams-endepunktet.',
			secondSeriesName: 'Antall teams',
		},
	)
	const dollyTeamsDistributionChartOptions =
		createMonthlyTeamDistributionChartOptions(dollyTeamsDistribution)

	useEffect(() => {
		if (dollyTeamsIntervalOptions.length === 0) {
			setSelectedDollyTeamsInterval('')
			return
		}
		const selectedExists = dollyTeamsIntervalOptions.some(
			(option) => option.value === selectedDollyTeamsInterval,
		)
		if (!selectedExists) {
			setSelectedDollyTeamsInterval(dollyTeamsIntervalOptions[0].value)
		}
	}, [dollyTeamsIntervalOptions, selectedDollyTeamsInterval])

	// --- Teams ---
	const monthlyTeamPoints = toMonthlyTeamPoints(activeDashboardTeams)
	const filteredMonthlyTeamPoints = filterMonthlyTeamPoints(monthlyTeamPoints, monthScope)
	const monthlyIntervalOptions = toMonthlyIntervalOptions(filteredMonthlyTeamPoints)
	const teamYearOptions = [
		...new Set(monthlyIntervalOptions.map((option) => option.value.slice(0, 4))),
	].sort((a, b) => b.localeCompare(a))
	const selectedTeamYear = selectedInterval.slice(0, 4)
	const teamMonthOptions = monthlyIntervalOptions
		.filter((option) => option.value.startsWith(`${selectedTeamYear}-`))
		.map((option) => ({ ...option, label: option.label.replace(/\s+\d{4}$/, '') }))
	const selectedMonthlyPoint =
		filteredMonthlyTeamPoints.find((point) => point.interval === selectedInterval) ?? null
	const teamDistribution = toTeamDistributionForInterval(
		filteredMonthlyTeamPoints,
		selectedInterval,
	)
	const monthlyTrendChartOptions = createMonthlyTeamTrendChartOptions(
		toMonthlyTrendData(filteredMonthlyTeamPoints),
	)
	const monthlyDistributionChartOptions =
		createMonthlyTeamDistributionChartOptions(teamDistribution)

	useEffect(() => {
		if (monthlyIntervalOptions.length === 0) {
			setSelectedInterval('')
			return
		}
		const selectedExists = monthlyIntervalOptions.some(
			(option) => option.value === selectedInterval,
		)
		if (!selectedExists) {
			setSelectedInterval(monthlyIntervalOptions[0].value)
		}
	}, [monthlyIntervalOptions, selectedInterval])

	const applyQuickRange = (quickRangeValue: string) => {
		const toDateValue = format(quickRangeAnchorDate, 'yyyy-MM-dd')
		const fromDateValue =
			quickRangeValue === 'week'
				? format(subDays(quickRangeAnchorDate, 6), 'yyyy-MM-dd')
				: quickRangeValue === 'month'
					? format(subDays(quickRangeAnchorDate, 29), 'yyyy-MM-dd')
					: quickRangeValue === 'threeMonths'
						? format(subDays(quickRangeAnchorDate, 89), 'yyyy-MM-dd')
						: quickRangeValue === 'sixMonths'
							? format(subDays(quickRangeAnchorDate, 179), 'yyyy-MM-dd')
							: format(subYears(quickRangeAnchorDate, 1), 'yyyy-MM-dd')
		setFraDato(fromDateValue)
		setTilDato(toDateValue)
		if (isQuickRangeValue(quickRangeValue)) {
			setSelectedQuickRange(quickRangeValue)
		}
	}

	const handleRefresh = () => {
		if (mockModeEnabled) {
			setMockData(createDashboardMockData(Date.now()))
			return
		}
		reloadDashboard()
	}

	return {
		// Mock mode
		mockModeEnabled,
		onSeedMockData: () => {
			setMockData(createDashboardMockData(Date.now()))
			setMockModeEnabled(true)
		},
		onShowRealData: () => setMockModeEnabled(false),

		// Loading & errors
		isAnyLoading:
			(loadingDashboardPersoner ||
				loadingDashboardTeams ||
				loadingDashboardOrganisasjoner ||
				loadingDashboardDollyTeams) &&
			!mockModeEnabled,
		loadingDashboardPersoner,
		dashboardPersonerError,
		dashboardTeamsError,
		dashboardOrganisasjonerError,
		dashboardDollyTeamsError,

		// Previous day section
		selectedDayDisplayLabel,
		selectedDayPeriodTitle,
		selectedDayButtonLabel,
		selectedDayScope,
		onSelectedDayScopeChange: setSelectedDayScope,
		previousDayPeriodData,
		previousDaySummary: previousDaySummaryWithTotals,
		previousDayChartOptions,
		previousDayErrorBreakdownChartOptions,

		// Person analysis section
		selectedQuickRange,
		fraDato,
		tilDato,
		todayDate,
		onFraDatoChange: (value: string) => {
			setFraDato(value)
			setSelectedQuickRange(null)
		},
		onTilDatoChange: (value: string) => {
			setTilDato(value)
			setSelectedQuickRange(null)
		},
		onQuickRangeClick: applyQuickRange,
		onRefresh: handleRefresh,
		filteredPersonerLength: completeFilteredPersoner.length,
		summary,
		personTrendDataLength: personTrendData.length,
		personTrendChartOptions,

		// Organisasjoner section
		filteredOrganisasjonPointsLength: filteredOrganisasjonPoints.length,
		organisasjonMonthScope,
		onOrganisasjonMonthScopeChange: setOrganisasjonMonthScope,
		organisasjonMonthlyTrendChartOptions,
		organisasjonYearOptions,
		selectedOrganisasjonYear,
		organisasjonMonthOptions,
		onSelectedOrganisasjonYearChange: (year: string) => {
			const firstOption = organisasjonIntervalOptions.find((option) =>
				option.value.startsWith(`${year}-`),
			)
			if (firstOption) setSelectedOrganisasjonInterval(firstOption.value)
		},
		selectedOrganisasjonInterval,
		onSelectedOrganisasjonIntervalChange: setSelectedOrganisasjonInterval,
		selectedOrganisasjonPoint,
		organisasjonDistribution,
		organisasjonDistributionChartOptions,

		// Dolly teams section
		filteredDollyTeamsPointsLength: filteredDollyTeamsPoints.length,
		dollyTeamsMonthlyTrendChartOptions,
		dollyTeamsYearOptions,
		selectedDollyTeamsYear,
		dollyTeamsMonthOptions,
		onSelectedDollyTeamsYearChange: (year: string) => {
			const firstOption = dollyTeamsIntervalOptions.find((option) =>
				option.value.startsWith(`${year}-`),
			)
			if (firstOption) setSelectedDollyTeamsInterval(firstOption.value)
		},
		selectedDollyTeamsInterval,
		onSelectedDollyTeamsIntervalChange: setSelectedDollyTeamsInterval,
		selectedDollyTeamsPoint,
		dollyTeamsDistribution,
		dollyTeamsDistributionChartOptions,

		// Teams section
		filteredMonthlyTeamPointsLength: filteredMonthlyTeamPoints.length,
		monthScope,
		onMonthScopeChange: setMonthScope,
		monthlyTrendChartOptions,
		teamYearOptions,
		selectedTeamYear,
		teamMonthOptions,
		onSelectedTeamYearChange: (year: string) => {
			const firstOption = monthlyIntervalOptions.find((option) =>
				option.value.startsWith(`${year}-`),
			)
			if (firstOption) setSelectedInterval(firstOption.value)
		},
		selectedTeamInterval: selectedInterval,
		onSelectedTeamIntervalChange: setSelectedInterval,
		selectedMonthlyPoint,
		teamDistribution,
		monthlyDistributionChartOptions,
	}
}
