import { Alert, BodyShort, Heading, HGrid, VStack } from '@navikt/ds-react'
import { type Options } from 'highcharts'
import {
	DashboardChartPanel,
	DashboardKpiCard,
	DashboardSectionCard,
	DashboardSelectButtons,
} from './dashboardSharedComponents'
import { MONTH_SCOPE_ALL, MONTH_SCOPE_LAST_12, type TeamDistributionPoint } from './dashboardUtils'

export const MonthlyTeamTrendSection = ({
	title = 'Nav-team statistikk',
	description,
	showScopeToggle = true,
	emptyStateMessage = 'Ingen teamdata tilgjengelig.',
	filteredMonthlyTeamPointsLength,
	monthScope,
	onMonthScopeChange,
	monthlyTrendChartOptions,
}: {
	title?: string
	description?: string
	showScopeToggle?: boolean
	emptyStateMessage?: string
	filteredMonthlyTeamPointsLength: number
	monthScope: typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL
	onMonthScopeChange: (value: typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL) => void
	monthlyTrendChartOptions: Options
}) => (
	<DashboardSectionCard>
		<VStack gap="space-16">
			<Heading level="2" size="small">
				{title}
			</Heading>
			{description && <BodyShort>{description}</BodyShort>}
			{filteredMonthlyTeamPointsLength === 0 ? (
				<Alert variant="info" inline>
					{emptyStateMessage}
				</Alert>
			) : (
				<>
					{showScopeToggle && (
						<DashboardSelectButtons
							label="Vis tidsrom"
							selected={monthScope}
							onSelect={(value) =>
								onMonthScopeChange(value as typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL)
							}
							options={[
								{ value: MONTH_SCOPE_LAST_12, label: 'Siste 12 måneder' },
								{ value: MONTH_SCOPE_ALL, label: 'All historikk' },
							]}
						/>
					)}
					<DashboardChartPanel
						options={monthlyTrendChartOptions}
						ariaLabel="Månedlig trend for unike brukere og antall teams"
					/>
				</>
			)}
		</VStack>
	</DashboardSectionCard>
)

export const MonthlyTeamDistributionSection = ({
	title = 'Nav-team fordeling i valgt måned',
	monthLabel = 'Måned',
	primaryTotalLabel = 'Unike brukere totalt',
	secondaryTotalLabel = 'Antall teams totalt',
	yearOptions,
	selectedYear,
	onSelectedYearChange,
	monthOptions,
	onSelectedIntervalChange,
	selectedInterval,
	selectedMonthlyPoint,
	teamDistribution,
	monthlyDistributionChartOptions,
}: {
	title?: string
	monthLabel?: string
	primaryTotalLabel?: string
	secondaryTotalLabel?: string
	yearOptions: string[]
	selectedYear: string
	onSelectedYearChange: (value: string) => void
	monthOptions: { value: string; label: string }[]
	onSelectedIntervalChange: (value: string) => void
	selectedInterval: string
	selectedMonthlyPoint: {
		interval: string
		intervalVisning: string
		totaltUnikeBrukere: number
		totaltAntallTeams: number
	} | null
	teamDistribution: TeamDistributionPoint[]
	monthlyDistributionChartOptions: Options
}) => (
	<DashboardSectionCard>
		<VStack gap="space-16">
			<Heading level="2" size="small">
				{title}
			</Heading>
			{yearOptions.length === 0 ? (
				<Alert variant="info" inline>
					Ingen teamfordeling tilgjengelig.
				</Alert>
			) : (
				<>
					<DashboardSelectButtons
						label="År"
						selected={selectedYear}
						onSelect={onSelectedYearChange}
						options={yearOptions.map((year) => ({ value: year, label: year }))}
					/>
					<DashboardSelectButtons
						label={monthLabel}
						selected={selectedInterval}
						onSelect={onSelectedIntervalChange}
						options={monthOptions}
					/>
					{selectedMonthlyPoint && (
						<HGrid columns={{ xs: 1, sm: 2 }} gap="space-12">
							<DashboardKpiCard
								label={primaryTotalLabel}
								value={selectedMonthlyPoint.totaltUnikeBrukere}
							/>
							<DashboardKpiCard
								label={secondaryTotalLabel}
								value={selectedMonthlyPoint.totaltAntallTeams}
							/>
						</HGrid>
					)}
					{teamDistribution.length === 0 ? (
						<Alert variant="info" inline>
							Ingen teamfordeling tilgjengelig for valgt måned.
						</Alert>
					) : (
						<DashboardChartPanel
							options={monthlyDistributionChartOptions}
							ariaLabel="Teamfordeling for valgt måned"
						/>
					)}
				</>
			)}
		</VStack>
	</DashboardSectionCard>
)
