import React from 'react'
import Icon from '~/components/ui/icon/Icon'

import './FagsystemStatus.less'
import { Miljostatus, Status } from '~/components/bestilling/sammendrag/miljoeStatus/MiljoeStatus'

export default function FagsystemStatus({ bestilling }: Miljostatus) {
	const IconTypes = {
		suksess: 'feedback-check-circle',
		avvik: 'report-problem-circle',
		feil: 'report-problem-triangle',
	}

	const iconType = (statuser: Status[], feil: string) => {
		if (feil) return IconTypes.feil
		// Alle er OK
		if (statuser.every((status) => status.melding === 'OK')) return IconTypes.suksess
		// Denne statusmeldingen gir kun avvik
		else if (
			statuser.some(
				(status) =>
					status?.melding?.includes('TIDSAVBRUDD') ||
					status?.melding?.includes('Tidsavbrudd') ||
					status?.melding?.includes('tidsavbrudd')
			)
		)
			return IconTypes.avvik
		// Avvik eller Error
		return statuser.some((status) => status?.melding === 'OK') ? IconTypes.avvik : IconTypes.feil
	}

	return (
		<div className="fagsystem-status">
			{bestilling.status.map((fagsystem, idx) => (
				<div className="fagsystem-status_kind" key={idx}>
					<Icon kind={iconType(fagsystem.statuser, bestilling.feil)} />
					<p>{fagsystem.navn}</p>
				</div>
			))}
		</div>
	)
}
