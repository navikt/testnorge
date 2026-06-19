import { useEffect, useState } from 'react'
import { format, isValid, parseISO, subDays, subYears } from 'date-fns'
import {
	useDashboard,
	useDashboardFeilDetaljert,
	useDashboardFeilForDager,
	useDashboardFeilSummert,
} from '@/utils/hooks/useDashboard'
import {
	asNumber,
	fillMissingPersonDays,
	filterMonthlyTeamPoints,
	getPreviousBusinessPeriod,
	MONTH_SCOPE_ALL,
	MONTH_SCOPE_LAST_12,
	type MonthlyTeamPoint,
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
	type FeilPeriodeOption,
	monthNumberToName,
	toFeilGrupper,
	toFeilPeriodeOptions,
	toFeilSummertView,
} from './dashboardFeilUtils'
import {
	createFeilSummertChartOptions,
	createMonthlyTeamDistributionChartOptions,
	createMonthlyTeamTrendChartOptions,
	createPersonTrendChartOptions,
	createPreviousDayChartOptions,
} from './dashboardChartOptions'
import { createDashboardMockData } from './dashboardMockData'

export const QUICK_RANGE_OPTIONS = [
	{ value: 'week', label: 'Siste uke' },
	{ value: 'month', label: 'Siste måned' },
	{ value: 'threeMonths', label: 'Siste 3 måneder' },
	{ value: 'sixMonths', label: 'Siste 6 måneder' },
	{ value: 'year', label: 'Siste år' },
	{ value: 'all', label: 'All historikk' },
] as const

type QuickRangeValue = (typeof QUICK_RANGE_OPTIONS)[number]['value']

const isQuickRangeValue = (value: string): value is QuickRangeValue =>
	QUICK_RANGE_OPTIONS.some((option) => option.value === value)

const DAY_SCOPE_YESTERDAY = 'YESTERDAY'
const DAY_SCOPE_TODAY = 'TODAY'

const buildMonthlyDistributionView = (
	monthlyPoints: MonthlyTeamPoint[],
	selectedInterval: string,
) => {
	const intervalOptions = toMonthlyIntervalOptions(monthlyPoints)
	const yearOptions = [...new Set(intervalOptions.map((option) => option.value.slice(0, 4)))].sort(
		(a, b) => a.localeCompare(b),
	)
	const selectedYear = selectedInterval.slice(0, 4)
	const monthOptions = intervalOptions
		.filter((option) => option.value.startsWith(`${selectedYear}-`))
		.map((option) => ({ ...option, label: option.label.replace(/\s+\d{4}$/, '') }))
	const selectedPoint = monthlyPoints.find((point) => point.interval === selectedInterval) ?? null
	const distribution = toTeamDistributionForInterval(monthlyPoints, selectedInterval)
	return { intervalOptions, yearOptions, selectedYear, monthOptions, selectedPoint, distribution }
}

const useAutoSelectLatestInterval = (
	intervalOptions: { value: string }[],
	selectedInterval: string,
	setSelectedInterval: (value: string) => void,
) => {
	useEffect(() => {
		if (intervalOptions.length === 0) {
			if (selectedInterval !== '') setSelectedInterval('')
			return
		}
		if (!intervalOptions.some((option) => option.value === selectedInterval)) {
			setSelectedInterval(intervalOptions.at(-1)!.value)
		}
	}, [intervalOptions, selectedInterval, setSelectedInterval])
}

const makeYearChangeHandler =
	(intervalOptions: { value: string }[], setSelectedInterval: (value: string) => void) =>
	(year: string) => {
		const lastOption = intervalOptions.findLast((option) => option.value.startsWith(`${year}-`))
		if (lastOption) setSelectedInterval(lastOption.value)
	}

const buildFeilPeriodView = (periodeOptions: FeilPeriodeOption[], selectedInterval: string) => {
	const intervalOptions = periodeOptions.map((option) => ({
		value: option.interval,
		label: option.intervalVisning,
	}))
	const yearOptions = [...new Set(intervalOptions.map((option) => option.value.slice(0, 4)))].sort(
		(a, b) => a.localeCompare(b),
	)
	const selectedYear = selectedInterval.slice(0, 4)
	const monthOptions = intervalOptions
		.filter((option) => option.value.startsWith(`${selectedYear}-`))
		.map((option) => ({ ...option, label: option.label.replace(/\s+\d{4}$/, '') }))
	return { intervalOptions, yearOptions, selectedYear, monthOptions }
}

export const useDashboardData = () => {
	const {
		dashboardPersoner,
		dashboardTeams,
		dashboardOrganisasjoner,
		dashboardDollyTeams,
		dashboardOversikt,
		loadingDashboardPersoner,
		loadingDashboardTeams,
		loadingDashboardOrganisasjoner,
		loadingDashboardDollyTeams,
		dashboardPersonerError,
		dashboardTeamsError,
		dashboardOrganisasjonerError,
		dashboardDollyTeamsError,
		dashboardOversiktError,
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
	const [dollyTeamsMonthScope, setDollyTeamsMonthScope] = useState<
		typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL
	>(MONTH_SCOPE_LAST_12)
	const [selectedInterval, setSelectedInterval] = useState('')
	const [selectedOrganisasjonInterval, setSelectedOrganisasjonInterval] = useState('')
	const [selectedDollyTeamsInterval, setSelectedDollyTeamsInterval] = useState('')
	const [selectedFeilInterval, setSelectedFeilInterval] = useState('')
	const [selectedFeilDay, setSelectedFeilDay] = useState<number | null>(null)
	const [selectedDayScope, setSelectedDayScope] = useState<
		typeof DAY_SCOPE_YESTERDAY | typeof DAY_SCOPE_TODAY
	>(DAY_SCOPE_YESTERDAY)
	const [personerTotaltVisible, setPersonerTotaltVisible] = useState(false)
	const [mockModeEnabled, setMockModeEnabled] = useState(false)
	const [mockData, setMockData] = useState(() => createDashboardMockData(0))

	const activeDashboardPersoner = mockModeEnabled ? mockData.dashboardPersoner : dashboardPersoner ?? []
	const activeDashboardTeams = mockModeEnabled ? mockData.dashboardTeams : dashboardTeams ?? []
	const activeDashboardOrganisasjoner = mockModeEnabled
		? mockData.dashboardOrganisasjoner
		: dashboardOrganisasjoner ?? []
	const activeDashboardDollyTeams = mockModeEnabled
		? mockData.dashboardDollyTeams
		: dashboardDollyTeams ?? []
	const activeDashboardOversikt = mockModeEnabled ? mockData.feilOversikt : dashboardOversikt ?? []

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

	const earliestAvailableDateValue = activeDashboardPersoner
		.map((personData) => personData.dato)
		.filter((dateValue): dateValue is string => Boolean(dateValue))
		.sort((a, b) => a.localeCompare(b))
		.at(0)

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
			return acc
		},
		{
			nye: 0,
			gjenopprettede: 0,
			nyeInklGjenopprettede: 0,
		},
	)
	const previousDaySummaryWithTotals = {
		...previousDaySummary,
		nyeInklGjenopprettede: previousDaySummary.nye + previousDaySummary.gjenopprettede,
	}

	const previousDayChartOptions = createPreviousDayChartOptions({
		nye: previousDaySummaryWithTotals.nye,
		gjenopprettede: previousDaySummaryWithTotals.gjenopprettede,
	})

	const selectedDayFeilDager = selectedDayDates.flatMap((dato) => {
		const parsed = parseISO(dato)
		const month = monthNumberToName(parsed.getMonth() + 1)
		return month ? [{ year: parsed.getFullYear(), month, day: parsed.getDate() }] : []
	})

	const { feilForDager, loadingFeilForDager, feilForDagerError } = useDashboardFeilForDager(
		mockModeEnabled ? [] : selectedDayFeilDager,
	)
	const activeSelectedDayFeil = mockModeEnabled
		? selectedDayDates.flatMap((dato) => mockData.feilDetaljertByDate[dato] ?? [])
		: feilForDager
	const selectedDayFeilGrupper = toFeilGrupper(activeSelectedDayFeil)
	const selectedDayFeilCount = new Set(activeSelectedDayFeil.map((rad) => rad.bestillingId)).size

	const filteredPersoner = activeDashboardPersoner
		.filter((personData) => withinDateRange(personData.dato, fraDato, tilDato))
		.sort((a, b) => a.dato.localeCompare(b.dato))
	const completeFilteredPersoner = fillMissingPersonDays(filteredPersoner, fraDato, tilDato)

	const summary = completeFilteredPersoner.reduce(
		(acc, personData) => {
			acc.personerTotalt += asNumber(personData.personerTotalt)
			acc.nye += asNumber(personData.nye)
			acc.gjenopprettede += asNumber(personData.gjenopprettede)
			return acc
		},
		{ personerTotalt: 0, nye: 0, gjenopprettede: 0 },
	)

	const personTrendData = toPersonTrendData(completeFilteredPersoner)
	const personTrendChartOptions = createPersonTrendChartOptions(personTrendData, {
		personerTotaltVisible,
		onPersonerTotaltVisibilityChange: setPersonerTotaltVisible,
	})

	// --- Organisasjoner ---
	const organisasjonMonthlyPoints = toMonthlyOrganisasjonPoints(activeDashboardOrganisasjoner)
	const filteredOrganisasjonPoints = filterMonthlyTeamPoints(
		organisasjonMonthlyPoints,
		organisasjonMonthScope,
	)
	const organisasjonView = buildMonthlyDistributionView(
		organisasjonMonthlyPoints,
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
	const organisasjonDistributionChartOptions = createMonthlyTeamDistributionChartOptions(
		organisasjonView.distribution,
	)

	useAutoSelectLatestInterval(
		organisasjonView.intervalOptions,
		selectedOrganisasjonInterval,
		setSelectedOrganisasjonInterval,
	)

	// --- Dolly Teams ---
	const dollyTeamsMonthlyPoints = toMonthlyDollyTeamPoints(activeDashboardDollyTeams)
	const filteredDollyTeamsPoints = filterMonthlyTeamPoints(
		dollyTeamsMonthlyPoints,
		dollyTeamsMonthScope,
	)
	const dollyTeamsView = buildMonthlyDistributionView(
		dollyTeamsMonthlyPoints,
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
	const dollyTeamsDistributionChartOptions = createMonthlyTeamDistributionChartOptions(
		dollyTeamsView.distribution,
		'Antall medlemmer',
	)

	useAutoSelectLatestInterval(
		dollyTeamsView.intervalOptions,
		selectedDollyTeamsInterval,
		setSelectedDollyTeamsInterval,
	)

	// --- Teams ---
	const monthlyTeamPoints = toMonthlyTeamPoints(activeDashboardTeams)
	const filteredMonthlyTeamPoints = filterMonthlyTeamPoints(monthlyTeamPoints, monthScope)
	const teamView = buildMonthlyDistributionView(monthlyTeamPoints, selectedInterval)
	const monthlyTrendChartOptions = createMonthlyTeamTrendChartOptions(
		toMonthlyTrendData(filteredMonthlyTeamPoints),
	)
	const monthlyDistributionChartOptions = createMonthlyTeamDistributionChartOptions(
		teamView.distribution,
	)

	useAutoSelectLatestInterval(teamView.intervalOptions, selectedInterval, setSelectedInterval)

	// --- Feil (måned → dag drilldown) ---
	const feilPeriodeOptions = toFeilPeriodeOptions(activeDashboardOversikt)
	const feilView = buildFeilPeriodView(feilPeriodeOptions, selectedFeilInterval)
	const selectedFeilPeriode = feilPeriodeOptions.find(
		(option) => option.interval === selectedFeilInterval,
	)
	const feilYear = selectedFeilPeriode?.aar ?? null
	const feilMonthName = selectedFeilPeriode?.maanedNavn ?? null

	const { feilSummert, loadingFeilSummert, feilSummertError } = useDashboardFeilSummert(
		mockModeEnabled ? null : feilYear,
		mockModeEnabled ? null : feilMonthName,
	)
	const { feilDetaljert, loadingFeilDetaljert, feilDetaljertError } = useDashboardFeilDetaljert(
		mockModeEnabled ? null : feilYear,
		mockModeEnabled ? null : feilMonthName,
		mockModeEnabled ? null : selectedFeilDay,
	)

	const activeFeilSummert = mockModeEnabled
		? (mockData.feilByInterval[selectedFeilInterval] ?? [])
		: feilSummert
	const activeFeilDetaljert = mockModeEnabled
		? (mockData.feilDetaljertByDate[
				selectedFeilDay !== null
					? `${selectedFeilInterval}-${String(selectedFeilDay).padStart(2, '0')}`
					: ''
			] ?? [])
		: feilDetaljert

	const feilSummertView = toFeilSummertView(activeFeilSummert)
	const feilSummertChartOptions = createFeilSummertChartOptions(
		feilSummertView.punkter,
		feilSummertView.fagsystemNokler,
		setSelectedFeilDay,
	)
	const feilGrupper = toFeilGrupper(activeFeilDetaljert)
	const selectedFeilPunkt =
		selectedFeilDay !== null
			? (feilSummertView.punkter.find((punkt) => punkt.dag === selectedFeilDay) ?? null)
			: null

	useAutoSelectLatestInterval(
		feilView.intervalOptions,
		selectedFeilInterval,
		setSelectedFeilInterval,
	)

	useEffect(() => {
		setSelectedFeilDay(null)
	}, [selectedFeilInterval])

	const applyQuickRange = (quickRangeValue: string) => {
		const toDateValue = format(quickRangeAnchorDate, 'yyyy-MM-dd')
		const fromDateValue =
			quickRangeValue === 'all'
				? (earliestAvailableDateValue ?? format(subYears(quickRangeAnchorDate, 1), 'yyyy-MM-dd'))
				: quickRangeValue === 'week'
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
		selectedDayFeilGrupper,
		selectedDayFeilCount,
		loadingSelectedDayFeil: loadingFeilForDager && !mockModeEnabled,
		selectedDayFeilError: mockModeEnabled ? undefined : feilForDagerError,

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
		organisasjonYearOptions: organisasjonView.yearOptions,
		selectedOrganisasjonYear: organisasjonView.selectedYear,
		organisasjonMonthOptions: organisasjonView.monthOptions,
		onSelectedOrganisasjonYearChange: makeYearChangeHandler(
			organisasjonView.intervalOptions,
			setSelectedOrganisasjonInterval,
		),
		selectedOrganisasjonInterval,
		onSelectedOrganisasjonIntervalChange: setSelectedOrganisasjonInterval,
		selectedOrganisasjonPoint: organisasjonView.selectedPoint,
		organisasjonDistribution: organisasjonView.distribution,
		organisasjonDistributionChartOptions,

		// Dolly teams section
		filteredDollyTeamsPointsLength: filteredDollyTeamsPoints.length,
		dollyTeamsMonthScope,
		onDollyTeamsMonthScopeChange: setDollyTeamsMonthScope,
		dollyTeamsMonthlyTrendChartOptions,
		dollyTeamsYearOptions: dollyTeamsView.yearOptions,
		selectedDollyTeamsYear: dollyTeamsView.selectedYear,
		dollyTeamsMonthOptions: dollyTeamsView.monthOptions,
		onSelectedDollyTeamsYearChange: makeYearChangeHandler(
			dollyTeamsView.intervalOptions,
			setSelectedDollyTeamsInterval,
		),
		selectedDollyTeamsInterval,
		onSelectedDollyTeamsIntervalChange: setSelectedDollyTeamsInterval,
		selectedDollyTeamsPoint: dollyTeamsView.selectedPoint,
		dollyTeamsDistribution: dollyTeamsView.distribution,
		dollyTeamsDistributionChartOptions,

		// Teams section
		filteredMonthlyTeamPointsLength: filteredMonthlyTeamPoints.length,
		monthScope,
		onMonthScopeChange: setMonthScope,
		monthlyTrendChartOptions,
		teamYearOptions: teamView.yearOptions,
		selectedTeamYear: teamView.selectedYear,
		teamMonthOptions: teamView.monthOptions,
		onSelectedTeamYearChange: makeYearChangeHandler(teamView.intervalOptions, setSelectedInterval),
		selectedTeamInterval: selectedInterval,
		onSelectedTeamIntervalChange: setSelectedInterval,
		selectedMonthlyPoint: teamView.selectedPoint,
		teamDistribution: teamView.distribution,
		monthlyDistributionChartOptions,

		// Feil section
		feilYearOptions: feilView.yearOptions,
		selectedFeilYear: feilView.selectedYear,
		feilMonthOptions: feilView.monthOptions,
		onSelectedFeilYearChange: makeYearChangeHandler(
			feilView.intervalOptions,
			setSelectedFeilInterval,
		),
		selectedFeilInterval,
		onSelectedFeilIntervalChange: setSelectedFeilInterval,
		feilPeriodeVisning: selectedFeilPeriode?.intervalVisning ?? '',
		feilSummertChartOptions,
		feilDagerMedFeil: feilSummertView.punkter.length,
		feilTotalt: feilSummertView.punkter.reduce((sum, punkt) => sum + punkt.total, 0),
		hasFeilData: feilSummertView.punkter.length > 0,
		loadingFeilSummert: loadingFeilSummert && !mockModeEnabled,
		feilSummertError: mockModeEnabled ? undefined : feilSummertError,
		selectedFeilDay,
		selectedFeilPunkt,
		feilSelectedDayLabel: selectedFeilPunkt?.datoVisning ?? '',
		onSelectedFeilDayChange: setSelectedFeilDay,
		feilGrupper,
		feilDetaljertCount: new Set(activeFeilDetaljert.map((rad) => rad.bestillingId)).size,
		loadingFeilDetaljert: loadingFeilDetaljert && !mockModeEnabled,
		feilDetaljertError: mockModeEnabled ? undefined : feilDetaljertError,
	}
}
