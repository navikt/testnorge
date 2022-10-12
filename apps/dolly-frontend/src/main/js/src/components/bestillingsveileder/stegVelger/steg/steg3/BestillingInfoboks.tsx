import * as React from 'react'
import { Bestillingsdata } from '~/components/bestillingsveileder/bestillingTypes'
import { Alert } from '@navikt/ds-react'

type BestillingInfoboksProps = {
	bestillingsdata: Bestillingsdata
}

export const BestillingInfoboks = ({ bestillingsdata }: BestillingInfoboksProps) => {
	const tpsfInfo = bestillingsdata.tpsf

	const harRelasjonMedAdressebeskyttelse = () => {
		let harAdressebeskyttelse = false
		if (tpsfInfo.relasjoner) {
			Object.entries(tpsfInfo.relasjoner).forEach((relasjon) => {
				relasjon[1].forEach((person) => {
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
			<Alert variant={'info'} style={{ marginTop: 20 }}>
				Tilgangsstyring basert på diskresjonskode har nattlig oppdatering, slik at riktig tilgang
				mot miljø kan verifiseres først dagen etter. Ta kontakt med Team Dolly i morgen hvis ønsket
				tilgang mot miljø ikke samsvarer med bestillingen. <br />
			</Alert>
		)
	}
	return null
}
