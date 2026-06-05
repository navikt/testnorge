import { Alert, BodyShort, Button, Heading, HGrid, HStack, Label, VStack } from '@navikt/ds-react'
import { type Options } from 'highcharts'
import {
	DashboardChartPanel,
	DashboardKpiCard,
	DashboardSectionCard,
} from './dashboardSharedComponents'
import { MONTH_SCOPE_ALL, MONTH_SCOPE_LAST_12, type TeamDistributionPoint } from './dashboardUtils'

export const MonthlyTeamTrendSection = ({
	title = 'Nav-team statistikk',
	description,
	showScopeToggle = true,
	filteredMonthlyTeamPointsLength,
	monthScope,
	onMonthScopeChange,
	monthlyTrendChartOptions,
}: {
	title?: string
	description?: string
	showScopeToggle?: boolean
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
					Ingen teamdata tilgjengelig.
				</Alert>
			) : (
				<>
					{showScopeToggle && (
						<VStack gap="space-8">
							<Label>Vis tidsrom</Label>
							<HStack gap="space-8" wrap>
								<Button
									variant={monthScope === MONTH_SCOPE_LAST_12 ? 'secondary' : 'tertiary'}
									size="small"
									onClick={() => onMonthScopeChange(MONTH_SCOPE_LAST_12)}
								>
									Siste 12 måneder
								</Button>
								<Button
									variant={monthScope === MONTH_SCOPE_ALL ? 'secondary' : 'tertiary'}
									size="small"
									onClick={() => onMonthScopeChange(MONTH_SCOPE_ALL)}
								>
									All historikk
								</Button>
							</HStack>
						</VStack>
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
	monthOptions,
	onSelectedYearChange,
	selectedInterval,
	onSelectedIntervalChange,
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
	monthOptions: { value: string; label: string }[]
	onSelectedYearChange: (value: string) => void
	selectedInterval: string
	onSelectedIntervalChange: (value: string) => void
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
					<VStack gap="space-8">
						<Label>År</Label>
						<HStack gap="space-8" wrap>
							{yearOptions.map((year) => (
								<Button
									key={year}
									variant={selectedYear === year ? 'secondary' : 'tertiary'}
									size="small"
									onClick={() => onSelectedYearChange(year)}
								>
									{year}
								</Button>
							))}
						</HStack>
					</VStack>
					<VStack gap="space-8">
						<Label>{monthLabel}</Label>
						<HStack gap="space-8" wrap>
							{monthOptions.map((option) => (
								<Button
									key={option.value}
									variant={selectedInterval === option.value ? 'secondary' : 'tertiary'}
									size="small"
									onClick={() => onSelectedIntervalChange(option.value)}
								>
									{option.label}
								</Button>
							))}
						</HStack>
					</VStack>
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

export const GenericTrendSection = ({
	title,
	description,
	datasetFields,
	seriesTotals,
	chartOptions,
	ariaLabel,
	emptyMessage,
}: {
	title: string
	description?: string
	datasetFields: string[]
	seriesTotals: { name: string; total: number }[]
	chartOptions: Options
	ariaLabel: string
	emptyMessage: string
}) => (
	<DashboardSectionCard>
		<VStack gap="space-16">
			<Heading level="2" size="small">
				{title}
			</Heading>
			{description && <BodyShort>{description}</BodyShort>}
			{seriesTotals.length === 0 ? (
				<Alert variant="info" inline>
					{emptyMessage}
				</Alert>
			) : (
				<>
					<VStack gap="space-4">
						<Label>Datasettet inneholder</Label>
						<BodyShort>{datasetFields.join(', ')}</BodyShort>
					</VStack>
					<HGrid columns={{ xs: 1, sm: 2, lg: 4 }} gap="space-12">
						{seriesTotals.map((seriesItem) => (
							<DashboardKpiCard
								key={seriesItem.name}
								label={`Totalt ${seriesItem.name.toLowerCase()}`}
								value={seriesItem.total}
							/>
						))}
					</HGrid>
					<DashboardChartPanel options={chartOptions} ariaLabel={ariaLabel} />
				</>
			)}
		</VStack>
	</DashboardSectionCard>
)
