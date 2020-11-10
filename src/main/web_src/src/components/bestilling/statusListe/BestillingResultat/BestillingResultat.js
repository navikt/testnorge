import React from 'react'
import Button from '~/components/ui/button/Button'
import { Feedback } from '~/components/feedback'
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'
import FagsystemStatus from './FagsystemStatus/FagsystemStatus'
import antallIdenterOpprettet from '~/components/bestilling/utils/antallIdenterOpprettet'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/SammendragModal'

import './BestillingResultat.less'

export default function BestillingResultat(props) {
	const { bestilling, onCloseButton, brukerBilde } = props

	const antall = antallIdenterOpprettet(bestilling)

	return (
		<div className="bestilling-resultat">
			<div className="status-header">
				<p>Bestilling #{bestilling.id}</p>
				<h3>Bestillingsstatus</h3>
				<div className="status-header_button-wrap">
					<Button kind="remove-circle" onClick={() => onCloseButton(bestilling.id)} />
				</div>
			</div>
			<hr />
			<FagsystemStatus bestilling={bestilling} />
			{antall.harMangler && <span>{antall.tekst}</span>}
			{bestilling.feil && <ApiFeilmelding feilmelding={bestilling.feil} container />}
			<Feedback
				label="Hvordan var din opplevelse med bruk av Dolly?"
				feedbackFor="Bruk av Dolly etter bestilling"
				brukerBilde={brukerBilde}
			/>
			<div className="flexbox--all-center">
				<BestillingSammendragModal bestilling={bestilling} />
			</div>
		</div>
	)
}
