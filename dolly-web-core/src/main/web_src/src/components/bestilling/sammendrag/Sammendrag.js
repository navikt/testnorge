import React from 'react'
import Bestillingskriterier from './kriterier/Kriterier'
import MiljoeStatus from './miljoeStatus/MiljoeStatus'
import JiraLenker from '~/components/bestilling/jiraLenker/JiraLenker'

export default function BestillingSammendrag({ bestilling }) {
	return (
		<div className="bestilling-detaljer">
			<Bestillingskriterier bestilling={bestilling} header="Bestillingskriterier" />
			<MiljoeStatus bestilling={bestilling} />
			<JiraLenker openAm={bestilling.openamSent && bestilling.openamSent.split(',')} />
		</div>
	)
}
