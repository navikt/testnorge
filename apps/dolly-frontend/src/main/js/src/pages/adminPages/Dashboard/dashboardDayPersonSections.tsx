import {
	Alert,
	BodyShort,
	Box,
	Button,
	Heading,
	HGrid,
	HStack,
	Label,
	Skeleton,
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
import zeroFeilIllustrasjon from '@/assets/img/dolly-sheep-confetti.svg'

export type QuickRangeOption = {
	value: string
	label: string
}

export type PersonSummary = {
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
	selectedDayDisplayLabel,
	selectedDayPeriodTitle,
	selectedDayButtonLabel,
	selectedDayScope,
	onSelectedDayScopeChange,
	previousDayPeriodData,
	previousDaySummary,
	previousDayChartOptions,
	previousDayErrorBreakdownChartOptions,
	isLoading = false,
}: {
	selectedDayDisplayLabel: string
	selectedDayPeriodTitle: string
	selectedDayButtonLabel: string
	selectedDayScope: 'YESTERDAY' | 'TODAY'
	onSelectedDayScopeChange: (scope: 'YESTERDAY' | 'TODAY') => void
	previousDayPeriodData: DashboardPersonerDTO[]
	previousDaySummary: PreviousDaySummary
	previousDayChartOptions: Options
	previousDayErrorBreakdownChartOptions: Options
	isLoading?: boolean
}) => (
	<DashboardSectionCard>
		<VStack gap="space-16">
			<Heading level="2" size="small">
				{selectedDayScope === 'TODAY'
					? 'Statistikk for i dag'
					: `Statistikk for ${selectedDayPeriodTitle.toLowerCase()}`}{' '}
				({selectedDayDisplayLabel})
			</Heading>
			<HStack gap="space-8" wrap>
				<Button
					variant={selectedDayScope === 'YESTERDAY' ? 'secondary' : 'tertiary'}
					size="small"
					onClick={() => onSelectedDayScopeChange('YESTERDAY')}
				>
					{selectedDayButtonLabel}
				</Button>
				<Button
					variant={selectedDayScope === 'TODAY' ? 'secondary' : 'tertiary'}
					size="small"
					onClick={() => onSelectedDayScopeChange('TODAY')}
				>
					I dag
				</Button>
			</HStack>
			{isLoading ? (
				<VStack gap="space-12">
					<HGrid columns={{ xs: 1, sm: 2 }} gap="space-12">
						<Skeleton variant="rectangle" height="80px" />
						<Skeleton variant="rectangle" height="80px" />
					</HGrid>
					<Skeleton variant="rectangle" height="320px" />
				</VStack>
			) : previousDayPeriodData.length === 0 ? (
				<Alert variant="info" inline>
					Ingen data tilgjengelig for valgt dag.
				</Alert>
			) : (
				<>
					<HGrid columns={{ xs: 1, sm: 2 }} gap="space-12">
						<DashboardKpiCard
							label="Identer opprettet/gjenopprettet"
							value={previousDaySummary.nyeInklGjenopprettede}
						/>
						{previousDaySummary.totaltFeil === 0 ? (
							<DashboardKpiCard
								label="Antall feil"
								value={
									<BodyShort as="p" style={{ textAlign: 'center' }}>
										Ingen feil observert, hurra!
										<br />
										Dette burde feires med kake!
									</BodyShort>
								}
							/>
						) : (
							<DashboardKpiCard label="Antall feil" value={previousDaySummary.totaltFeil} />
						)}
					</HGrid>
					<HGrid columns={{ xs: 1, lg: 2 }} gap="space-16">
						<DashboardChartPanel
							options={previousDayChartOptions}
							ariaLabel="Opprettet og gjenopprettet for valgt dag"
						/>
						{previousDaySummary.totaltFeil === 0 ? (
							<Box
								padding="space-16"
								borderRadius="8"
								minHeight="320px"
								style={{
									display: 'flex',
									alignItems: 'center',
									justifyContent: 'center',
								}}
							>
								<img
									alt="Ingen feil registrert for valgt dag"
									src={zeroFeilIllustrasjon}
									style={{
										display: 'block',
										width: '264px',
										height: '264px',
										borderRadius: '50%',
										boxShadow:
											'0 18px 40px rgba(12, 22, 39, 0.24), 0 6px 16px rgba(12, 22, 39, 0.16)',
									}}
								/>
							</Box>
						) : (
							<DashboardChartPanel
								options={previousDayErrorBreakdownChartOptions}
								ariaLabel="Feilfordeling per system for valgt dag"
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
	selectedQuickRange,
	fraDato,
	tilDato,
	tilDatoMax,
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
	selectedQuickRange: string | null
	fraDato: string
	tilDato: string
	tilDatoMax: string
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
				Statistikk for perioder
			</Heading>
			<VStack gap="space-8">
				<Label>Velg periode</Label>
				<HStack gap="space-8" wrap>
					{quickRangeOptions.map((option) => (
						<Button
							key={option.value}
							variant={selectedQuickRange === option.value ? 'secondary' : 'tertiary'}
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
					max={tilDatoMax}
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
