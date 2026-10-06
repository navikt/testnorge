import { Button, Heading, HStack, Loader, Tag, Tooltip, VStack } from '@navikt/ds-react'

import { FagsystemStatus } from '@/services/statusApi'
import { isRunning } from '@/pages/StatusPage/useFagsystemStatuses'
import { formatTime, rerunUnavailableReason } from '@/pages/StatusPage/rerunAvailability'

interface FagsystemStatusCardProps {
	statuses: FagsystemStatus[]
	anyRunActive: boolean
	starting: boolean
	now: number
	onRerun: (systemId: string) => void
}

const statusDetails = (status: FagsystemStatus) => {
	if (isRunning(status.state)) {
		return { label: 'Kjører', color: 'info' as const }
	}
	if (status.state === 'OK') {
		return { label: 'OK', color: 'success' as const }
	}
	if (status.state === 'TECHNICAL_ONLY') {
		if (status.technicalStatus.state === 'UP') {
			return { label: 'internal: OK', color: 'success' as const }
		}
		if (status.technicalStatus.state === 'DOWN') {
			return { label: 'Teknisk feil', color: 'danger' as const }
		}
		return { label: 'Ikke sjekket', color: 'neutral' as const }
	}
	if (status.state === 'NOT_RUN') {
		return { label: 'Ikke kjørt', color: 'neutral' as const }
	}
	if (status.state === 'BLOCKED') {
		return { label: 'Blokkert', color: 'warning' as const }
	}
	return { label: 'Feil', color: 'danger' as const }
}

const environmentLabel = (environment: FagsystemStatus['environment']) =>
	environment === 'GLOBAL' ? 'Felles' : environment

export const FagsystemStatusCard = ({
	statuses,
	anyRunActive,
	starting,
	now,
	onRerun,
}: FagsystemStatusCardProps) => {
	const firstStatus = statuses[0]
	const unavailableReason = rerunUnavailableReason(statuses, anyRunActive, now)

	return (
		<article className="fagsystem-card">
			<VStack gap="space-16" className="fagsystem-card-content">
				<Heading level="3" size="small">
					{firstStatus.displayName}
				</Heading>
				<ul className="fagsystem-environments">
					{statuses.map((status) => {
						const details = statusDetails(status)
						return (
							<li key={status.environment}>
								<VStack gap="space-8">
									<HStack align="center" justify="space-between" gap="space-12" wrap={false}>
										<strong>{environmentLabel(status.environment)}</strong>
										<HStack align="center" gap="space-8" wrap={false}>
											{isRunning(status.state) && (
												<Loader size="small" title={`Tester ${firstStatus.displayName}`} />
											)}
											<Tag variant="moderate" size="small" data-color={details.color}>
												{details.label}
											</Tag>
										</HStack>
									</HStack>
									{status.error && <p className="fagsystem-error">{status.error.message}</p>}
									<p className="fagsystem-time">
										Sist fullført: {formatTime(status.completedAt)}
									</p>
								</VStack>
							</li>
						)
					})}
				</ul>
				<VStack gap="space-8" className="fagsystem-card-actions">
					{unavailableReason && <p className="fagsystem-unavailable">{unavailableReason}</p>}
					<Tooltip
						content={unavailableReason ?? `Kjør testen for ${firstStatus.displayName} på nytt`}
						describesChild
					>
						<Button
							type="button"
							variant="secondary"
							size="small"
							loading={starting}
							aria-disabled={unavailableReason !== null}
							onClick={() => unavailableReason === null && onRerun(firstStatus.systemId)}
						>
							Kjør på nytt
						</Button>
					</Tooltip>
				</VStack>
			</VStack>
		</article>
	)
}
