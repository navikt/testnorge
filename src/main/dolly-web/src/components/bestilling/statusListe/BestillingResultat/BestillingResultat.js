import React, { useState } from 'react'
import Icon from '~/components/ui/icon/Icon'
import Button from '~/components/ui/button/Button'
import DollyModal from '~/components/ui/modal/DollyModal'
import BestillingSammendrag from '~/components/bestilling/sammendrag/Sammendrag'
import MiljoeStatus from './MiljoeStatus/MiljoeStatus'

import './BestillingResultat.less'

export default function BestillingResultat(props) {
	const [modalIsOpen, setModalIsOpen] = useState(false)
	const openModal = () => setModalIsOpen(true)
	const closeModal = () => setModalIsOpen(false)

	const { bestilling, onCloseButton } = props

	return (
		<div className="bestilling-resultat">
			<div className="status-header">
				<p>Bestilling #{bestilling.id}</p>
				<h3>Bestillingsstatus</h3>
				<div className="remove-button-container">
					<Button kind="remove-circle" onClick={() => onCloseButton(bestilling.id)} />
				</div>
			</div>
			<hr />
			<div className="miljoe-container miljoe-container-kolonne">
				<MiljoeStatus bestilling={bestilling} />
			</div>
			{bestilling.feil && (
				<div className="flexbox--all-center overall-feil-container">
					<Icon size="16px" kind="report-problem-triangle" />
					<p>{bestilling.feil} </p>
				</div>
			)}
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
