import { useId } from 'react'
import { Heading, VStack } from '@navikt/ds-react'

import { FagsystemStatus } from '@/services/statusApi'
import { FagsystemStatusCard } from '@/pages/StatusPage/FagsystemStatusCard'

interface FagsystemStatusPanelProps {
	title: string
	groupedStatuses: FagsystemStatus[][]
	anyRunActive: boolean
	startingSystemId: string | null
	now: number
	onRerun: (systemId: string) => void
}

export const FagsystemStatusPanel = ({
	title,
	groupedStatuses,
	anyRunActive,
	startingSystemId,
	now,
	onRerun,
}: FagsystemStatusPanelProps) => {
	const headingId = useId()

	return (
		<section className="fagsystem-panel" aria-labelledby={headingId}>
			<VStack gap="space-16">
				<Heading id={headingId} level="2" size="medium">
					{title}
				</Heading>
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
