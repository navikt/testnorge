import React from 'react'
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'
import antallIdenterOpprettet from '~/components/bestilling/utils/antallIdenterOpprettet'

export default function Systemfeil({ bestilling }) {
	if (!bestilling.feil) return false
	const { tekst } = antallIdenterOpprettet(bestilling)

	return (
		<div className="feilmelding_generell">
			<p>{tekst}</p>
			<ApiFeilmelding feilmelding={bestilling.feil} container />
		</div>
	)
}
