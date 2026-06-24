import {
	Accordion,
	Alert,
	BodyShort,
	Box,
	Button,
	Detail,
	Heading,
	HGrid,
	HStack,
	Skeleton,
	VStack,
} from '@navikt/ds-react'
import BestillingResultat from '@/components/bestilling/statusListe/BestillingResultat/BestillingResultat'
import DollySpinner from '@/components/ui/loading/DollySpinner'
import {
	DashboardChartPanel,
	DashboardKpiCard,
	DashboardSectionCard,
	DashboardSelectButtons,
} from './dashboardSharedComponents'
import { type FeilDetaljRad, type FeilGruppe, feilVerdiTilMelding } from './dashboardFeilUtils'
import { useDashboardFeil } from './DashboardFeilContext'
import type { Bestillingsstatus } from '@/utils/hooks/useDollyOrganisasjoner'

const FeilDetaljRadVisning = ({
	rad,
	fagsystemNavn,
}: {
	rad: FeilDetaljRad
	fagsystemNavn: string
}) => {
	const feilmelding = feilVerdiTilMelding(rad.verdi)
	const bestilling = {
		id: rad.bestillingId,
		environments: rad.master ? [rad.master] : [],
		antallIdenter: 1,
		antallIdenterOpprettet: 0,
		antallLevert: 0,
		bestilling: {},
		bruker: {},
		gruppeId: 0,
		ferdig: false,
		sistOppdatert: new Date(rad.sistOppdatert),
		opprettetFraGruppeId: 0,
		gjenopprettetFraIdent: 0,
		opprettetFraId: 0,
		status: [
			{
				id: rad.feilNokkel ?? `feil-${rad.bestillingId}`,
				navn: fagsystemNavn,
				statuser: [
					{
						melding: feilmelding,
						identer: [rad.ident],
						detaljert: rad.master
							? [
									{
										miljo: rad.master,
										identer: [rad.ident],
									},
								]
							: undefined,
					},
				],
			},
		],
		systeminfo: '',
		stoppet: false,
	} as Bestillingsstatus
	return (
		<BestillingResultat
			bestilling={bestilling}
			lukkBestilling={() => undefined}
			erOrganisasjon={false}
			compact
		/>
	)
}

export const FeilGrupperVisning = ({
	feilGrupper,
	feilDetaljertCount,
}: {
	feilGrupper: FeilGruppe[]
	feilDetaljertCount: number
}) => (
	<>
		<Detail textColor="subtle">
			{feilDetaljertCount} bestilling(er) med feil, fordelt over {feilGrupper.length} fagsystem.
		</Detail>
		<Accordion size="small">
			{feilGrupper.map((gruppe) => (
				<Accordion.Item key={gruppe.feilNokkel}>
					<Accordion.Header>
						{gruppe.label} ({gruppe.rader.length})
					</Accordion.Header>
					<Accordion.Content>
						{gruppe.rader.map((rad) => (
							<FeilDetaljRadVisning
								key={`${gruppe.feilNokkel}-${rad.bestillingId}-${rad.ident}`}
								rad={rad}
								fagsystemNavn={gruppe.label}
							/>
						))}
					</Accordion.Content>
				</Accordion.Item>
			))}
		</Accordion>
	</>
)

export const DashboardFeilSection = ({
	title = 'Feil per måned og dag',
	isLoading = false,
}: {
	title?: string
	isLoading?: boolean
} = {}) => {
	const {
		feilYearOptions,
		selectedFeilYear,
		onSelectedFeilYearChange,
		feilMonthOptions,
		selectedFeilInterval,
		onSelectedFeilIntervalChange,
		feilDagerMedFeil,
		feilTotalt,
		hasFeilData,
		feilSummertChartOptions,
		loadingFeilSummert,
		selectedFeilDay,
		feilSelectedDayLabel,
		onSelectedFeilDayChange,
		feilGrupper,
		feilDetaljertCount,
		loadingFeilDetaljert,
	} = useDashboardFeil()

	return (
		<DashboardSectionCard>
			<VStack gap="space-16">
				<Heading level="2" size="small">
					{title}
				</Heading>
				{isLoading ? (
					<DollySpinner size={120} label="Laster feildata..." />
				) : feilYearOptions.length === 0 ? (
					<Alert variant="info" inline>
						Ingen feildata tilgjengelig.
					</Alert>
				) : (
					<>
						<DashboardSelectButtons
							label="År"
							selected={selectedFeilYear}
							onSelect={onSelectedFeilYearChange}
							options={feilYearOptions.map((year) => ({ value: year, label: year }))}
						/>
						<DashboardSelectButtons
							label="Måned"
							selected={selectedFeilInterval}
							onSelect={onSelectedFeilIntervalChange}
							options={feilMonthOptions}
						/>
						{loadingFeilSummert ? (
							<VStack gap="space-12">
								<HGrid columns={{ xs: 1, sm: 2 }} gap="space-12">
									<Skeleton variant="rectangle" height="80px" />
									<Skeleton variant="rectangle" height="80px" />
								</HGrid>
								<Skeleton variant="rectangle" height="360px" />
							</VStack>
						) : !hasFeilData ? (
							<Alert variant="success" inline>
								Ingen feil registrert i valgt måned.
							</Alert>
						) : (
							<>
								<HGrid columns={{ xs: 1, sm: 2 }} gap="space-12">
									<DashboardKpiCard label="Antall feil i måneden" value={feilTotalt} />
									<DashboardKpiCard label="Dager med feil" value={feilDagerMedFeil} />
								</HGrid>
								<BodyShort size="small" textColor="subtle">
									Dager i diagrammet kan åpnes for å se detaljerte feil per fagsystem.
								</BodyShort>
								<DashboardChartPanel
									options={feilSummertChartOptions}
									ariaLabel="Antall feil per dag fordelt på fagsystem"
								/>
								{selectedFeilDay !== null && (
									<Box
										borderWidth="1"
										borderColor="neutral-subtle"
										borderRadius="8"
										padding="space-16"
										background="neutral-soft"
									>
										<VStack gap="space-12">
											<HStack justify="space-between" align="center" wrap gap="space-8">
												<Heading level="3" size="xsmall">
													Detaljerte feil {feilSelectedDayLabel}
												</Heading>
												<Button
													variant="tertiary"
													size="small"
													onClick={() => onSelectedFeilDayChange(null)}
												>
													Lukk
												</Button>
											</HStack>
											{loadingFeilDetaljert ? (
												<Skeleton variant="rectangle" height="160px" />
											) : feilGrupper.length === 0 ? (
												<Alert variant="info" inline>
													Ingen detaljerte feil registrert for valgt dag.
												</Alert>
											) : (
												<FeilGrupperVisning
													feilGrupper={feilGrupper}
													feilDetaljertCount={feilDetaljertCount}
												/>
											)}
										</VStack>
									</Box>
								)}
							</>
						)}
					</>
				)}
			</VStack>
		</DashboardSectionCard>
	)
}
