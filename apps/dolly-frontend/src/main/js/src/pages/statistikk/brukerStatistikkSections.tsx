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

interface AarSectionProps {
	aarOptions: string[]
	valgtAar: string
	onAarChange: (aar: string) => void
	aarSammendrag: AarSammendrag | null
	aarSammendragTittel: string
	children: ReactNode
}

export const AarSection = ({
	aarOptions,
	valgtAar,
	onAarChange,
	aarSammendrag,
	aarSammendragTittel,
	children,
}: AarSectionProps) => {
	const headingId = useId()

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
					<VStack gap="space-8">
						<Heading id={headingId} level="2" size="small">
							Bestillinger per år
						</Heading>
						<BodyShort size="small" textColor="subtle">
							Året du velger styrer månedene under.
						</BodyShort>
					</VStack>
					<DashboardSelectButtons
						label="År"
						selected={valgtAar}
						onSelect={onAarChange}
						options={aarOptions.map((aar) => ({ value: aar, label: aar }))}
					/>
					{aarSammendrag && (
						<VStack gap="space-8">
							<Heading level="3" size="xsmall">
								{aarSammendragTittel}
							</Heading>
							<PersonKpiRad fordeling={aarSammendrag} />
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
						</VStack>
					)}
				</VStack>
				{children}
			</VStack>
		</Box>
	)
}

interface MaanedSectionProps {
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

export const MaanedSection = ({
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
}: MaanedSectionProps) => {
	const headingId = useId()
	const skjulDetaljverdier = isLoadingDetaljert || Boolean(detaljerFeil)

	return (
		<Box
			as="section"
			aria-labelledby={headingId}
			borderWidth="1 0 0 0"
			borderColor="neutral-subtle"
			paddingBlock="space-24 space-0"
			minWidth="0"
		>
			<VStack gap="space-24">
				<VStack gap="space-16">
					<VStack gap="space-8">
						<Heading id={headingId} level="3" size="small">
							Bestillinger i {valgtMaanedTekst}
						</Heading>
						<BodyShort size="small" textColor="subtle">
							Tallene og grafene under gjelder måneden du velger.
						</BodyShort>
						<DashboardSelectButtons
							label="Måned"
							selected={valgtPeriodeKey}
							onSelect={onPeriodeChange}
							options={maanedOptions}
							size="xsmall"
							gap="space-4"
						/>
					</VStack>
					{detaljerFeil && (
						<Alert variant="error">
							Klarte ikke å hente detaljer for valgt måned: {detaljerFeil.message}
						</Alert>
					)}
					<VStack gap="space-8">
						<PersonKpiRad fordeling={valgtPunkt} />
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
					</VStack>
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
			<Heading level="4" size="xsmall">
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
			<Heading level="4" size="xsmall">
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
							<Heading level="5" size="xsmall">
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
							<Heading level="5" size="xsmall">
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
