import React from 'react'
import Feilmelding from './feilmelding/Feilmelding'
import Bestillingskriterier from './kriterier/Kriterier'
import MiljoeStatus from './miljoeStatus/MiljoeStatus'

export default function BestillingSammendrag({ bestilling, modal = false }) {
	return (
		<div className="bestilling-detaljer">
			{modal && <h1>Bestilling #{bestilling.id}</h1>}
			<Bestillingskriterier bestilling={bestilling} />
			<MiljoeStatus bestilling={bestilling} />
			<Feilmelding bestilling={bestilling} />
		</div>
	)
}
