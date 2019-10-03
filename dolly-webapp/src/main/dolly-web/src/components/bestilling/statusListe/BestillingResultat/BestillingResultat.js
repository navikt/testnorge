import React, { useState } from 'react'
import Icon from '~/components/ui/icon/Icon'
import Button from '~/components/ui/button/Button'
import DollyModal from '~/components/ui/modal/DollyModal'
import BestillingSammendrag from '~/components/bestilling/sammendrag/Sammendrag'
import ApiFeilmelding from '~/components/ui/apiFeilmelding/ApiFeilmelding'
import FagsystemStatus from './FagsystemStatus/FagsystemStatus'
import antallIdenterOpprettet from '~/components/bestilling/utils/antallIdenterOpprettet'

import './BestillingResultat.less'

export default function BestillingResultat(props) {
	const [modalIsOpen, setModalIsOpen] = useState(false)
	const openModal = () => setModalIsOpen(true)
	const closeModal = () => setModalIsOpen(false)

	const { bestilling, onCloseButton } = props

	const antall = antallIdenterOpprettet(bestilling)

	return (
		<div className="bestilling-resultat">
			<div className="status-header">
				<p>Bestilling #{bestilling.id}</p>
				<h3>Bestillingsstatus</h3>
				<Button kind="remove-circle" onClick={() => onCloseButton(bestilling.id)} />
			</div>
			<hr />
			<FagsystemStatus bestilling={bestilling} />
			{antall.harMangler && <span>{antall.tekst}</span>}
			{bestilling.feil && <ApiFeilmelding feilmelding={bestilling.feil} container />}
			<div className="flexbox--all-center">
				<Button onClick={openModal} className="flexbox--align-center" kind="details">
					BESTILLINGSDETALJER
				</Button>
				<DollyModal isOpen={modalIsOpen} closeModal={closeModal} width="60%">
					<BestillingSammendrag bestilling={bestilling} modal />
				</DollyModal>
			</div>
		</div>
	)
}
