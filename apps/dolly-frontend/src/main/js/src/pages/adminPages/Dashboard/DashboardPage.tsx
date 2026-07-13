import { AdminAccessDenied } from '@/pages/adminPages/AdminAccessDenied'
import { DashboardHeaderActions } from '@/pages/adminPages/Dashboard/dashboardSharedComponents'
import {
	PersonAnalysisSection,
	PreviousDaySection,
} from '@/pages/adminPages/Dashboard/dashboardDayPersonSections'
import { DashboardFeilSection } from '@/pages/adminPages/Dashboard/dashboardFeilSection'
import {
	MonthlyTeamDistributionSection,
	MonthlyTeamTrendSection,
} from '@/pages/adminPages/Dashboard/dashboardMonthlySections'
import { useErDollyAdmin } from '@/utils/DollyAdmin'
import { Alert, Box, VStack } from '@navikt/ds-react'
import DollySpinner from '@/components/ui/loading/DollySpinner'
import Highcharts from 'highcharts'
import HighchartsAccessibility from 'highcharts/modules/accessibility'
import { CHART_TEXT_COLOR } from '@/pages/adminPages/Dashboard/dashboardChartBase'
import { DashboardFeilProvider } from './DashboardFeilContext'
import { DashboardPersonProvider } from './DashboardPersonContext'
import { useDashboardDataCore } from './useDashboardDataCore'

const initAccessibilityModule =
	typeof HighchartsAccessibility === 'function'
		? HighchartsAccessibility
		: (HighchartsAccessibility as { default?: (chartInstance: typeof Highcharts) => void }).default
initAccessibilityModule?.(Highcharts)

Highcharts.setOptions({
	palette: { colorScheme: 'light' },
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
	const coreData = useDashboardDataCore()
	const isDevDashboardMode =
		typeof window !== 'undefined' &&
		(window.location.hostname.includes('localhost') ||
			window.location.hostname.includes('dolly-frontend-dev'))

	if (!isAdmin) {
		return <AdminAccessDenied />
	}

	return (
		<DashboardPersonProvider
			dashboardBestillinger={coreData.activeDashboardBestillinger}
			mockModeEnabled={coreData.mockModeEnabled}
		>
			<DashboardFeilProvider
				dashboardOversikt={coreData.activeDashboardOversikt}
				mockModeEnabled={coreData.mockModeEnabled}
				mockData={coreData.mockData}
				makeYearChangeHandler={coreData.makeYearChangeHandler}
				buildFeilPeriodView={coreData.buildFeilPeriodView}
			>
				<>
					<VStack gap={{ xs: 'space-16', md: 'space-24' }}>
						<Box>
							<h1>Dashboard</h1>
							<p>Statistikk for syntetisering av personer og bruk av Dolly.</p>
						</Box>

						{isDevDashboardMode && (
							<DashboardHeaderActions
								mockModeEnabled={coreData.mockModeEnabled}
								onSeedMockData={coreData.onSeedMockData}
								onShowRealData={coreData.onShowRealData}
							/>
						)}

						<PreviousDaySection
							selectedDayDisplayLabel={coreData.selectedDayDisplayLabel}
							selectedDayPeriodTitle={coreData.selectedDayPeriodTitle}
							selectedDayButtonLabel={coreData.selectedDayButtonLabel}
							selectedDayScope={coreData.selectedDayScope}
							onSelectedDayScopeChange={coreData.onSelectedDayScopeChange}
							previousDayPeriodData={coreData.previousDayPeriodData}
							previousDaySummary={coreData.previousDaySummary}
							previousDayChartOptions={coreData.previousDayChartOptions}
							selectedDayFeilGrupper={coreData.selectedDayFeilGrupper}
							selectedDayFeilCount={coreData.selectedDayFeilCount}
							loadingSelectedDayFeil={coreData.loadingSelectedDayFeil}
							isLoading={coreData.loadingDashboardBestillinger && !coreData.mockModeEnabled}
						/>

						<PersonAnalysisSection
							isLoading={coreData.loadingDashboardPersoner && !coreData.mockModeEnabled}
						/>

						<DashboardFeilSection
							isLoading={coreData.loadingDashboardOversikt && !coreData.mockModeEnabled}
						/>

						{coreData.isAnyLoading && (
							<Box aria-busy="true" aria-live="polite">
								<DollySpinner size={120} label="Laster dashboard-data..." />
							</Box>
						)}

						{coreData.dashboardBestillingerError && (
							<Alert variant="error">
								Klarte ikke å hente bestillingsdata: {coreData.dashboardBestillingerError.message}
							</Alert>
						)}
						{coreData.selectedDayFeilError && (
							<Alert variant="error">
								Klarte ikke å hente feil for valgt dag: {coreData.selectedDayFeilError.message}
							</Alert>
						)}
						{coreData.dashboardTeamsError && (
							<Alert variant="error">
								Klarte ikke å hente teamdata: {coreData.dashboardTeamsError.message}
							</Alert>
						)}
						{coreData.dashboardOrganisasjonerError && (
							<Alert variant="error">
								Klarte ikke å hente organisasjonsdata:{' '}
								{coreData.dashboardOrganisasjonerError.message}
							</Alert>
						)}
						{coreData.dashboardDollyTeamsError && (
							<Alert variant="error">
								Klarte ikke å hente dolly team-data: {coreData.dashboardDollyTeamsError.message}
							</Alert>
						)}
						{coreData.feilSummertError && (
							<Alert variant="error">
								Klarte ikke å hente feildata: {coreData.feilSummertError.message}
							</Alert>
						)}
						{coreData.feilDetaljertError && (
							<Alert variant="error">
								Klarte ikke å hente detaljerte feil: {coreData.feilDetaljertError.message}
							</Alert>
						)}

						<MonthlyTeamTrendSection
							filteredMonthlyTeamPointsLength={coreData.filteredMonthlyTeamPointsLength}
							monthScope={coreData.monthScope}
							onMonthScopeChange={coreData.onMonthScopeChange}
							monthlyTrendChartOptions={coreData.monthlyTrendChartOptions}
							isLoading={coreData.loadingDashboardTeams && !coreData.mockModeEnabled}
						/>

						<MonthlyTeamDistributionSection
							yearOptions={coreData.teamYearOptions}
							selectedYear={coreData.selectedTeamYear}
							onSelectedYearChange={coreData.onSelectedTeamYearChange}
							monthOptions={coreData.teamMonthOptions}
							onSelectedIntervalChange={coreData.onSelectedTeamIntervalChange}
							selectedInterval={coreData.selectedTeamInterval}
							selectedMonthlyPoint={coreData.selectedMonthlyPoint}
							teamDistribution={coreData.teamDistribution}
							monthlyDistributionChartOptions={coreData.monthlyDistributionChartOptions}
							isLoading={coreData.loadingDashboardTeams && !coreData.mockModeEnabled}
						/>

						<MonthlyTeamTrendSection
							title="Ekstern bruk over tid"
							emptyStateMessage="Ingen organisasjonsdata tilgjengelig."
							filteredMonthlyTeamPointsLength={coreData.filteredOrganisasjonPointsLength}
							monthScope={coreData.organisasjonMonthScope}
							onMonthScopeChange={coreData.onOrganisasjonMonthScopeChange}
							monthlyTrendChartOptions={coreData.organisasjonMonthlyTrendChartOptions}
							isLoading={coreData.loadingDashboardOrganisasjoner && !coreData.mockModeEnabled}
						/>

						<MonthlyTeamDistributionSection
							title="Ekstern bruk i valgt måned"
							monthLabel="Måned"
							primaryTotalLabel="Unike brukere totalt"
							secondaryTotalLabel="Antall organisasjoner totalt"
							yearOptions={coreData.organisasjonYearOptions}
							selectedYear={coreData.selectedOrganisasjonYear}
							onSelectedYearChange={coreData.onSelectedOrganisasjonYearChange}
							monthOptions={coreData.organisasjonMonthOptions}
							onSelectedIntervalChange={coreData.onSelectedOrganisasjonIntervalChange}
							selectedInterval={coreData.selectedOrganisasjonInterval}
							selectedMonthlyPoint={coreData.selectedOrganisasjonPoint}
							teamDistribution={coreData.organisasjonDistribution}
							monthlyDistributionChartOptions={coreData.organisasjonDistributionChartOptions}
							isLoading={coreData.loadingDashboardOrganisasjoner && !coreData.mockModeEnabled}
						/>

						<MonthlyTeamTrendSection
							title="Dolly-team statistikk"
							emptyStateMessage="Ingen dolly team-data tilgjengelig."
							filteredMonthlyTeamPointsLength={coreData.filteredDollyTeamsPointsLength}
							monthScope={coreData.dollyTeamsMonthScope}
							onMonthScopeChange={coreData.onDollyTeamsMonthScopeChange}
							monthlyTrendChartOptions={coreData.dollyTeamsMonthlyTrendChartOptions}
							isLoading={coreData.loadingDashboardDollyTeams && !coreData.mockModeEnabled}
						/>

						<MonthlyTeamDistributionSection
							title="Dolly-team fordeling i valgt måned"
							monthLabel="Måned"
							primaryTotalLabel="Antall medlemmer totalt"
							secondaryTotalLabel="Antall teams totalt"
							yearOptions={coreData.dollyTeamsYearOptions}
							selectedYear={coreData.selectedDollyTeamsYear}
							onSelectedYearChange={coreData.onSelectedDollyTeamsYearChange}
							monthOptions={coreData.dollyTeamsMonthOptions}
							onSelectedIntervalChange={coreData.onSelectedDollyTeamsIntervalChange}
							selectedInterval={coreData.selectedDollyTeamsInterval}
							selectedMonthlyPoint={coreData.selectedDollyTeamsPoint}
							teamDistribution={coreData.dollyTeamsDistribution}
							monthlyDistributionChartOptions={coreData.dollyTeamsDistributionChartOptions}
							isLoading={coreData.loadingDashboardDollyTeams && !coreData.mockModeEnabled}
						/>
					</VStack>
				</>
			</DashboardFeilProvider>
		</DashboardPersonProvider>
	)
}
