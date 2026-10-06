import { useId } from 'react'
import { Button, Heading, HStack, Tooltip, VStack } from '@navikt/ds-react'

import { FagsystemStatus } from '@/services/statusApi'
import { FagsystemStatusCard } from '@/pages/StatusPage/FagsystemStatusCard'
import { rerunUnavailableReason } from '@/pages/StatusPage/rerunAvailability'

interface FagsystemStatusPanelProps {
	title: string
	groupedStatuses: FagsystemStatus[][]
	anyRunActive: boolean
	startingSystemId: string | null
	startingAll: boolean
	now: number
	onRerun: (systemId: string) => void
	onRerunAll: () => void
}

export const FagsystemStatusPanel = ({
	title,
	groupedStatuses,
	anyRunActive,
	startingSystemId,
	startingAll,
	now,
	onRerun,
	onRerunAll,
}: FagsystemStatusPanelProps) => {
	const headingId = useId()
	const unavailableReason = rerunUnavailableReason(groupedStatuses.flat(), anyRunActive, now)

	return (
		<section className="fagsystem-panel" aria-labelledby={headingId}>
			<VStack gap="space-16">
				<HStack align="center" justify="space-between" gap="space-12">
					<Heading id={headingId} level="2" size="medium">
						{title}
					</Heading>
					<HStack align="center" gap="space-12">
						{unavailableReason && <p className="fagsystem-unavailable">{unavailableReason}</p>}
						<Tooltip
							content={unavailableReason ?? `Kjør alle ${title.toLowerCase()} på nytt`}
							describesChild
						>
							<Button
								type="button"
								variant="secondary"
								size="small"
								loading={startingAll}
								aria-disabled={unavailableReason !== null}
								onClick={() => unavailableReason === null && onRerunAll()}
							>
								Kjør alle på nytt
							</Button>
						</Tooltip>
					</HStack>
				</HStack>
				<div className="fagsystem-grid">
					{groupedStatuses.map((statuses) => (
						<FagsystemStatusCard
							key={statuses[0].systemId}
							statuses={statuses}
							anyRunActive={anyRunActive}
							starting={startingSystemId === statuses[0].systemId}
							now={now}
							onRerun={onRerun}
						/>
					))}
				</div>
			</VStack>
		</section>
	)
}
