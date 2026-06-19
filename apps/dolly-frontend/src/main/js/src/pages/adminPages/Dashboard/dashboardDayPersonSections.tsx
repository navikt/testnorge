import { Alert, Box, Button, Heading, HGrid, Skeleton, TextField, VStack } from '@navikt/ds-react'
import { type Options } from 'highcharts'
import {
	DashboardChartPanel,
	DashboardKpiCard,
	DashboardSectionCard,
	DashboardSelectButtons,
} from './dashboardSharedComponents'
import { FeilGrupperVisning } from './dashboardFeilSection'
import { type FeilGruppe } from './dashboardFeilUtils'
import { createPreviousDayFeilDonutChartOptions } from './dashboardPreviousDayChartOptions'
import { type DashboardPersonerDTO } from '@/utils/hooks/useDashboard'
import dollyImg from '@/assets/img/dolly-sheep-confetti.svg'

export type QuickRangeOption = {
	value: string
	label: string
}

export type PersonSummary = {
	personerTotalt: number
	nye: number
	gjenopprettede: number
}

type PreviousDaySummary = {
	nye: number
	gjenopprettede: number
	nyeInklGjenopprettede: number
}

const PreviousDayFeilBlock = ({
	label,
	feilGrupper,
	feilDetaljertCount,
	isLoading,
}: {
	label: string
	feilGrupper: FeilGruppe[]
	feilDetaljertCount: number
	isLoading: boolean
}) => (
	<Box
		borderWidth="1"
		borderColor="neutral-subtle"
		borderRadius="8"
		padding="space-16"
		background="neutral-soft"
	>
		<VStack gap="space-12">
			<Heading level="3" size="xsmall">
				Feil registrert {label}
			</Heading>
			{isLoading ? (
				<Skeleton variant="rectangle" height="120px" />
			) : feilGrupper.length === 0 ? (
				<Alert variant="success" inline>
					Ingen feil registrert for valgt periode.
				</Alert>
			) : (
				<FeilGrupperVisning feilGrupper={feilGrupper} feilDetaljertCount={feilDetaljertCount} />
			)}
		</VStack>
	</Box>
)

export const PreviousDaySection = ({
	selectedDayDisplayLabel,
	selectedDayPeriodTitle,
	selectedDayButtonLabel,
	selectedDayScope,
	onSelectedDayScopeChange,
	previousDayPeriodData,
	previousDaySummary,
	previousDayChartOptions,
	selectedDayFeilGrupper,
	selectedDayFeilCount,
	loadingSelectedDayFeil,
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
	selectedDayFeilGrupper: FeilGruppe[]
	selectedDayFeilCount: number
	loadingSelectedDayFeil: boolean
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
			<DashboardSelectButtons
				selected={selectedDayScope}
				onSelect={(value) => onSelectedDayScopeChange(value as 'YESTERDAY' | 'TODAY')}
				options={[
					{ value: 'YESTERDAY', label: selectedDayButtonLabel },
					{ value: 'TODAY', label: 'I dag' },
				]}
			/>
			{isLoading ? (
				<VStack gap="space-12">
					<Skeleton variant="rectangle" height="80px" />
					<Skeleton variant="rectangle" height="320px" />
				</VStack>
			) : previousDayPeriodData.length === 0 ? (
				<Alert variant="info" inline>
					Ingen data tilgjengelig for valgt dag.
				</Alert>
			) : (
				<HGrid columns={{ xs: 1, sm: 2 }} gap="space-16">
					<VStack gap="space-16">
						<DashboardKpiCard
							label="Personer opprettet/gjenopprettet"
							value={previousDaySummary.nyeInklGjenopprettede}
						/>
						<DashboardChartPanel
							options={previousDayChartOptions}
							ariaLabel="Opprettet og gjenopprettet for valgt dag"
						/>
					</VStack>
					{selectedDayFeilGrupper.length > 0 ? (
						<DashboardChartPanel
							options={createPreviousDayFeilDonutChartOptions(selectedDayFeilGrupper)}
							ariaLabel="Feilfordeling per fagsystem"
						/>
					) : (
						<Box
							padding="space-16"
							background="neutral-soft"
							borderRadius="8"
							borderWidth="1"
							borderColor="neutral-subtle"
							asChild
						>
							<div
								style={{
									display: 'flex',
									flexDirection: 'column',
									justifyContent: 'center',
									alignItems: 'center',
									minHeight: '320px',
									gap: 'var(--ax-space-16)',
								}}
							>
								<img
									src={dollyImg}
									alt="Ingen feil observert"
									style={{
										width: '280px',
										height: '280px',
										filter: 'drop-shadow(0 8px 16px rgba(0, 0, 0, 0.12))',
									}}
								/>
								<VStack gap="space-4" align="center">
									<Heading level="3" size="small">
										Ingen feil observert, hurra!
									</Heading>
									<Heading level="3" size="xsmall">
										Dette burde feires med kake!
									</Heading>
								</VStack>
							</div>
						</Box>
					)}
				</HGrid>
			)}
			<PreviousDayFeilBlock
				label={selectedDayDisplayLabel}
				feilGrupper={selectedDayFeilGrupper}
				feilDetaljertCount={selectedDayFeilCount}
				isLoading={loadingSelectedDayFeil}
			/>
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
			<DashboardSelectButtons
				label="Velg periode"
				selected={selectedQuickRange}
				onSelect={onQuickRangeClick}
				options={quickRangeOptions}
			/>
			<HGrid columns={{ xs: 1, sm: 2, lg: 5 }} gap="space-16" align="end">
				<TextField
					label="Fra dato"
					type="date"
					max={tilDatoMax}
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
			<HGrid columns={{ xs: 1, sm: 2, lg: 4 }} gap="space-12">
				<DashboardKpiCard label="Dager i periode" value={filteredPersonerLength} />
				<DashboardKpiCard label="Personer totalt" value={summary.personerTotalt} />
				<DashboardKpiCard label="Nye personer" value={summary.nye} />
				<DashboardKpiCard label="Gjenopprettede" value={summary.gjenopprettede} />
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
