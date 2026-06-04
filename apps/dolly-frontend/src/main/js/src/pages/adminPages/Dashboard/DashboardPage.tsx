import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import {
	DashboardHeaderActions,
	MonthlyTeamDistributionSection,
	MonthlyTeamTrendSection,
	PersonAnalysisSection,
	PreviousDaySection,
} from '@/pages/adminPages/Dashboard/dashboardPanels'
import { useErDollyAdmin } from '@/utils/DollyAdmin'
import { useDashboard } from '@/utils/hooks/useDashboard'
import { Alert, BodyShort, Box, Heading, Loader, Page, VStack } from '@navikt/ds-react'
import { format, subDays, subYears } from 'date-fns'
import { useEffect, useState } from 'react'
import Highcharts from 'highcharts'
import HighchartsAccessibility from 'highcharts/modules/accessibility'
import {
	asNumber,
	createDashboardMockData,
	filterMonthlyTeamPoints,
	MONTH_SCOPE_ALL,
	MONTH_SCOPE_LAST_12,
	toMonthlyIntervalOptions,
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

const initAccessibilityModule =
	typeof HighchartsAccessibility === 'function'
		? HighchartsAccessibility
		: (HighchartsAccessibility as { default?: (chartInstance: typeof Highcharts) => void }).default
initAccessibilityModule?.(Highcharts)

const QUICK_RANGE_OPTIONS = [
	{ value: 'today', label: 'I dag' },
	{ value: 'week', label: 'Siste uke' },
	{ value: 'month', label: 'Siste måned' },
	{ value: 'threeMonths', label: 'Siste 3 måneder' },
	{ value: 'sixMonths', label: 'Siste 6 måneder' },
	{ value: 'year', label: 'Siste år' },
] as const

export default () => {
	const isAdmin = useErDollyAdmin()
	const {
		dashboardPersoner,
		dashboardTeams,
		loadingDashboardPersoner,
		loadingDashboardTeams,
		dashboardPersonerError,
		dashboardTeamsError,
		reloadDashboard,
	} = useDashboard()

	const [fraDato, setFraDato] = useState(() => format(subDays(new Date(), 6), 'yyyy-MM-dd'))
	const [tilDato, setTilDato] = useState(() => format(new Date(), 'yyyy-MM-dd'))
	const [monthScope, setMonthScope] = useState<typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL>(
		MONTH_SCOPE_LAST_12,
	)
	const [selectedInterval, setSelectedInterval] = useState('')
	const [mockModeEnabled, setMockModeEnabled] = useState(false)
	const [mockData, setMockData] = useState(() => createDashboardMockData(0))

	const activeDashboardPersoner = mockModeEnabled ? mockData.dashboardPersoner : dashboardPersoner
	const activeDashboardTeams = mockModeEnabled ? mockData.dashboardTeams : dashboardTeams

	const handleRefresh = () => {
		if (mockModeEnabled) {
			setMockData(createDashboardMockData(Date.now()))
			return
		}
		reloadDashboard()
	}

	const applyQuickRange = (quickRangeValue: string) => {
		const today = new Date()
		const toDateValue = format(today, 'yyyy-MM-dd')
		const fromDateValue =
			quickRangeValue === 'today'
				? format(today, 'yyyy-MM-dd')
				: quickRangeValue === 'week'
					? format(subDays(today, 6), 'yyyy-MM-dd')
					: quickRangeValue === 'month'
						? format(subDays(today, 29), 'yyyy-MM-dd')
						: quickRangeValue === 'threeMonths'
							? format(subDays(today, 89), 'yyyy-MM-dd')
							: quickRangeValue === 'sixMonths'
								? format(subDays(today, 179), 'yyyy-MM-dd')
								: format(subYears(today, 1), 'yyyy-MM-dd')

		setFraDato(fromDateValue)
		setTilDato(toDateValue)
	}

	const previousDayDate = format(subDays(new Date(), 1), 'yyyy-MM-dd')
	const previousDayPersonData = activeDashboardPersoner.find(
		(personData) => personData.dato === previousDayDate,
	)
	const previousDaySummary = (() => {
		const nye = asNumber(previousDayPersonData?.nye)
		const gjenopprettede = asNumber(previousDayPersonData?.gjenopprettede)
		const pdlFeil = asNumber(previousDayPersonData?.pdlFeil)
		const andreFeil = asNumber(previousDayPersonData?.andreFeil)

		return {
			nye,
			gjenopprettede,
			nyeInklGjenopprettede: nye + gjenopprettede,
			pdlFeil,
			andreFeil,
			totaltFeil: pdlFeil + andreFeil,
		}
	})()

	const previousDayChartOptions = createPreviousDayChartOptions({
		nye: previousDaySummary.nye,
		gjenopprettede: previousDaySummary.gjenopprettede,
	})

	const previousDayErrorBreakdownChartOptions = createPreviousDayErrorBreakdownChartOptions(
		previousDaySummary.pdlFeil,
		previousDaySummary.andreFeil,
	)

	const filteredPersoner = activeDashboardPersoner
		.filter((personData) => withinDateRange(personData.dato, fraDato, tilDato))
		.sort((a, b) => a.dato.localeCompare(b.dato))

	const summary = filteredPersoner.reduce(
		(acc, personData) => {
			acc.personerTotalt += asNumber(personData.personerTotalt)
			acc.nye += asNumber(personData.nye)
			acc.gjenopprettede += asNumber(personData.gjenopprettede)
			acc.pdlFeil += asNumber(personData.pdlFeil)
			acc.andreFeil += asNumber(personData.andreFeil)
			return acc
		},
		{
			personerTotalt: 0,
			nye: 0,
			gjenopprettede: 0,
			pdlFeil: 0,
			andreFeil: 0,
		},
	)

	const personTrendData = toPersonTrendData(filteredPersoner)
	const personTrendChartOptions = createPersonTrendChartOptions(personTrendData)

	const monthlyTeamPoints = toMonthlyTeamPoints(activeDashboardTeams)
	const filteredMonthlyTeamPoints = filterMonthlyTeamPoints(monthlyTeamPoints, monthScope)
	const monthlyIntervalOptions = toMonthlyIntervalOptions(filteredMonthlyTeamPoints)

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

	const monthlyTrendData = toMonthlyTrendData(filteredMonthlyTeamPoints)
	const selectedMonthlyPoint =
		filteredMonthlyTeamPoints.find((point) => point.interval === selectedInterval) ?? null
	const teamDistribution = toTeamDistributionForInterval(
		filteredMonthlyTeamPoints,
		selectedInterval,
	)
	const monthlyTrendChartOptions = createMonthlyTeamTrendChartOptions(monthlyTrendData)
	const monthlyDistributionChartOptions =
		createMonthlyTeamDistributionChartOptions(teamDistribution)

	if (!isAdmin) {
		return <AdminAccessDenied />
	}

	return (
		<Page contentBlockPadding="end">
			<Page.Block as="main" width="xl" gutters>
				<VStack gap={{ xs: 'space-16', md: 'space-24' }}>
					<Heading level="1" size="large">
						Dashboard
					</Heading>
					<BodyShort>Periodevis oversikt over syntetisering av identer og teamaktivitet.</BodyShort>
					<DashboardHeaderActions
						mockModeEnabled={mockModeEnabled}
						onSeedMockData={() => {
							setMockData(createDashboardMockData(Date.now()))
							setMockModeEnabled(true)
						}}
						onShowRealData={() => setMockModeEnabled(false)}
					/>

					<PreviousDaySection
						previousDayDate={previousDayDate}
						previousDayPersonData={previousDayPersonData}
						previousDaySummary={previousDaySummary}
						previousDayChartOptions={previousDayChartOptions}
						previousDayErrorBreakdownChartOptions={previousDayErrorBreakdownChartOptions}
					/>

					<PersonAnalysisSection
						quickRangeOptions={QUICK_RANGE_OPTIONS}
						fraDato={fraDato}
						tilDato={tilDato}
						onFraDatoChange={setFraDato}
						onTilDatoChange={setTilDato}
						onQuickRangeClick={applyQuickRange}
						onRefresh={handleRefresh}
						filteredPersonerLength={filteredPersoner.length}
						summary={summary}
						personTrendDataLength={personTrendData.length}
						personTrendChartOptions={personTrendChartOptions}
					/>

					{(loadingDashboardPersoner || loadingDashboardTeams) && (
						<Box aria-busy="true" aria-live="polite">
							<Loader size="xlarge" title="Laster dashboard-data..." />
						</Box>
					)}

					{dashboardPersonerError && (
						<Alert variant="error">
							Klarte ikke å hente persondata: {dashboardPersonerError.message}
						</Alert>
					)}
					{dashboardTeamsError && (
						<Alert variant="error">
							Klarte ikke å hente teamdata: {dashboardTeamsError.message}
						</Alert>
					)}

					<MonthlyTeamTrendSection
						filteredMonthlyTeamPointsLength={filteredMonthlyTeamPoints.length}
						monthScope={monthScope}
						onMonthScopeChange={setMonthScope}
						monthlyTrendChartOptions={monthlyTrendChartOptions}
					/>

					<MonthlyTeamDistributionSection
						monthlyIntervalOptions={monthlyIntervalOptions}
						selectedInterval={selectedInterval}
						onSelectedIntervalChange={setSelectedInterval}
						selectedMonthlyPoint={selectedMonthlyPoint}
						teamDistribution={teamDistribution}
						monthlyDistributionChartOptions={monthlyDistributionChartOptions}
					/>
				</VStack>
			</Page.Block>
		</Page>
	)
}
