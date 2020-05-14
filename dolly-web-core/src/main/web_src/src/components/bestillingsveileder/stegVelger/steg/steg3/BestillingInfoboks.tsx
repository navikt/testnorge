import * as React from 'react'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { Bestillingsdata } from '~/components/bestillingsveileder/bestillingTypes'

type BestillingInfoboks = {
	bestillingsdata: Bestillingsdata
}

export const BestillingInfoboks = ({ bestillingsdata }: BestillingInfoboks) => {
	const tpsfInfo = bestillingsdata.tpsf
	if (!tpsfInfo) return null
	if (tpsfInfo.egenAnsattDatoFom || tpsfInfo.spesreg === 'SPFO' || tpsfInfo.spesreg === 'SPSF') {
		return (
			<AlertStripeInfo style={{ marginTop: 20 }}>
				Tilgangsstyring basert på diskresjonskode og egenansatt har nattlig oppdatering, slik at
				riktig tilgang mot miljø kan verifiseres først dagen etter. Ta kontakt med team Dolly i
				morgen hvis ønsket tilgang mot miljø ikke samsvarer med bestillingen.
			</AlertStripeInfo>
		)
	}
	return null
}
