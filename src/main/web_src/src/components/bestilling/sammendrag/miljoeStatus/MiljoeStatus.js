import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Formatters from '~/utils/DataFormatter'
import FagsystemStatus from './fagsystemStatus/FagsystemStatus'
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'
import antallIdenterOpprettet from '~/components/bestilling/utils/antallIdenterOpprettet'

import './MiljoeStatus.less'

const mapStatusrapport = bestillingstatus => {
	if (bestillingstatus[0].organisasjonsnummer) {
		const status = bestillingstatus[0]
		return {
			organisasjonsnummer: status.organisasjonsnummer,
			melding: status.organisasjonsforvalterStatus
		}
	}
	const successFirst = a => (a.melding ? 1 : -1)
	return bestillingstatus
		.reduce((acc, curr) => {
			return acc.concat(
				curr.statuser.map(status => {
					const feil = {
						navn: curr.navn,
						melding: status.melding !== 'OK' ? status.melding : null
					}

					if (status.identer) {
						feil.miljo = null
						feil.identer = status.identer
					}

					if (status.detaljert) {
						const miljoArray = status.detaljert.map(m => m.miljo).sort()
						const identArray = status.detaljert[0].identer
						feil.miljo = miljoArray[0] ? Formatters.arrayToString(miljoArray) : ''
						feil.identer = identArray
					}

					return feil
				})
			)
		}, [])
		.sort(successFirst)
}

export default function MiljoeStatus({ bestilling }) {
	const statusrapport = mapStatusrapport(bestilling.status)
	const { tekst } = antallIdenterOpprettet(bestilling)

	return (
		<div>
			<SubOverskrift label="Bestillingsstatus" />
			{bestilling.feil && (
				<div className="feilmelding_generell">
					<p>{tekst}</p>
					<ApiFeilmelding feilmelding={bestilling.feil} container />
				</div>
			)}
			<FagsystemStatus statusrapport={statusrapport} />
		</div>
	)
}
