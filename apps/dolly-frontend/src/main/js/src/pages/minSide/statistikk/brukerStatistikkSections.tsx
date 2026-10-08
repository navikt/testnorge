import { Alert, BodyShort, Heading, HGrid, VStack } from '@navikt/ds-react'
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
	maanedVisning,
	type MaanedPunkt,
	type MonthScope,
} from './brukerStatistikkUtils'

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
	valgtPeriodeVisning: string
	mestBrukteFagsystem: string | null
	antallFagsystemer: number
	isLoadingDetaljert: boolean
	detaljerUtilgjengelige: boolean
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
	valgtPeriodeVisning,
	mestBrukteFagsystem,
	antallFagsystemer,
	isLoadingDetaljert,
	detaljerUtilgjengelige,
}: PeriodeSectionProps) => {
	const skjulDetaljverdier = isLoadingDetaljert || detaljerUtilgjengelige

	return (
		<DashboardSectionCard>
			<VStack gap="space-24">
				<Heading level="2" size="small">
					Bestillinger
				</Heading>
				<VStack gap="space-16">
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
							<HGrid columns={{ xs: 1, sm: 2, lg: 5 }} gap="space-12">
								<DashboardKpiCard label="Nye personer" value={aarSammendrag.nyePersoner} />
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
				<VStack gap="space-16">
					<DashboardSelectButtons
						label="Måned"
						selected={valgtPeriodeKey}
						onSelect={onPeriodeChange}
						options={maanedOptions}
					/>
					<VStack gap="space-8">
						<Heading level="3" size="xsmall">
							{valgtPeriodeVisning}
						</Heading>
						<HGrid columns={{ xs: 1, sm: 2, lg: 5 }} gap="space-12">
							<DashboardKpiCard label="Nye personer" value={valgtPunkt.nyePersoner} />
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
			</VStack>
		</DashboardSectionCard>
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
	<DashboardSectionCard>
		<VStack gap="space-16">
			<Heading level="2" size="small">
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
	</DashboardSectionCard>
)

interface FagsystemDetaljerSectionProps {
	fagsystemOptions: DashboardSelectOption[]
	valgtFagsystem: string | null
	onFagsystemChange: (fagsystem: string) => void
	valgtFagsystemLabel: string
	valgChartOptions: Options | null
	antallChartOptions: Options | null
	isLoading: boolean
}

export const FagsystemDetaljerSection = ({
	fagsystemOptions,
	valgtFagsystem,
	onFagsystemChange,
	valgtFagsystemLabel,
	valgChartOptions,
	antallChartOptions,
	isLoading,
}: FagsystemDetaljerSectionProps) => (
	<DashboardSectionCard>
		<VStack gap="space-16">
			<Heading level="2" size="small">
				Detaljer per fagsystem
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
							<Heading level="3" size="xsmall">
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
							<Heading level="3" size="xsmall">
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
	</DashboardSectionCard>
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
