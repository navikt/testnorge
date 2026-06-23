import { format, parseISO } from 'date-fns'
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
	Label,
	Skeleton,
	Tag,
	VStack,
} from '@navikt/ds-react'
import {
	DashboardChartPanel,
	DashboardKpiCard,
	DashboardSectionCard,
	DashboardSelectButtons,
} from './dashboardSharedComponents'
import {
	erStrukturertFeilVerdi,
	type FeilDetaljRad,
	type FeilGruppe,
	feilVerdiTilTekst,
} from './dashboardFeilUtils'
import { type FeilVerdi } from '@/utils/hooks/useDashboard'
import { useDashboardFeil } from './DashboardFeilContext'

const toDateTimeDisplay = (value: string): string => {
	try {
		return format(parseISO(value), 'dd.MM.yyyy HH:mm')
	} catch {
		return value
	}
}

const FeilVerdiVisning = ({ verdi }: { verdi: FeilVerdi }) => {
	const tekst = feilVerdiTilTekst(verdi)
	if (!tekst) {
		return <Detail textColor="subtle">Ingen feilmelding</Detail>
	}
	if (erStrukturertFeilVerdi(verdi)) {
		return (
			<Box
				background="neutral-soft"
				borderRadius="4"
				padding="space-8"
				maxHeight="240px"
				overflow="auto"
			>
				<pre
					style={{
						margin: 0,
						fontSize: '12px',
						whiteSpace: 'pre-wrap',
						wordBreak: 'break-word',
					}}
				>
					{tekst}
				</pre>
			</Box>
		)
	}
	return <BodyShort size="small">{tekst}</BodyShort>
}

const FeilDetaljRadVisning = ({ rad }: { rad: FeilDetaljRad }) => (
	<Box borderWidth="1" borderColor="neutral-subtle" borderRadius="8" padding="space-12">
		<VStack gap="space-4">
			<HStack gap="space-8" align="center" wrap>
				<Label size="small">{rad.ident}</Label>
				{rad.master && (
					<Tag size="xsmall" variant="neutral">
						{rad.master}
					</Tag>
				)}
				<Detail textColor="subtle">Sist oppdatert: {toDateTimeDisplay(rad.sistOppdatert)}</Detail>
			</HStack>
			<FeilVerdiVisning verdi={rad.verdi} />
		</VStack>
	</Box>
)

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
						<VStack gap="space-12">
							{gruppe.rader.map((rad) => (
								<FeilDetaljRadVisning
									key={`${gruppe.feilNokkel}-${rad.bestillingId}-${rad.ident}`}
									rad={rad}
								/>
							))}
						</VStack>
					</Accordion.Content>
				</Accordion.Item>
			))}
		</Accordion>
	</>
)

export const DashboardFeilSection = ({
	title = 'Feil per måned og dag',
}: {
	title?: string
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
				{feilYearOptions.length === 0 ? (
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
