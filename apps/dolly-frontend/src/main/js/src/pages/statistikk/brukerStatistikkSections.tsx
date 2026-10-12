import { Alert, BodyShort, Box, Heading, HGrid, ToggleGroup, VStack } from '@navikt/ds-react'
import { type ReactNode, useId, useState } from 'react'
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
	type PersonFordeling,
} from './brukerStatistikkUtils'

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

type NoekkeltallVisning = 'aar' | 'maaned'

interface PeriodeSectionProps {
	aarOptions: string[]
	valgtAar: string
	onAarChange: (aar: string) => void
	aarSammendrag: AarSammendrag | null
	aarSammendragTittel: string
	maanedOptions: DashboardSelectOption[]
	valgtPeriodeKey: string
	onPeriodeChange: (periodeKey: string) => void
	valgtPunkt: MaanedPunkt
	valgtMaanedTekst: string
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
	aarSammendragTittel,
	maanedOptions,
	valgtPeriodeKey,
	onPeriodeChange,
	valgtPunkt,
	valgtMaanedTekst,
	mestBrukteFagsystem,
	antallFagsystemer,
	isLoadingDetaljert,
	detaljerFeil,
	children,
}: PeriodeSectionProps) => {
	const headingId = useId()
	const [noekkeltallVisning, setNoekkeltallVisning] = useState<NoekkeltallVisning>('maaned')
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
					<Heading id={headingId} level="2" size="small">
						Bestillinger per år
					</Heading>
					<VStack gap="space-8">
						<DashboardSelectButtons
							label="År"
							selected={valgtAar}
							onSelect={onAarChange}
							options={aarOptions.map((aar) => ({ value: aar, label: aar }))}
						/>
						<DashboardSelectButtons
							label="Måned"
							selected={valgtPeriodeKey}
							onSelect={onPeriodeChange}
							options={maanedOptions}
						/>
					</VStack>
					{detaljerFeil && (
						<Alert variant="error">
							Klarte ikke å hente detaljer for valgt måned: {detaljerFeil.message}
						</Alert>
					)}
					<Box maxWidth="20rem">
						<ToggleGroup
							label="Vis nøkkeltall for"
							size="small"
							fill
							value={noekkeltallVisning}
							onChange={(verdi) => setNoekkeltallVisning(verdi as NoekkeltallVisning)}
						>
							<ToggleGroup.Item value="maaned" label={valgtMaanedTekst} />
							<ToggleGroup.Item value="aar" label={aarSammendragTittel} />
						</ToggleGroup>
					</Box>
					{noekkeltallVisning === 'aar' && aarSammendrag ? (
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
			<Heading level="3" size="xsmall">
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
			<Heading level="3" size="xsmall">
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
			<Heading level="2" size="small">
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
