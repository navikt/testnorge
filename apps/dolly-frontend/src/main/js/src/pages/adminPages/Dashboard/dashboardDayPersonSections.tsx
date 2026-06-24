import { Alert, Box, Heading, HGrid, Skeleton, TextField, VStack } from '@navikt/ds-react'
import DollySpinner from '@/components/ui/loading/DollySpinner'
import { type Options } from 'highcharts'
import {
	DashboardChartPanel,
	DashboardKpiCard,
	DashboardSectionCard,
	DashboardSelectButtons,
} from './dashboardSharedComponents'
import { FeilGrupperVisning } from './dashboardFeilSection'
import { type FeilGruppe } from './dashboardFeilUtils'
import { createFeilPerFagsystemChartOptions } from './dashboardChartOptions'
import { type DashboardBestillingerDTO } from '@/utils/hooks/useDashboard'
import { useDashboardPerson } from './DashboardPersonContext'
import { QUICK_RANGE_OPTIONS } from './dashboardUtils'
import dollyImg from '@/assets/img/dolly-sheep-confetti.svg'

export type PersonSummary = {
	bestillinger: number
	personerTotalt: number
	nye: number
	gjenopprettede: number
	navIdenter: number
	testnorgeIdenter: number
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
	previousDayPeriodData: DashboardBestillingerDTO[]
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
				<DollySpinner size={120} label="Laster statistikk..." />
			) : previousDayPeriodData.length === 0 ? (
				<Alert variant="info" inline>
					Ingen data tilgjengelig for valgt dag.
				</Alert>
			) : (
				<HGrid columns={{ xs: 1, sm: 2 }} gap="space-16" align="stretch">
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
					<Box style={{ display: 'flex', flexDirection: 'column', minHeight: 0, height: '100%' }}>
						<VStack gap="space-16" style={{ height: '100%', minHeight: 0 }}>
							<DashboardKpiCard label="Feil totalt" value={selectedDayFeilCount} />
							{selectedDayFeilGrupper.length > 0 ? (
								<Box style={{ flex: 1, minHeight: 0 }}>
									<DashboardChartPanel
										options={createFeilPerFagsystemChartOptions(selectedDayFeilGrupper)}
										ariaLabel="Feil per fagsystem"
										stretchHeight
									/>
								</Box>
							) : (
								<Box
									padding="space-16"
									background="neutral-soft"
									borderRadius="8"
									borderWidth="1"
									borderColor="neutral-subtle"
									asChild
									style={{ flex: 1, minHeight: 0 }}
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
						</VStack>
					</Box>
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

export const PersonAnalysisSection = ({ isLoading = false }: { isLoading?: boolean } = {}) => {
	const {
		selectedQuickRange,
		fraDato,
		tilDato,
		todayDate,
		onFraDatoChange,
		onTilDatoChange,
		onQuickRangeClick,
		filteredPersonerLength,
		summary,
		personTrendDataLength,
		personTrendChartOptions,
		identerTotal,
		identerDonutChartOptions,
		nyeGjenopprettedeTotal,
		nyeGjenopprettedeDonutChartOptions,
	} = useDashboardPerson()

	return (
		<DashboardSectionCard>
			<VStack gap="space-16">
				<Heading level="2" size="small">
					Statistikk for perioder
				</Heading>
				{isLoading ? (
					<DollySpinner size={120} label="Laster statistikk..." />
				) : (
					<>
						<DashboardSelectButtons
							label="Velg periode"
							selected={selectedQuickRange}
							onSelect={onQuickRangeClick}
							options={QUICK_RANGE_OPTIONS}
						/>
						<HGrid columns={{ xs: 1, sm: 2, lg: 5 }} gap="space-16" align="end">
							<TextField
								label="Fra dato"
								type="date"
								max={todayDate}
								value={fraDato}
								onChange={(event) => onFraDatoChange(event.target.value)}
							/>
							<TextField
								label="Til dato"
								type="date"
								value={tilDato}
								max={todayDate}
								onChange={(event) => onTilDatoChange(event.target.value)}
							/>
						</HGrid>
						<HGrid columns={{ xs: 1, sm: 2, lg: 4 }} gap="space-12">
							<DashboardKpiCard label="Dager i periode" value={filteredPersonerLength} />
							<DashboardKpiCard label="Personer totalt" value={summary.personerTotalt} />
							<DashboardKpiCard label="Nye personer" value={summary.nye} />
							<DashboardKpiCard label="Gjenopprettede" value={summary.gjenopprettede} />
						</HGrid>
						<HGrid columns={{ xs: 1, sm: 2, lg: 3 }} gap="space-12">
							<DashboardKpiCard label="Bestillinger" value={summary.bestillinger} />
							<DashboardKpiCard label="NAV-identer" value={summary.navIdenter} />
							<DashboardKpiCard label="Testnorge-identer" value={summary.testnorgeIdenter} />
						</HGrid>
						{personTrendDataLength === 0 ? (
							<Alert variant="info" inline>
								Ingen persondata i valgt periode.
							</Alert>
						) : (
							<VStack gap="space-16">
								<HGrid
									columns={{ xs: '1fr', md: 'repeat(auto-fit, minmax(340px, 1fr))' }}
									gap="space-16"
									align="start"
								>
									<VStack gap="space-8">
										{identerTotal === 0 ? (
											<Alert variant="info" inline>
												Ingen identer i valgt periode.
											</Alert>
										) : (
											<DashboardChartPanel
												options={identerDonutChartOptions}
												ariaLabel="Fordeling av NAV-identer og Testnorge-identer"
											/>
										)}
									</VStack>
									<VStack gap="space-8">
										{nyeGjenopprettedeTotal === 0 ? (
											<Alert variant="info" inline>
												Ingen nye eller gjenopprettede personer i valgt periode.
											</Alert>
										) : (
											<DashboardChartPanel
												options={nyeGjenopprettedeDonutChartOptions}
												ariaLabel="Fordeling av nye og gjenopprettede personer"
											/>
										)}
									</VStack>
								</HGrid>
								<VStack gap="space-8">
									<Heading level="3" size="xsmall">
										Personer per dag
									</Heading>
									<DashboardChartPanel
										options={personTrendChartOptions}
										ariaLabel="Personer per dag"
									/>
								</VStack>
							</VStack>
						)}
					</>
				)}
			</VStack>
		</DashboardSectionCard>
	)
}
