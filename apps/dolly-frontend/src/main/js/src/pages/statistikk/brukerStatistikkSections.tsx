import { Alert, BodyShort, Box, Heading, HGrid, VStack } from '@navikt/ds-react'
import { type ReactNode, useId } from 'react'
import { type Options } from 'highcharts'
import DollySpinner from '@/components/ui/loading/DollySpinner'
import {
	DashboardChartPanel,
	DashboardKpiCard,
	DashboardSectionCard,
	DashboardSelectButtons,
	type DashboardSelectOption,
} from '@/pages/adminPages/Dashboard/dashboardSharedComponents'
import { MONTH_SCOPE_ALL, MONTH_SCOPE_LAST_12 } from '@/pages/adminPages/Dashboard/dashboardUtils'
import {
	type AarSammendrag,
	harPersonFordeling,
	maanedVisning,
	type MaanedPunkt,
	type MonthScope,
	type PeriodeVisning,
	type PersonFordeling,
} from './brukerStatistikkUtils'
import { PeriodeVelger } from './PeriodeVelger'

const PersonKpiRad = ({ fordeling }: { fordeling: PersonFordeling & { nyePersoner: number } }) => (
	<HGrid columns={{ xs: 1, sm: 3 }} gap="space-12">
		<DashboardKpiCard label="Nye personer" value={fordeling.nyePersoner} />
		{harPersonFordeling(fordeling) && (
			<>
				<DashboardKpiCard label="Opprettet i Dolly" value={fordeling.dollyPersoner} />
				<DashboardKpiCard label="Importert fra Testnorge" value={fordeling.testnorgePersoner} />
			</>
		)}
	</HGrid>
)

const PanelDel = ({ children }: { children: ReactNode }) => (
	<Box
		borderWidth="1 0 0 0"
		borderColor="neutral-subtle"
		paddingBlock="space-24 space-0"
		minWidth="0"
	>
		{children}
	</Box>
)

interface PeriodeSectionProps {
	aarOptions: string[]
	valgtAar: string
	onAarChange: (aar: string) => void
	aarSammendrag: AarSammendrag | null
	maanedOptions: DashboardSelectOption[]
	valgtPeriodeKey: string
	onPeriodeChange: (periodeKey: string) => void
	valgtPunkt: MaanedPunkt
	visning: PeriodeVisning
	onVisningChange: (visning: PeriodeVisning) => void
	mestBrukteFagsystem: string | null
	antallFagsystemer: number
	isLoadingDetaljert: boolean
	detaljerFeil: Error | undefined
	children: ReactNode
}

export const PeriodeSection = ({
	aarOptions,
	valgtAar,
	onAarChange,
	aarSammendrag,
	maanedOptions,
	valgtPeriodeKey,
	onPeriodeChange,
	valgtPunkt,
	visning,
	onVisningChange,
	mestBrukteFagsystem,
	antallFagsystemer,
	isLoadingDetaljert,
	detaljerFeil,
	children,
}: PeriodeSectionProps) => {
	const headingId = useId()
	const skjulDetaljverdier = isLoadingDetaljert || Boolean(detaljerFeil)

	return (
		<Box
			as="section"
			aria-labelledby={headingId}
			background="default"
			borderRadius="12"
			borderWidth="1"
			borderColor="neutral-subtle"
			padding={{ xs: 'space-16', md: 'space-24' }}
			minWidth="0"
		>
			<VStack gap="space-24">
				<VStack gap="space-16">
					<Heading id={headingId} level="2" size="medium">
						Bestillinger per år
					</Heading>
					<PeriodeVelger
						aarOptions={aarOptions}
						valgtAar={valgtAar}
						onAarChange={onAarChange}
						maanedOptions={maanedOptions}
						valgtMaaned={valgtPeriodeKey}
						onMaanedChange={onPeriodeChange}
						visning={visning}
						onVisningChange={onVisningChange}
					/>
					{detaljerFeil && (
						<Alert variant="error">
							Klarte ikke å hente detaljer for {visning === 'aar' ? 'valgt år' : 'valgt måned'}:{' '}
							{detaljerFeil.message}
						</Alert>
					)}
					{visning === 'aar' && aarSammendrag ? (
						<VStack gap="space-8">
							<HGrid columns={{ xs: 1, sm: 2, lg: 4 }} gap="space-12">
								<DashboardKpiCard label="Nye bestillinger" value={aarSammendrag.nyeBestillinger} />
								<DashboardKpiCard label="Gjenopprettinger" value={aarSammendrag.gjenopprettinger} />
								<DashboardKpiCard label="Aktive måneder" value={aarSammendrag.aktiveMaaneder} />
								<DashboardKpiCard
									label="Mest aktive måned"
									value={
										aarSammendrag.mestAktiveMaaned
											? maanedVisning(aarSammendrag.mestAktiveMaaned)
											: 'Ingen'
									}
								/>
							</HGrid>
							<PersonKpiRad fordeling={aarSammendrag} />
						</VStack>
					) : (
						<VStack gap="space-8">
							<HGrid columns={{ xs: 1, sm: 2, lg: 4 }} gap="space-12">
								<DashboardKpiCard label="Nye bestillinger" value={valgtPunkt.nyeBestillinger} />
								<DashboardKpiCard label="Gjenopprettinger" value={valgtPunkt.gjenopprettinger} />
								<DashboardKpiCard
									label="Fagsystem brukt"
									value={skjulDetaljverdier ? '–' : antallFagsystemer}
								/>
								<DashboardKpiCard
									label="Mest brukte fagsystem"
									value={skjulDetaljverdier ? '–' : (mestBrukteFagsystem ?? 'Ingen')}
								/>
							</HGrid>
							<PersonKpiRad fordeling={valgtPunkt} />
						</VStack>
					)}
				</VStack>
				{children}
			</VStack>
		</Box>
	)
}

interface ChartSectionProps {
	title: string
	description?: string
	ariaLabel: string
	emptyStateMessage: string
	harData: boolean
	isLoading: boolean
	chartOptions: Options
}

export const ChartSection = ({
	title,
	description,
	ariaLabel,
	emptyStateMessage,
	harData,
	isLoading,
	chartOptions,
}: ChartSectionProps) => (
	<PanelDel>
		<VStack gap="space-16">
			<Heading level="3" size="small">
				{title}
			</Heading>
			{description && <BodyShort>{description}</BodyShort>}
			{isLoading ? (
				<DollySpinner size={120} label="Laster statistikk..." />
			) : !harData ? (
				<Alert variant="info" inline>
					{emptyStateMessage}
				</Alert>
			) : (
				<DashboardChartPanel options={chartOptions} ariaLabel={ariaLabel} />
			)}
		</VStack>
	</PanelDel>
)

interface FagsystemDetaljerSectionProps {
	title: string
	fagsystemOptions: DashboardSelectOption[]
	valgtFagsystem: string | null
	onFagsystemChange: (fagsystem: string) => void
	valgtFagsystemLabel: string
	valgChartOptions: Options | null
	antallChartOptions: Options | null
	isLoading: boolean
}

export const FagsystemDetaljerSection = ({
	title,
	fagsystemOptions,
	valgtFagsystem,
	onFagsystemChange,
	valgtFagsystemLabel,
	valgChartOptions,
	antallChartOptions,
	isLoading,
}: FagsystemDetaljerSectionProps) => (
	<PanelDel>
		<VStack gap="space-16">
			<Heading level="3" size="small">
				{title}
			</Heading>
			<BodyShort>
				Velg et fagsystem for å se hva som ble bestilt. «Personer» er antall personer med valget.
				«Antall oppføringer» er summen av oppføringer, for eksempel flere arbeidsforhold per person.
			</BodyShort>
			{isLoading ? (
				<DollySpinner size={120} label="Laster statistikk..." />
			) : fagsystemOptions.length === 0 ? (
				<Alert variant="info" inline>
					Ingen detaljer tilgjengelig for valgt måned.
				</Alert>
			) : (
				<>
					<DashboardSelectButtons
						label="Fagsystem"
						selected={valgtFagsystem}
						onSelect={onFagsystemChange}
						options={fagsystemOptions}
					/>
					{valgChartOptions && (
						<VStack gap="space-8">
							<Heading level="4" size="xsmall">
								Valg for {valgtFagsystemLabel}
							</Heading>
							<DashboardChartPanel
								options={valgChartOptions}
								ariaLabel={`Valg for ${valgtFagsystemLabel}`}
							/>
						</VStack>
					)}
					{antallChartOptions && (
						<VStack gap="space-8">
							<Heading level="4" size="xsmall">
								Oppføringer for {valgtFagsystemLabel}
							</Heading>
							<DashboardChartPanel
								options={antallChartOptions}
								ariaLabel={`Oppføringer for ${valgtFagsystemLabel}`}
							/>
						</VStack>
					)}
				</>
			)}
		</VStack>
	</PanelDel>
)

interface TrendSectionProps {
	monthScope: MonthScope
	onMonthScopeChange: (scope: MonthScope) => void
	chartOptions: Options
}

export const TrendSection = ({
	monthScope,
	onMonthScopeChange,
	chartOptions,
}: TrendSectionProps) => (
	<DashboardSectionCard>
		<VStack gap="space-16">
			<Heading level="2" size="medium">
				Utvikling over tid
			</Heading>
			<DashboardSelectButtons
				label="Vis tidsrom"
				selected={monthScope}
				onSelect={(value) => onMonthScopeChange(value as MonthScope)}
				options={[
					{ value: MONTH_SCOPE_LAST_12, label: 'Siste 12 måneder' },
					{ value: MONTH_SCOPE_ALL, label: 'All historikk' },
				]}
			/>
			<DashboardChartPanel options={chartOptions} ariaLabel="Månedlig utvikling i bestillinger" />
		</VStack>
	</DashboardSectionCard>
)
