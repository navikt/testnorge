import React from 'react'

export default function Systemfeil({ bestilling }) {
	if (!bestilling.feil) return false

	const { antallIdenter, antallIdenterOpprettet } = bestilling
	const antallTekstPartial =
		antallIdenterOpprettet < 1 ? 'Ingen' : `${antallIdenterOpprettet} av ${antallIdenter}`
	const antallTekst = `${antallTekstPartial} bestilte identer ble opprettet i TPSF`

	return (
		<div className="feilmelding_generell">
			<p>{antallTekst}</p>
			<pre>{bestilling.feil}</pre>
		</div>
	)
}
