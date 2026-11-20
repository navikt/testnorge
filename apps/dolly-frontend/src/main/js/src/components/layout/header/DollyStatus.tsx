import { useEffect, useState } from 'react'
import {
	ArrowsCirclepathIcon,
	CheckmarkCircleIcon,
	ExclamationmarkTriangleIcon,
	QuestionmarkCircleIcon,
	XMarkOctagonIcon,
} from '@navikt/aksel-icons'
import {
	Badge,
	BodyShort,
	Button,
	Detail,
	Heading,
	HStack,
	InfoCard,
	List,
	Loader,
	Popover,
	VStack,
} from '@navikt/ds-react'
import styled from 'styled-components'
import { formatDateTime } from '@/utils/DataFormatter'
import { useCurrentBruker } from '@/utils/hooks/useBruker'
import type {
	DollyStatusKjoringResult,
	DollyStatusNivaa,
	DollyStatusOversikt,
} from '@/utils/hooks/useDollyStatus'
import { useAutomatiskDollyStatusKjoring, useDollyStatus } from '@/utils/hooks/useDollyStatus'

const maxTimerDelay = 2_147_483_647

const HeaderStatusButton = styled(Button)`
	&& {
		color: white;

		&:hover {
			color: white;
			background-color: rgb(255 255 255 / 12%);
		}

		&:focus-visible {
			outline: 2px solid white;
			outline-offset: 2px;
		}

		min-width: calc(0.75rem + 2 * var(--ax-space-12));
		overflow: hidden;

		.aksel-label {
			min-width: 0;
		}
	}
`

const StatusInnhold = styled.span`
	display: flex;
	flex-wrap: wrap;
	align-content: flex-start;
	justify-content: flex-end;
	column-gap: 6px;
	height: 1.5rem;
	overflow: hidden;

	> span {
		display: flex;
		align-items: center;
		height: 1.5rem;
		line-height: 1.5rem;
	}
`

const getBadgeColor = (
	status: DollyStatusNivaa | undefined,
	hasError: boolean,
): NonNullable<Badge.Props['data-color']> => {
	if (hasError || !status || status === 'UKJENT') {
		return 'neutral'
	}
	if (status === 'OK') {
		return 'success'
	}
	if (status === 'ADVARSEL') {
		return 'warning'
	}
	return 'danger'
}

const getAriaLabel = (statusoversikt: DollyStatusOversikt | undefined, hasError: boolean) => {
	const statusLabel = getStatusLabel(statusoversikt, hasError)
	return !hasError && statusoversikt?.kjoringPagaar
		? `${statusLabel}. Testkjøring pågår`
		: statusLabel
}

const getStatusLabel = (statusoversikt: DollyStatusOversikt | undefined, hasError: boolean) => {
	if (hasError || !statusoversikt || statusoversikt.status === 'UKJENT') {
		return 'Systemstatus: Ukjent'
	}
	if (statusoversikt.status === 'OK') {
		return 'Systemstatus: Alt fungerer'
	}

	const antallProblemer = statusoversikt?.problemer?.length ?? 0
	const systemer = antallProblemer === 1 ? 'system' : 'systemer'
	if (statusoversikt.status === 'ADVARSEL') {
		return `Systemstatus: Advarsel for ${antallProblemer} ${systemer}`
	}
	return `Systemstatus: Feil i ${antallProblemer} ${systemer}`
}

const getStatusText = (statusoversikt: DollyStatusOversikt | undefined, hasError: boolean) => {
	if (hasError) {
		return 'Fikk ikke hentet status.'
	}
	switch (statusoversikt?.status) {
		case 'OK':
			return 'Alle fagsystem OK.'
		case 'ADVARSEL':
			return 'Noen systemer har problemer.'
		case 'FEIL':
			return 'Noen systemer feiler.'
		default:
			return statusoversikt?.kjoringPagaar
				? 'Testene kjører. Status oppdateres når kjøringen er ferdig.'
				: 'Status er ukjent.'
	}
}

const getStatusIcon = (status: DollyStatusNivaa | undefined, hasError: boolean) => {
	if (hasError || !status || status === 'UKJENT') {
		return <QuestionmarkCircleIcon aria-hidden />
	}
	if (status === 'OK') {
		return <CheckmarkCircleIcon aria-hidden />
	}
	if (status === 'ADVARSEL') {
		return <ExclamationmarkTriangleIcon aria-hidden />
	}
	return <XMarkOctagonIcon aria-hidden />
}

const formatClockTime = (time: string) => formatDateTime(time).split(' ')[1]

const isValidTimestamp = (time: string | null | undefined): time is string =>
	typeof time === 'string' && !Number.isNaN(Date.parse(time))

export const DollyStatus = () => {
	const { statusoversikt, error, kjorFeilede, kjorUtlopte } = useDollyStatus()
	const { currentBruker } = useCurrentBruker()
	const [anchorElement, setAnchorElement] = useState<HTMLButtonElement | null>(null)
	const [open, setOpen] = useState(false)
	const [posting, setPosting] = useState(false)
	const [postError, setPostError] = useState(false)
	const [postOutcome, setPostOutcome] = useState<DollyStatusKjoringResult | null>(null)
	const [now, setNow] = useState(() => Date.now())
	const hasProblems = (statusoversikt?.problemer?.length ?? 0) > 0
	const statusColor = getBadgeColor(statusoversikt?.status, !!error)
	const statusIcon = getStatusIcon(statusoversikt?.status, !!error)
	const statusText = getStatusText(statusoversikt, !!error)
	const kjoringPagaar = !error && !!statusoversikt?.kjoringPagaar
	const kjoringPagaarUtenStatus = kjoringPagaar && statusoversikt?.status === 'UKJENT'
	const canRunTests = !!currentBruker && currentBruker.brukertype !== 'BANKID'
	const manualDeadline = statusoversikt?.nesteManuelleKjoringTidligst
	const parsedManualDeadline = manualDeadline ? Date.parse(manualDeadline) : undefined
	const manualDeadlineTime =
		parsedManualDeadline !== undefined && !Number.isNaN(parsedManualDeadline)
			? parsedManualDeadline
			: undefined
	const canRunManually = manualDeadlineTime !== undefined && manualDeadlineTime <= now

	useEffect(() => {
		if (!manualDeadline || manualDeadlineTime === undefined || manualDeadlineTime <= now) {
			return
		}
		const timer = window.setTimeout(
			() => setNow(Date.now()),
			Math.min(Math.max(0, manualDeadlineTime - Date.now()), maxTimerDelay),
		)
		return () => window.clearTimeout(timer)
	}, [manualDeadline, manualDeadlineTime, now])

	useAutomatiskDollyStatusKjoring(
		statusoversikt,
		currentBruker?.brukertype !== 'BANKID' && !!currentBruker,
	)

	const handleManualKjoring = async (startKjoring: () => Promise<DollyStatusKjoringResult>) => {
		setPosting(true)
		setPostError(false)
		setPostOutcome(null)
		try {
			setPostOutcome(await startKjoring())
		} catch {
			setPostError(true)
		} finally {
			setPosting(false)
		}
	}

	const togglePopover = () => {
		if (!open) {
			setPostError(false)
			setPostOutcome(null)
		}
		setOpen(!open)
	}

	return (
		<>
			<HeaderStatusButton
				ref={setAnchorElement}
				variant="tertiary-neutral"
				size="small"
				aria-label={getAriaLabel(statusoversikt, !!error)}
				aria-expanded={open}
				data-testid="dolly-status-knapp"
				onClick={togglePopover}
			>
				<StatusInnhold>
					<span>
						{kjoringPagaar ? (
							<Loader size="xsmall" variant="inverted" transparent aria-hidden title="" />
						) : (
							<Badge aria-hidden data-color={statusColor} />
						)}
					</span>
					<span>Status</span>
				</StatusInnhold>
			</HeaderStatusButton>
			<Popover
				open={open}
				onClose={() => setOpen(false)}
				anchorEl={anchorElement}
				placement="bottom-end"
			>
				<Popover.Content style={{ maxWidth: '24rem' }}>
					<VStack gap="space-12">
						<Heading size="xsmall" level="2">
							Systemstatus
						</Heading>
						<InfoCard size="small" data-color={statusColor}>
							{hasProblems ? (
								<>
									<InfoCard.Header icon={statusIcon}>
										<InfoCard.Title as="h3">{statusText}</InfoCard.Title>
									</InfoCard.Header>
									<InfoCard.Content>
										<List size="small">
											{statusoversikt?.problemer?.map((problem) => (
												<List.Item
													key={`${problem.systemId}-${problem.miljo ?? ''}`}
													title={
														problem.miljo ? `${problem.navn} (${problem.miljo})` : problem.navn
													}
												>
													{problem.melding ?? problem.tilstand}
												</List.Item>
											))}
										</List>
									</InfoCard.Content>
								</>
							) : (
								<InfoCard.Message
									icon={
										kjoringPagaarUtenStatus ? (
											<Loader size="xsmall" aria-hidden title="" />
										) : (
											statusIcon
										)
									}
								>
									{statusText}
								</InfoCard.Message>
							)}
						</InfoCard>
						{kjoringPagaar && !kjoringPagaarUtenStatus && (
							<HStack gap="space-8" align="center">
								<Loader size="xsmall" aria-hidden title="" />
								<BodyShort size="small">Ny testkjøring pågår.</BodyShort>
							</HStack>
						)}
						{(isValidTimestamp(statusoversikt?.sistOppdatert) ||
							isValidTimestamp(statusoversikt?.nesteAutomatiskeKjoring)) && (
							<VStack gap="space-4">
								{isValidTimestamp(statusoversikt?.sistOppdatert) && (
									<Detail textColor="subtle">
										Sist oppdatert: {formatDateTime(statusoversikt.sistOppdatert)}
									</Detail>
								)}
								{isValidTimestamp(statusoversikt?.nesteAutomatiskeKjoring) && (
									<Detail textColor="subtle">
										Neste automatiske kjøring kl.{' '}
										{formatClockTime(statusoversikt.nesteAutomatiskeKjoring)}
									</Detail>
								)}
							</VStack>
						)}
						{canRunTests &&
							!error &&
							statusoversikt?.status === 'UKJENT' &&
							!statusoversikt.kjoringPagaar && (
								<div>
									<Button
										size="small"
										variant="secondary"
										icon={<ArrowsCirclepathIcon aria-hidden />}
										loading={posting}
										disabled={posting}
										onClick={() => void handleManualKjoring(kjorUtlopte)}
									>
										Kjør tester
									</Button>
								</div>
							)}
						{canRunTests && hasProblems && statusoversikt?.status !== 'UKJENT' && (
							<div>
								<Button
									size="small"
									variant="secondary"
									icon={<ArrowsCirclepathIcon aria-hidden />}
									loading={posting}
									disabled={posting || !!statusoversikt?.kjoringPagaar || !canRunManually}
									onClick={() => void handleManualKjoring(kjorFeilede)}
								>
									{statusoversikt?.kjoringPagaar
										? 'Kjøring pågår'
										: manualDeadlineTime !== undefined && manualDeadlineTime > now
											? `Kan kjøres igjen kl. ${formatClockTime(manualDeadline!)}`
											: 'Kjør feilende tester på nytt'}
								</Button>
							</div>
						)}
						{postOutcome === 'nedkjoling' && (
							<BodyShort size="small" role="status">
								Testene ble startet nylig. Prøv igjen om noen minutter.
							</BodyShort>
						)}
						{postOutcome === 'pagar' && (
							<BodyShort size="small" role="status">
								En testkjøring pågår allerede.
							</BodyShort>
						)}
						{postError && (
							<BodyShort size="small" role="alert">
								Klarte ikke å starte testkjøring.
							</BodyShort>
						)}
					</VStack>
				</Popover.Content>
			</Popover>
		</>
	)
}
