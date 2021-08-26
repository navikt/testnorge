import * as React from 'react'
import { AlertStripeInfo } from 'nav-frontend-alertstriper'
import { Bestillingsdata } from '~/components/bestillingsveileder/bestillingTypes'

type BestillingInfoboks = {
	bestillingsdata: Bestillingsdata
}

export const BestillingInfoboks = ({ bestillingsdata }: BestillingInfoboks) => {
	const tpsfInfo = bestillingsdata.tpsf

	const harRelasjonMedAdressebeskyttelse = () => {
		let harAdressebeskyttelse = false
		if (tpsfInfo.relasjoner) {
			Object.entries(tpsfInfo.relasjoner).forEach(relasjon => {
				relasjon[1].forEach(person => {
					if (person.spesreg === 'SPFO' || person.spesreg === 'SPSF' || person.spesreg === 'SFU') {
						harAdressebeskyttelse = true
					}
				})
			})
		}
		return harAdressebeskyttelse
	}

	if (
		tpsfInfo &&
		(tpsfInfo.spesreg === 'SPFO' ||
			tpsfInfo.spesreg === 'SPSF' ||
			tpsfInfo.spesreg === 'SFU' ||
			harRelasjonMedAdressebeskyttelse() === true)
	) {
		return (
			// @ts-ignore
			<AlertStripeInfo style={{ marginTop: 20 }}>
				Tilgangsstyring basert på diskresjonskode har nattlig oppdatering, slik at riktig tilgang
				mot miljø kan verifiseres først dagen etter. Ta kontakt med team Dolly i morgen hvis ønsket
				tilgang mot miljø ikke samsvarer med bestillingen. <br />
			</AlertStripeInfo>
		)
	}
	return null
}
