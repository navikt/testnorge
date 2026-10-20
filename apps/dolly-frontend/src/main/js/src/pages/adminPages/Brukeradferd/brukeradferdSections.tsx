import {
	Alert,
	BodyShort,
	Box,
	Button,
	Detail,
	Heading,
	HGrid,
	HStack,
	ReadMore,
	Table,
	VStack,
} from '@navikt/ds-react'
import { type Options } from 'highcharts'
import { type ReactNode, useEffect, useId, useRef } from 'react'
import DollySpinner from '@/components/ui/loading/DollySpinner'
import {
	DashboardChartPanel,
	DashboardKpiCard,
	DashboardSelectButtons,
	type DashboardSelectOption,
} from '@/pages/adminPages/Dashboard/dashboardSharedComponents'
import {
	type AdferdGranularitet,
	type BrukeradferdNoekkeltall,
	type FagsystemTotal,
	erForkortetLabel,
	fagsystemKortLabel,
	formaterAndel,
	type PersonNoekkeltall,
	type PersonTidPunkt,
} from './brukeradferdUtils'
import { type PeriodeVisning } from '@/pages/statistikk/brukerStatistikkUtils'
import { PeriodeVelger } from '@/pages/statistikk/PeriodeVelger'
import { FAGSYSTEM_DETALJER_ID } from './useBrukeradferdData'

const formaterTall = (verdi: number) => verdi.toLocaleString('nb-NO')

const FagsystemKortNavn = ({ total }: { total: FagsystemTotal }) =>
	erForkortetLabel(total.fagsystem) ? (
		<abbr title={total.label}>{fagsystemKortLabel(total.fagsystem)}</abbr>
	) : (
		fagsystemKortLabel(total.fagsystem)
	)

const TomPeriode = ({
	melding = 'Ingen bestillinger med fagsystemvalg i valgt periode.',
}: {
	melding?: string
}) => (
	<Alert variant="info" inline>
		{melding}
	</Alert>
)

const LasterStatistikk = () => <DollySpinner size={120} label="Laster statistikk..." />

const iPeriode = (tittel: string, periodeTittel: string) =>
	periodeTittel ? `${tittel} ${periodeTittel}` : tittel

const TallKpi = ({
	label,
	verdi,
	laster = false,
}: {
	label: string
	verdi: number
	laster?: boolean
}) => (
	<DashboardKpiCard
		label={label}
		value={
			laster ? (
				'–'
			) : (
				<Heading as="p" size="large">
					{formaterTall(verdi)}
				</Heading>
			)
		}
	/>
)

interface PanelDelProps {
	tittel: string
	beskrivelse?: ReactNode
	handling?: ReactNode
	children: ReactNode
}

const PanelDel = ({ tittel, beskrivelse, handling, children }: PanelDelProps) => {
	const headingId = useId()
	return (
		<Box
			as="section"
			aria-labelledby={headingId}
			borderWidth="1 0 0 0"
			borderColor="neutral-subtle"
			paddingBlock="space-24 space-0"
			minWidth="0"
		>
			<VStack gap="space-16">
				<HStack justify="space-between" align="center" wrap gap="space-8">
					<Heading id={headingId} level="3" size="small">
						{tittel}
					</Heading>
					{handling}
				</HStack>
				{beskrivelse && <BodyShort>{beskrivelse}</BodyShort>}
				{children}
			</VStack>
		</Box>
	)
}

interface BrukeradferdPanelProps {
	yearOptions: string[]
	selectedYear: string
	onYearChange: (year: string) => void
	monthOptions: DashboardSelectOption[]
	selectedInterval: string
	onIntervalChange: (interval: string) => void
	visning: PeriodeVisning
	onVisningChange: (visning: PeriodeVisning) => void
	noekkeltall: BrukeradferdNoekkeltall
	personNoekkeltall: PersonNoekkeltall
	isLoading?: boolean
	isLoadingNoekkeltall?: boolean
	children: ReactNode
}

export const BrukeradferdPanel = ({
	yearOptions,
	selectedYear,
	onYearChange,
	monthOptions,
	selectedInterval,
	onIntervalChange,
	visning,
	onVisningChange,
	noekkeltall,
	personNoekkeltall,
	isLoading = false,
	isLoadingNoekkeltall = false,
	children,
}: BrukeradferdPanelProps) => {
	const headingId = useId()
	const harPerioder = yearOptions.length > 0

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
						Bestillinger per periode
					</Heading>
					{isLoading ? (
						<LasterStatistikk />
					) : !harPerioder ? (
						<Alert variant="info" inline>
							Ingen perioder tilgjengelig.
						</Alert>
					) : (
						<>
							<PeriodeVelger
								aarOptions={yearOptions}
								valgtAar={selectedYear}
								onAarChange={onYearChange}
								maanedOptions={monthOptions}
								valgtMaaned={selectedInterval}
								onMaanedChange={onIntervalChange}
								visning={visning}
								onVisningChange={onVisningChange}
							/>
							<BrukeradferdNoekkeltall
								noekkeltall={noekkeltall}
								personNoekkeltall={personNoekkeltall}
								laster={isLoadingNoekkeltall}
							/>
						</>
					)}
				</VStack>
				{!isLoading && harPerioder && children}
			</VStack>
		</Box>
	)
}

const PersonKpiRad = ({
	personer,
	ekstra,
	laster = false,
}: {
	personer: PersonNoekkeltall
	ekstra?: ReactNode
	laster?: boolean
}) => (
	<HGrid columns={ekstra ? { xs: 1, sm: 2, lg: 4 } : { xs: 1, sm: 3 }} gap="space-12">
		<TallKpi label="Nye personer" verdi={personer.nyePersoner} laster={laster} />
		<TallKpi label="Opprettet i Dolly" verdi={personer.dollyPersoner} laster={laster} />
		<TallKpi label="Importert fra Testnorge" verdi={personer.testnorgePersoner} laster={laster} />
		{ekstra}
	</HGrid>
)

const harPersontall = (personer: PersonNoekkeltall) =>
	personer.nyeBestillinger > 0 || personer.nyePersoner > 0

interface BrukeradferdNoekkeltallProps {
	noekkeltall: BrukeradferdNoekkeltall
	personNoekkeltall: PersonNoekkeltall
	laster: boolean
}

const BrukeradferdNoekkeltall = ({
	noekkeltall,
	personNoekkeltall,
	laster,
}: BrukeradferdNoekkeltallProps) => (
	<VStack gap="space-8" aria-busy={laster}>
		<HGrid columns={{ xs: 1, sm: 2, lg: 4 }} gap="space-12">
			<TallKpi label="Nye bestillinger" verdi={personNoekkeltall.nyeBestillinger} laster={laster} />
			<TallKpi
				label="Fagsystemvalg totalt"
				verdi={noekkeltall.totaltFagsystemvalg}
				laster={laster}
			/>
			<TallKpi label="Fagsystemer i bruk" verdi={noekkeltall.fagsystemerIBruk} laster={laster} />
			<DashboardKpiCard
				label="Mest brukte fagsystem"
				value={
					!laster && noekkeltall.mestBrukt ? (
						<VStack gap="space-4">
							<Heading as="p" size="medium" style={{ overflowWrap: 'anywhere' }}>
								<FagsystemKortNavn total={noekkeltall.mestBrukt} />
							</Heading>
							<Detail textColor="subtle">
								{formaterTall(noekkeltall.mestBrukt.antall)} valg (
								{formaterAndel(noekkeltall.mestBrukt.andel)})
							</Detail>
						</VStack>
					) : laster ? (
						'–'
					) : (
						'Ingen'
					)
				}
			/>
		</HGrid>
		<PersonKpiRad
			personer={personNoekkeltall}
			laster={laster}
			ekstra={
				<TallKpi
					label="Dager med bestillinger"
					verdi={noekkeltall.dagerMedBestillinger}
					laster={laster}
				/>
			}
		/>
	</VStack>
)

interface FagsystemTabellProps {
	totaler: FagsystemTotal[]
	caption: string
}

const FagsystemTabell = ({ totaler, caption }: FagsystemTabellProps) => (
	<Table size="small" zebraStripes aria-label={caption}>
		<Table.Header>
			<Table.Row>
				<Table.HeaderCell scope="col">Fagsystem</Table.HeaderCell>
				<Table.HeaderCell scope="col" align="right">
					Fagsystemvalg
				</Table.HeaderCell>
				<Table.HeaderCell scope="col" align="right">
					Andel
				</Table.HeaderCell>
			</Table.Row>
		</Table.Header>
		<Table.Body>
			{totaler.map((total) => (
				<Table.Row key={total.fagsystem}>
					<Table.HeaderCell scope="row">{total.label}</Table.HeaderCell>
					<Table.DataCell align="right">{formaterTall(total.antall)}</Table.DataCell>
					<Table.DataCell align="right">{formaterAndel(total.andel)}</Table.DataCell>
				</Table.Row>
			))}
		</Table.Body>
	</Table>
)

interface FagsystemFordelingSectionProps {
	fagsystemTotaler: FagsystemTotal[]
	chartOptions: Options
	periodeTittel: string
	isLoading?: boolean
}

export const FagsystemFordelingSection = ({
	fagsystemTotaler,
	chartOptions,
	periodeTittel,
	isLoading = false,
}: FagsystemFordelingSectionProps) => (
	<PanelDel
		tittel={iPeriode('Fagsystem', periodeTittel)}
		beskrivelse="Antall personer bestilt mot hvert fagsystem. Klikk et fagsystem for å se detaljer der det finnes."
	>
		{isLoading ? (
			<LasterStatistikk />
		) : fagsystemTotaler.length === 0 ? (
			<TomPeriode />
		) : (
			<>
				<DashboardChartPanel
					options={chartOptions}
					ariaLabel="Antall fagsystemvalg per fagsystem"
				/>
				<ReadMore header="Vis som tabell" size="small">
					<FagsystemTabell
						totaler={fagsystemTotaler}
						caption="Antall fagsystemvalg per fagsystem i valgt periode"
					/>
				</ReadMore>
			</>
		)}
	</PanelDel>
)

const enhetFor = (granularitet: AdferdGranularitet) => (granularitet === 'DAG' ? 'dag' : 'måned')

interface PersonTabellProps {
	punkter: PersonTidPunkt[]
	enhet: string
}

const PersonTabell = ({ punkter, enhet }: PersonTabellProps) => (
	<Table size="small" zebraStripes aria-label={`Nye personer og bestillinger per ${enhet}`}>
		<Table.Header>
			<Table.Row>
				<Table.HeaderCell scope="col">{enhet === 'dag' ? 'Dato' : 'Måned'}</Table.HeaderCell>
				<Table.HeaderCell scope="col" align="right">
					Nye bestillinger
				</Table.HeaderCell>
				<Table.HeaderCell scope="col" align="right">
					Nye personer
				</Table.HeaderCell>
				<Table.HeaderCell scope="col" align="right">
					Opprettet i Dolly
				</Table.HeaderCell>
				<Table.HeaderCell scope="col" align="right">
					Importert fra Testnorge
				</Table.HeaderCell>
			</Table.Row>
		</Table.Header>
		<Table.Body>
			{punkter.map((punkt) => (
				<Table.Row key={punkt.noekkel}>
					<Table.HeaderCell scope="row">{punkt.visning}</Table.HeaderCell>
					<Table.DataCell align="right">{formaterTall(punkt.nyeBestillinger)}</Table.DataCell>
					<Table.DataCell align="right">{formaterTall(punkt.nyePersoner)}</Table.DataCell>
					<Table.DataCell align="right">{formaterTall(punkt.dollyPersoner)}</Table.DataCell>
					<Table.DataCell align="right">{formaterTall(punkt.testnorgePersoner)}</Table.DataCell>
				</Table.Row>
			))}
		</Table.Body>
	</Table>
)

interface NyePersonerSectionProps {
	punkter: PersonTidPunkt[]
	granularitet: AdferdGranularitet
	chartOptions: Options
	periodeTittel: string
	isLoading?: boolean
}

export const NyePersonerSection = ({
	punkter,
	granularitet,
	chartOptions,
	periodeTittel,
	isLoading = false,
}: NyePersonerSectionProps) => {
	const enhet = enhetFor(granularitet)
	return (
		<PanelDel
			tittel={iPeriode(`Nye personer per ${enhet}`, periodeTittel)}
			beskrivelse={`Personer fra nye bestillinger, delt på personer opprettet i Dolly og personer importert fra Testnorge. Klikk en søyle for å se detaljer for valgt ${enhet}.`}
		>
			{isLoading ? (
				<LasterStatistikk />
			) : punkter.length === 0 ? (
				<TomPeriode melding="Ingen nye personer i valgt periode." />
			) : (
				<>
					<DashboardChartPanel
						options={chartOptions}
						ariaLabel={`Nye personer per ${enhet} fordelt på opprettet i Dolly og importert fra Testnorge`}
					/>
					<ReadMore header="Vis som tabell" size="small">
						<PersonTabell punkter={punkter} enhet={enhet} />
					</ReadMore>
				</>
			)}
		</PanelDel>
	)
}

interface AdferdOverTidSectionProps {
	harTidsdata: boolean
	granularitet: AdferdGranularitet
	chartOptions: Options
	periodeTittel: string
	isLoading?: boolean
}

export const AdferdOverTidSection = ({
	harTidsdata,
	granularitet,
	chartOptions,
	periodeTittel,
	isLoading = false,
}: AdferdOverTidSectionProps) => {
	const enhet = enhetFor(granularitet)
	return (
		<PanelDel
			tittel={iPeriode(`Fagsystem per ${enhet}`, periodeTittel)}
			beskrivelse={`Fagsystemvalg per ${enhet}. Klikk en søyle for å se fagsystemene for valgt ${enhet}.`}
		>
			{isLoading ? (
				<LasterStatistikk />
			) : !harTidsdata ? (
				<TomPeriode />
			) : (
				<DashboardChartPanel
					options={chartOptions}
					ariaLabel={`Antall fagsystemvalg per ${enhet} fordelt på fagsystem`}
				/>
			)}
		</PanelDel>
	)
}

interface ValgtPunktSectionProps {
	granularitet: AdferdGranularitet
	valgtPunkt: string
	valgtPunktVisning: string
	valgtPunktTotaler: FagsystemTotal[]
	valgtPunktPersoner: PersonNoekkeltall | null
	onLukk: () => void
}

export const ValgtPunktSection = ({
	granularitet,
	valgtPunkt,
	valgtPunktVisning,
	valgtPunktTotaler,
	valgtPunktPersoner,
	onLukk,
}: ValgtPunktSectionProps) => {
	const enhet = enhetFor(granularitet)
	const ref = useRef<HTMLDivElement>(null)

	useEffect(() => {
		ref.current?.scrollIntoView?.({ behavior: 'smooth', block: 'nearest' })
	}, [valgtPunkt])

	return (
		<div ref={ref}>
			<PanelDel
				tittel={`Valgt ${enhet}: ${valgtPunktVisning}`}
				handling={
					<Button variant="tertiary" size="small" onClick={onLukk}>
						Lukk
					</Button>
				}
			>
				{valgtPunktPersoner && harPersontall(valgtPunktPersoner) && (
					<PersonKpiRad
						personer={valgtPunktPersoner}
						ekstra={<TallKpi label="Nye bestillinger" verdi={valgtPunktPersoner.nyeBestillinger} />}
					/>
				)}
				{valgtPunktTotaler.length === 0 ? (
					<Alert variant="info" inline>
						Ingen fagsystemvalg registrert for valgt {enhet}.
					</Alert>
				) : (
					<FagsystemTabell
						totaler={valgtPunktTotaler}
						caption={`Fagsystemvalg ${valgtPunktVisning}`}
					/>
				)}
			</PanelDel>
		</div>
	)
}

interface FagsystemDetaljerSectionProps {
	fagsystemOptions: DashboardSelectOption[]
	valgtFagsystem: string | null
	onFagsystemChange: (fagsystem: string) => void
	valgtFagsystemLabel: string
	valgChartOptions: Options | null
	antallChartOptions: Options | null
	periodeTittel: string
	isLoading?: boolean
}

export const FagsystemDetaljerSection = ({
	fagsystemOptions,
	valgtFagsystem,
	onFagsystemChange,
	valgtFagsystemLabel,
	valgChartOptions,
	antallChartOptions,
	periodeTittel,
	isLoading = false,
}: FagsystemDetaljerSectionProps) => (
	<div id={FAGSYSTEM_DETALJER_ID}>
		<PanelDel
			tittel={iPeriode('Detaljer per fagsystem', periodeTittel)}
			beskrivelse="Velg et fagsystem for å se hva som ble bestilt. «Personer» er antall personer med valget. «Antall oppføringer» er summen av oppføringer, for eksempel flere arbeidsforhold per person."
		>
			{isLoading ? (
				<LasterStatistikk />
			) : fagsystemOptions.length === 0 ? (
				<TomPeriode melding="Ingen fagsystemer med detaljer i valgt periode." />
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
		</PanelDel>
	</div>
)
