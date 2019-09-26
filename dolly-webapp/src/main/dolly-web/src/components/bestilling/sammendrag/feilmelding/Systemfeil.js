import React from 'react'
import antallIdenterOpprettet from '~/components/bestilling/utils/antallIdenterOpprettet'

export default function Systemfeil({ bestilling }) {
	if (!bestilling.feil) return false
	const { tekst } = antallIdenterOpprettet(bestilling)

	return (
		<div className="feilmelding_generell">
			<p>{tekst}</p>
			<pre>{bestilling.feil}</pre>
		</div>
	)
}
