import React from 'react'
import Button from '~/components/ui/button/Button'
import { Feedback } from '~/components/feedback'
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'
import FagsystemStatus from './FagsystemStatus/FagsystemStatus'
import antallIdenterOpprettet from '~/components/bestilling/utils/antallIdenterOpprettet'
import { BestillingSammendragModal } from '~/components/bestilling/sammendrag/BestillingSammendragModal'

import './BestillingResultat.less'
import GjenopprettConnector from '~/components/bestilling/gjenopprett/GjenopprettBestillingConnector'
import useBoolean from '~/utils/hooks/useBoolean'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '~/utils/hooks/useMutate'

export default function BestillingResultat(props) {
	const { bestilling, onCloseButton } = props
	const brukerId = bestilling?.bruker?.brukerId
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)

	const antall = antallIdenterOpprettet(bestilling)
	const mutate = useMatchMutate()

	mutate(REGEX_BACKEND_GRUPPER)

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
			/>
			<div className="flexbox--all-center">
				<BestillingSammendragModal bestilling={bestilling} />
				<Button onClick={openGjenopprettModal} kind="synchronize">
					GJENOPPRETT
				</Button>
			</div>
			{isGjenopprettModalOpen && (
				<GjenopprettConnector
					bestilling={bestilling}
					brukerId={brukerId}
					closeModal={() => {
						closeGjenoprettModal()
						window.location.reload()
					}}
				/>
			)}
		</div>
	)
}
