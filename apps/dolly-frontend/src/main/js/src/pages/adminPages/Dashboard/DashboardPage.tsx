import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import { DashboardHeaderActions } from '@/pages/adminPages/Dashboard/dashboardSharedComponents'
import {
	PersonAnalysisSection,
	PreviousDaySection,
} from '@/pages/adminPages/Dashboard/dashboardDayPersonSections'
import {
	MonthlyTeamDistributionSection,
	MonthlyTeamTrendSection,
} from '@/pages/adminPages/Dashboard/dashboardMonthlySections'
import { useErDollyAdmin } from '@/utils/DollyAdmin'
import { Alert, Box, Loader, VStack } from '@navikt/ds-react'
import Highcharts from 'highcharts'
import HighchartsAccessibility from 'highcharts/modules/accessibility'
import { CHART_TEXT_COLOR } from '@/pages/adminPages/Dashboard/dashboardChartBase'
import { QUICK_RANGE_OPTIONS, useDashboardData } from './useDashboardData'

const initAccessibilityModule =
	typeof HighchartsAccessibility === 'function'
		? HighchartsAccessibility
		: (HighchartsAccessibility as { default?: (chartInstance: typeof Highcharts) => void }).default
initAccessibilityModule?.(Highcharts)

Highcharts.setOptions({
	xAxis: {
		labels: { style: { color: CHART_TEXT_COLOR, fontSize: '12px' } },
		title: { style: { color: CHART_TEXT_COLOR } },
	},
	yAxis: {
		labels: { style: { color: CHART_TEXT_COLOR, fontSize: '12px' } },
		title: { style: { color: CHART_TEXT_COLOR } },
	},
	legend: {
		itemStyle: { color: CHART_TEXT_COLOR },
		itemHoverStyle: { color: CHART_TEXT_COLOR },
	},
	title: { style: { color: CHART_TEXT_COLOR } },
	subtitle: { style: { color: CHART_TEXT_COLOR } },
})

export default () => {
	const isAdmin = useErDollyAdmin()
	const d = useDashboardData()
	const isDevDashboardMode =
		typeof window !== 'undefined' &&
		(window.location.hostname.includes('localhost') ||
			window.location.hostname.includes('dolly-frontend-dev'))

	if (!isAdmin) {
		return <AdminAccessDenied />
	}

	return (
		<>
			<VStack gap={{ xs: 'space-16', md: 'space-24' }}>
				<Box>
					<h1>Dashboard</h1>
					<p>Statistikk for syntetisering av identer og bruk av Dolly.</p>
				</Box>

				{isDevDashboardMode && (
					<DashboardHeaderActions
						mockModeEnabled={d.mockModeEnabled}
						onSeedMockData={d.onSeedMockData}
						onShowRealData={d.onShowRealData}
					/>
				)}

				<PreviousDaySection
					selectedDayDisplayLabel={d.selectedDayDisplayLabel}
					selectedDayPeriodTitle={d.selectedDayPeriodTitle}
					selectedDayButtonLabel={d.selectedDayButtonLabel}
					selectedDayScope={d.selectedDayScope}
					onSelectedDayScopeChange={d.onSelectedDayScopeChange}
					previousDayPeriodData={d.previousDayPeriodData}
					previousDaySummary={d.previousDaySummary}
					previousDayChartOptions={d.previousDayChartOptions}
					previousDayErrorBreakdownChartOptions={d.previousDayErrorBreakdownChartOptions}
					isLoading={d.loadingDashboardPersoner && !d.mockModeEnabled}
				/>

				<PersonAnalysisSection
					quickRangeOptions={QUICK_RANGE_OPTIONS}
					selectedQuickRange={d.selectedQuickRange}
					fraDato={d.fraDato}
					tilDato={d.tilDato}
					tilDatoMax={d.todayDate}
					onFraDatoChange={d.onFraDatoChange}
					onTilDatoChange={d.onTilDatoChange}
					onQuickRangeClick={d.onQuickRangeClick}
					onRefresh={d.onRefresh}
					filteredPersonerLength={d.filteredPersonerLength}
					summary={d.summary}
					personTrendDataLength={d.personTrendDataLength}
					personTrendChartOptions={d.personTrendChartOptions}
				/>

				{d.isAnyLoading && (
					<Box aria-busy="true" aria-live="polite">
						<Loader size="xlarge" title="Laster dashboard-data..." />
					</Box>
				)}

				{d.dashboardPersonerError && (
					<Alert variant="error">
						Klarte ikke å hente persondata: {d.dashboardPersonerError.message}
					</Alert>
				)}
				{d.dashboardTeamsError && (
					<Alert variant="error">
						Klarte ikke å hente teamdata: {d.dashboardTeamsError.message}
					</Alert>
				)}
				{d.dashboardOrganisasjonerError && (
					<Alert variant="error">
						Klarte ikke å hente organisasjonsdata: {d.dashboardOrganisasjonerError.message}
					</Alert>
				)}
				{d.dashboardDollyTeamsError && (
					<Alert variant="error">
						Klarte ikke å hente dolly team-data: {d.dashboardDollyTeamsError.message}
					</Alert>
				)}

				<MonthlyTeamTrendSection
					filteredMonthlyTeamPointsLength={d.filteredMonthlyTeamPointsLength}
					monthScope={d.monthScope}
					onMonthScopeChange={d.onMonthScopeChange}
					monthlyTrendChartOptions={d.monthlyTrendChartOptions}
				/>

				<MonthlyTeamDistributionSection
					yearOptions={d.teamYearOptions}
					selectedYear={d.selectedTeamYear}
					onSelectedYearChange={d.onSelectedTeamYearChange}
					monthOptions={d.teamMonthOptions}
					onSelectedIntervalChange={d.onSelectedTeamIntervalChange}
					selectedInterval={d.selectedTeamInterval}
					selectedMonthlyPoint={d.selectedMonthlyPoint}
					teamDistribution={d.teamDistribution}
					monthlyDistributionChartOptions={d.monthlyDistributionChartOptions}
				/>

				<MonthlyTeamTrendSection
					title="Ekstern bruk over tid"
					filteredMonthlyTeamPointsLength={d.filteredOrganisasjonPointsLength}
					monthScope={d.organisasjonMonthScope}
					onMonthScopeChange={d.onOrganisasjonMonthScopeChange}
					monthlyTrendChartOptions={d.organisasjonMonthlyTrendChartOptions}
				/>

				<MonthlyTeamDistributionSection
					title="Ekstern bruk i valgt måned"
					monthLabel="Måned"
					primaryTotalLabel="Unike brukere totalt"
					secondaryTotalLabel="Antall organisasjoner totalt"
					yearOptions={d.organisasjonYearOptions}
					selectedYear={d.selectedOrganisasjonYear}
					onSelectedYearChange={d.onSelectedOrganisasjonYearChange}
					monthOptions={d.organisasjonMonthOptions}
					onSelectedIntervalChange={d.onSelectedOrganisasjonIntervalChange}
					selectedInterval={d.selectedOrganisasjonInterval}
					selectedMonthlyPoint={d.selectedOrganisasjonPoint}
					teamDistribution={d.organisasjonDistribution}
					monthlyDistributionChartOptions={d.organisasjonDistributionChartOptions}
				/>

				<MonthlyTeamTrendSection
					title="Dolly-team statistikk"
					filteredMonthlyTeamPointsLength={d.filteredDollyTeamsPointsLength}
					monthScope={d.dollyTeamsMonthScope}
					onMonthScopeChange={d.onDollyTeamsMonthScopeChange}
					monthlyTrendChartOptions={d.dollyTeamsMonthlyTrendChartOptions}
				/>

				<MonthlyTeamDistributionSection
					title="Dolly-team fordeling i valgt måned"
					monthLabel="Måned"
					primaryTotalLabel="Antall medlemmer totalt"
					secondaryTotalLabel="Antall teams totalt"
					yearOptions={d.dollyTeamsYearOptions}
					selectedYear={d.selectedDollyTeamsYear}
					onSelectedYearChange={d.onSelectedDollyTeamsYearChange}
					monthOptions={d.dollyTeamsMonthOptions}
					onSelectedIntervalChange={d.onSelectedDollyTeamsIntervalChange}
					selectedInterval={d.selectedDollyTeamsInterval}
					selectedMonthlyPoint={d.selectedDollyTeamsPoint}
					teamDistribution={d.dollyTeamsDistribution}
					monthlyDistributionChartOptions={d.dollyTeamsDistributionChartOptions}
				/>
			</VStack>
		</>
	)
}
