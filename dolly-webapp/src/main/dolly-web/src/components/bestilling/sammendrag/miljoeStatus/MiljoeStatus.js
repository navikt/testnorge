import React from 'react'
import Header from '~/components/bestilling/sammendrag/header/Header'
import Formatters from '~/utils/DataFormatter'
import FagsystemStatus from './fagsystemStatus/FagsystemStatus'
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'
import antallIdenterOpprettet from '~/components/bestilling/utils/antallIdenterOpprettet'

import './MiljoeStatus.less'

const mapStatusrapport = bestillingstatus => {
	return bestillingstatus.reduce((acc, curr) => {
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
					feil.miljo = Formatters.arrayToString(miljoArray)
					feil.identer = Formatters.arrayToString(identArray)
				}

				return feil
			})
		)
	}, [])
}

export default function MiljoeStatus({ bestilling }) {
	const statusrapport = mapStatusrapport(bestilling.status)
	const { tekst } = antallIdenterOpprettet(bestilling)

	return (
		<div>
			<Header label="Bestillingsstatus" />
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
