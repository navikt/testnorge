import React from 'react'
import Bestillingskriterier from './kriterier/Kriterier'
import MiljoeStatus from './miljoeStatus/MiljoeStatus'
import JiraLenker from '~/components/bestilling/jiraLenker/JiraLenker'

export default function BestillingSammendrag({ bestilling, modal = false }) {
	return (
		<div className="bestilling-detaljer">
			{modal && <h1>Bestilling #{bestilling.id}</h1>}
			<Bestillingskriterier bestilling={bestilling} />
			<MiljoeStatus bestilling={bestilling} />
			<JiraLenker openAm={bestilling.openamSent && bestilling.openamSent.split(',')} />
		</div>
	)
}
