import React from 'react'
import Icon from '~/components/ui/icon/Icon'

import './FagsystemStatus.less'

export default function FagsystemStatus({ bestilling }) {
	const iconTypes = {
		suksess: 'feedback-check-circle',
		avvik: 'report-problem-circle',
		feil: 'report-problem-triangle'
	}

	const iconType = statuser => {
		// Alle er OK
		if (statuser.every(status => status.melding === 'OK')) return iconTypes.suksess

		// Avvik eller Error
		return statuser.some(status => status.melding === 'OK') ? iconTypes.avvik : iconTypes.feil
	}

	return (
		<div className="fagsystem-status">
			{bestilling.status.map((fagsystem, idx) => (
				<div className="fagsystem-status_kind" key={idx}>
					<Icon size="24px" kind={iconType(fagsystem.statuser)} />
					<p>{fagsystem.navn}</p>
				</div>
			))}
		</div>
	)
}
