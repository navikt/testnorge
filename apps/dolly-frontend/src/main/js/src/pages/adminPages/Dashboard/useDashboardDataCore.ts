import { useEffect, useMemo, useState } from 'react'
import { format, parseISO } from 'date-fns'
import {
	type DashboardBestillingerDTO,
	type DashboardDollyTeamsDTO,
	type DashboardFeilDetaljertRad,
	type DashboardOrganisasjonerDTO,
	type DashboardOversiktDTO,
	type DashboardTeamsDTO,
	useDashboard,
	useDashboardFeilForDager,
} from '@/utils/hooks/useDashboard'
import {
	asNumber,
	buildMonthlyDistributionView,
	filterMonthlyTeamPoints,
	getPreviousBusinessPeriod,
	makeYearChangeHandler,
	MONTH_SCOPE_ALL,
	MONTH_SCOPE_LAST_12,
	toDisplayDate,
	toMonthlyDollyTeamPoints,
	toMonthlyOrganisasjonPoints,
	toMonthlyTeamPoints,
	toMonthlyTrendData,
} from './dashboardUtils'
import {
	type FeilPeriodeOption,
	monthNumberToName,
	toFeilGrupper,
	toFeilPeriodeOptions,
} from './dashboardFeilUtils'
import {
	createMonthlyTeamDistributionChartOptions,
	createMonthlyTeamTrendChartOptions,
	createOpprettetGjenopprettetDonutChartOptions,
} from './dashboardChartOptions'
import { createDashboardMockData } from './dashboardMockData'

const DAY_SCOPE_YESTERDAY = 'YESTERDAY'
const DAY_SCOPE_TODAY = 'TODAY'

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

const useAutoSelectLatestInterval = (
	intervalOptions: { value: string }[],
	selectedInterval: string,
	setSetter: (value: string) => void,
) => {
	useEffect(() => {
		if (!selectedInterval && intervalOptions.length > 0) {
			setSetter(intervalOptions[intervalOptions.length - 1].value)
		}
	}, [intervalOptions, selectedInterval, setSetter])
}

const tilListe = <T>(input: unknown): T[] => {
	if (Array.isArray(input)) {
		return input as T[]
	}
	if (!input || typeof input !== 'object') {
		return []
	}
	const record = input as Record<string, unknown>
	if (Array.isArray(record.content)) {
		return record.content as T[]
	}
	if (Array.isArray(record.data)) {
		return record.data as T[]
	}
	return []
}

export const useDashboardDataCore = () => {
	const {
		dashboardBestillinger,
		dashboardTeams,
		dashboardOrganisasjoner,
		dashboardDollyTeams,
		dashboardOversikt,
		loadingDashboardBestillinger,
		loadingDashboardTeams,
		loadingDashboardOrganisasjoner,
		loadingDashboardDollyTeams,
		dashboardBestillingerError,
		dashboardTeamsError,
		dashboardOrganisasjonerError,
		dashboardDollyTeamsError,
		dashboardOversiktError,
		reloadDashboard,
	} = useDashboard()

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
	const [selectedDayScope, setSelectedDayScope] = useState<
		typeof DAY_SCOPE_YESTERDAY | typeof DAY_SCOPE_TODAY
	>(DAY_SCOPE_YESTERDAY)
	const [mockModeEnabled, setMockModeEnabled] = useState(false)
	const [mockData, setMockData] = useState(() => createDashboardMockData(0))

	const activeDashboardBestillinger = mockModeEnabled
		? tilListe<DashboardBestillingerDTO>(mockData.dashboardBestillinger)
		: tilListe<DashboardBestillingerDTO>(dashboardBestillinger)
	const activeDashboardTeams = mockModeEnabled
		? tilListe<DashboardTeamsDTO>(mockData.dashboardTeams)
		: tilListe<DashboardTeamsDTO>(dashboardTeams)
	const activeDashboardOrganisasjoner = mockModeEnabled
		? tilListe<DashboardOrganisasjonerDTO>(mockData.dashboardOrganisasjoner)
		: tilListe<DashboardOrganisasjonerDTO>(dashboardOrganisasjoner)
	const activeDashboardDollyTeams = mockModeEnabled
		? tilListe<DashboardDollyTeamsDTO>(mockData.dashboardDollyTeams)
		: tilListe<DashboardDollyTeamsDTO>(dashboardDollyTeams)
	const activeDashboardOversikt = mockModeEnabled
		? tilListe<DashboardOversiktDTO>(mockData.feilOversikt)
		: tilListe<DashboardOversiktDTO>(dashboardOversikt)

	// Pre-compute feil periode options and auto-select the latest one
	const feilPeriodeOptions = useMemo(
		() => toFeilPeriodeOptions(activeDashboardOversikt),
		[activeDashboardOversikt],
	)

	const todayDate = format(new Date(), 'yyyy-MM-dd')
	const previousBusinessPeriod = getPreviousBusinessPeriod(new Date())
	const selectedDayDates =
		selectedDayScope === DAY_SCOPE_TODAY ? [todayDate] : previousBusinessPeriod.dates
	const previousDayPeriodData = activeDashboardBestillinger
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

	const previousDayChartOptions = createOpprettetGjenopprettetDonutChartOptions({
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
		? selectedDayDates.flatMap((dato) =>
				tilListe<DashboardFeilDetaljertRad>(mockData.feilDetaljertByDate[dato]),
			)
		: tilListe<DashboardFeilDetaljertRad>(feilForDager)
	const selectedDayFeilGrupper = toFeilGrupper(activeSelectedDayFeil)
	const selectedDayFeilCount = new Set(activeSelectedDayFeil.map((rad) => rad.bestillingId)).size

	// --- Organisasjoner ---
	const organisasjonMonthlyPoints = useMemo(
		() => toMonthlyOrganisasjonPoints(activeDashboardOrganisasjoner),
		[activeDashboardOrganisasjoner],
	)

	const filteredOrganisasjonPoints = useMemo(
		() => filterMonthlyTeamPoints(organisasjonMonthlyPoints, organisasjonMonthScope),
		[organisasjonMonthlyPoints, organisasjonMonthScope],
	)

	const organisasjonView = useMemo(
		() => buildMonthlyDistributionView(organisasjonMonthlyPoints, selectedOrganisasjonInterval),
		[organisasjonMonthlyPoints, selectedOrganisasjonInterval],
	)

	const organisasjonMonthlyTrendChartOptions = useMemo(
		() =>
			createMonthlyTeamTrendChartOptions(toMonthlyTrendData(filteredOrganisasjonPoints), {
				description:
					'Linjediagram med månedlig utvikling i unike brukere og antall organisasjoner fra organisasjonsendepunktet.',
				secondSeriesName: 'Antall organisasjoner',
			}),
		[filteredOrganisasjonPoints],
	)

	const organisasjonDistributionChartOptions = useMemo(
		() => createMonthlyTeamDistributionChartOptions(organisasjonView.distribution),
		[organisasjonView.distribution],
	)

	useAutoSelectLatestInterval(
		organisasjonView.intervalOptions,
		selectedOrganisasjonInterval,
		setSelectedOrganisasjonInterval,
	)

	// --- Dolly Teams ---
	const dollyTeamsMonthlyPoints = useMemo(
		() => toMonthlyDollyTeamPoints(activeDashboardDollyTeams),
		[activeDashboardDollyTeams],
	)

	const filteredDollyTeamsPoints = useMemo(
		() => filterMonthlyTeamPoints(dollyTeamsMonthlyPoints, dollyTeamsMonthScope),
		[dollyTeamsMonthlyPoints, dollyTeamsMonthScope],
	)

	const dollyTeamsView = useMemo(
		() => buildMonthlyDistributionView(dollyTeamsMonthlyPoints, selectedDollyTeamsInterval),
		[dollyTeamsMonthlyPoints, selectedDollyTeamsInterval],
	)

	const dollyTeamsMonthlyTrendChartOptions = useMemo(
		() =>
			createMonthlyTeamTrendChartOptions(toMonthlyTrendData(filteredDollyTeamsPoints), {
				description:
					'Linjediagram med månedlig utvikling i unike brukere og antall dolly teams fra dollyteams-endepunktet.',
				secondSeriesName: 'Antall teams',
			}),
		[filteredDollyTeamsPoints],
	)

	const dollyTeamsDistributionChartOptions = useMemo(
		() =>
			createMonthlyTeamDistributionChartOptions(dollyTeamsView.distribution, 'Antall medlemmer'),
		[dollyTeamsView.distribution],
	)

	useAutoSelectLatestInterval(
		dollyTeamsView.intervalOptions,
		selectedDollyTeamsInterval,
		setSelectedDollyTeamsInterval,
	)

	// --- Teams ---
	const monthlyTeamPoints = useMemo(
		() => toMonthlyTeamPoints(activeDashboardTeams),
		[activeDashboardTeams],
	)

	const filteredMonthlyTeamPoints = useMemo(
		() => filterMonthlyTeamPoints(monthlyTeamPoints, monthScope),
		[monthlyTeamPoints, monthScope],
	)

	const teamView = useMemo(
		() => buildMonthlyDistributionView(monthlyTeamPoints, selectedInterval),
		[monthlyTeamPoints, selectedInterval],
	)

	const monthlyTrendChartOptions = useMemo(
		() => createMonthlyTeamTrendChartOptions(toMonthlyTrendData(filteredMonthlyTeamPoints)),
		[filteredMonthlyTeamPoints],
	)

	const monthlyDistributionChartOptions = useMemo(
		() => createMonthlyTeamDistributionChartOptions(teamView.distribution),
		[teamView.distribution],
	)

	useAutoSelectLatestInterval(teamView.intervalOptions, selectedInterval, setSelectedInterval)

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
		mockData,
		onSeedMockData: () => {
			setMockData(createDashboardMockData(Date.now()))
			setMockModeEnabled(true)
		},
		onShowRealData: () => setMockModeEnabled(false),

		// Loading & errors
		isAnyLoading:
			(loadingDashboardBestillinger ||
				loadingDashboardTeams ||
				loadingDashboardOrganisasjoner ||
				loadingDashboardDollyTeams) &&
			!mockModeEnabled,
		loadingDashboardBestillinger,
		loadingDashboardPersoner: loadingDashboardBestillinger,
		loadingDashboardTeams,
		loadingDashboardOrganisasjoner,
		loadingDashboardDollyTeams,
		loadingDashboardOversikt,
		dashboardBestillingerError,
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

		// Feil-related errors (not in context)
		feilSummertError: mockModeEnabled ? undefined : dashboardOversiktError,
		feilDetaljertError: undefined,

		// Data for context providers
		activeDashboardBestillinger,
		activeDashboardOversikt,
		buildFeilPeriodView,
		makeYearChangeHandler,
	}
}
