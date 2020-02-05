import React from 'react'
import HjelpeTekst from 'nav-frontend-hjelpetekst'

// Wrapper for å hindre at klikk på hjelpetekst-knapp åpner/lukker panel
export default function HjelpetekstWrapper({ informasjonstekst }) {
	return (
		<div onClick={e => e.stopPropagation()}>
			<HjelpeTekst>{informasjonstekst}</HjelpeTekst>
		</div>
	)
}
