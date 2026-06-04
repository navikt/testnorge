import {
	Alert,
	BodyShort,
	Button,
	Heading,
	HGrid,
	HStack,
	Label,
	Select,
	TextField,
	VStack,
} from '@navikt/ds-react'
import { type Options } from 'highcharts'
import {
	DashboardChartPanel,
	DashboardKpiCard,
	DashboardSectionCard,
} from './dashboardSharedComponents'
import { type DashboardPersonerDTO } from '@/utils/hooks/useDashboard'
import {
	MONTH_SCOPE_ALL,
	MONTH_SCOPE_LAST_12,
	type TeamDistributionPoint,
	toDisplayDate,
} from './dashboardUtils'

type QuickRangeOption = {
	value: string
	label: string
}

type PersonSummary = {
	personerTotalt: number
	nye: number
	gjenopprettede: number
	pdlFeil: number
	andreFeil: number
}

type PreviousDaySummary = PersonSummary & {
	nyeInklGjenopprettede: number
	totaltFeil: number
}

export const PreviousDaySection = ({
	previousDayDate,
	previousDayPersonData,
	previousDaySummary,
	previousDayChartOptions,
	previousDayErrorBreakdownChartOptions,
}: {
	previousDayDate: string
	previousDayPersonData: DashboardPersonerDTO | undefined
	previousDaySummary: PreviousDaySummary
	previousDayChartOptions: Options
	previousDayErrorBreakdownChartOptions: Options
}) => (
	<DashboardSectionCard>
		<VStack gap="space-16">
			<Heading level="2" size="small">
				Analyse for hele gårsdagen ({toDisplayDate(previousDayDate)})
			</Heading>
			{!previousDayPersonData ? (
				<Alert variant="info" inline>
					Ingen data tilgjengelig for forrige dag.
				</Alert>
			) : (
				<>
					<HGrid columns={{ xs: 1, sm: 2 }} gap="space-12">
						<DashboardKpiCard
							label="Identer opprettet/gjenopprettet"
							value={previousDaySummary.nyeInklGjenopprettede}
						/>
						<DashboardKpiCard label="Antall feil" value={previousDaySummary.totaltFeil} />
					</HGrid>
					<HGrid columns={{ xs: 1, lg: 2 }} gap="space-16">
						<DashboardChartPanel
							options={previousDayChartOptions}
							ariaLabel="Opprettet og gjenopprettet forrige dag"
						/>
						{previousDaySummary.totaltFeil === 0 ? (
							<Alert variant="info" inline>
								Ingen feil registrert for gårsdagen.
							</Alert>
						) : (
							<DashboardChartPanel
								options={previousDayErrorBreakdownChartOptions}
								ariaLabel="Feilfordeling per system forrige dag"
							/>
						)}
					</HGrid>
				</>
			)}
		</VStack>
	</DashboardSectionCard>
)

export const PersonAnalysisSection = ({
	quickRangeOptions,
	fraDato,
	tilDato,
	onFraDatoChange,
	onTilDatoChange,
	onQuickRangeClick,
	onRefresh,
	filteredPersonerLength,
	summary,
	personTrendDataLength,
	personTrendChartOptions,
}: {
	quickRangeOptions: QuickRangeOption[]
	fraDato: string
	tilDato: string
	onFraDatoChange: (value: string) => void
	onTilDatoChange: (value: string) => void
	onQuickRangeClick: (value: string) => void
	onRefresh: () => void
	filteredPersonerLength: number
	summary: PersonSummary
	personTrendDataLength: number
	personTrendChartOptions: Options
}) => (
	<DashboardSectionCard>
		<VStack gap="space-16">
			<Heading level="2" size="small">
				Personanalyse per dag
			</Heading>
			<VStack gap="space-8">
				<Label>Hurtigvalg periode</Label>
				<HStack gap="space-8" wrap>
					{quickRangeOptions.map((option) => (
						<Button
							key={option.value}
							variant="tertiary"
							size="small"
							onClick={() => onQuickRangeClick(option.value)}
						>
							{option.label}
						</Button>
					))}
				</HStack>
			</VStack>
			<HGrid columns={{ xs: 1, sm: 2, lg: 5 }} gap="space-16" align="end">
				<TextField
					label="Fra dato"
					type="date"
					value={fraDato}
					onChange={(event) => onFraDatoChange(event.target.value)}
				/>
				<TextField
					label="Til dato"
					type="date"
					value={tilDato}
					onChange={(event) => onTilDatoChange(event.target.value)}
				/>
				<Button variant="secondary" onClick={onRefresh}>
					Oppdater data
				</Button>
			</HGrid>
			<HGrid columns={{ xs: 1, sm: 2, lg: 5 }} gap="space-12">
				<DashboardKpiCard label="Dager i periode" value={filteredPersonerLength} />
				<DashboardKpiCard label="Personer totalt" value={summary.personerTotalt} />
				<DashboardKpiCard label="Nye personer" value={summary.nye} />
				<DashboardKpiCard label="Gjenopprettede" value={summary.gjenopprettede} />
				<DashboardKpiCard label="Feil totalt" value={summary.pdlFeil + summary.andreFeil} />
			</HGrid>
			{personTrendDataLength === 0 ? (
				<Alert variant="info" inline>
					Ingen persondata i valgt periode.
				</Alert>
			) : (
				<DashboardChartPanel options={personTrendChartOptions} ariaLabel="Personer per dag" />
			)}
		</VStack>
	</DashboardSectionCard>
)

export const MonthlyTeamTrendSection = ({
	filteredMonthlyTeamPointsLength,
	monthScope,
	onMonthScopeChange,
	monthlyTrendChartOptions,
}: {
	filteredMonthlyTeamPointsLength: number
	monthScope: typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL
	onMonthScopeChange: (value: typeof MONTH_SCOPE_LAST_12 | typeof MONTH_SCOPE_ALL) => void
	monthlyTrendChartOptions: Options
}) => (
	<DashboardSectionCard>
		<VStack gap="space-16">
			<Heading level="2" size="small">
				Team- og brukeranalyse over tid
			</Heading>
			<BodyShort>
				Visningen bruker månedsdata fra teams-endepunktet. Standardvisning er siste 12 måneder.
			</BodyShort>
			{filteredMonthlyTeamPointsLength === 0 ? (
				<Alert variant="info" inline>
					Ingen teamdata tilgjengelig.
				</Alert>
			) : (
				<>
					<VStack gap="space-8">
						<Label>Vis tidsrom</Label>
						<HStack gap="space-8" wrap>
							<Button
								variant="tertiary"
								size="small"
								onClick={() => onMonthScopeChange(MONTH_SCOPE_LAST_12)}
							>
								Siste 12 måneder
							</Button>
							<Button
								variant="tertiary"
								size="small"
								onClick={() => onMonthScopeChange(MONTH_SCOPE_ALL)}
							>
								All historikk
							</Button>
						</HStack>
					</VStack>
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
	monthlyIntervalOptions,
	selectedInterval,
	onSelectedIntervalChange,
	selectedMonthlyPoint,
	teamDistribution,
	monthlyDistributionChartOptions,
}: {
	monthlyIntervalOptions: { value: string; label: string }[]
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
				Teamfordeling i valgt måned
			</Heading>
			{monthlyIntervalOptions.length === 0 ? (
				<Alert variant="info" inline>
					Ingen teamfordeling tilgjengelig.
				</Alert>
			) : (
				<>
					<Select
						label="Måned for teamfordeling"
						value={selectedInterval}
						onChange={(event) => onSelectedIntervalChange(event.target.value)}
					>
						{monthlyIntervalOptions.map((option) => (
							<option key={option.value} value={option.value}>
								{option.label}
							</option>
						))}
					</Select>
					{selectedMonthlyPoint && (
						<HGrid columns={{ xs: 1, sm: 2 }} gap="space-12">
							<DashboardKpiCard
								label="Unike brukere totalt"
								value={selectedMonthlyPoint.totaltUnikeBrukere}
							/>
							<DashboardKpiCard
								label="Antall teams totalt"
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
