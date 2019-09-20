import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Button from '~/components/ui/button/Button'
import BestillingSammendrag from '~/components/bestilling/sammendrag/Sammendrag'
import GjenopprettConnector from '~/components/bestilling/gjenopprett/GjenopprettConnector'
import SendOpenAm from '~/components/bestilling/sendOpenAm/SendOpenAm'

import './BestillingDetaljer.less'

export default function BestillingDetaljer({ bestilling }) {
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)
	const [isOpenAmModalOpen, openOpenAmModal, closeOpenAmModal] = useBoolean(false)

	const alleredeSendtTilOpenAm = Boolean(bestilling.openamSent)

	// Flytte denne?
	const _erIdentOpprettet = () => {
		const { statusrapport } = bestilling
		let opprettet = false
		if (statusrapport.length) {
			const tpsf = statusrapport.find(f => f.id === 'tpsfStatus')
			if (tpsf) {
				opprettet = tpsf.statuser.some(s => s.detaljert)
			}
		}
		return opprettet
	}

	const harIdenterOpprettet = _erIdentOpprettet()

	return (
		<div className="bestilling-detaljer">
			<BestillingSammendrag bestilling={bestilling} />

			{harIdenterOpprettet && (
				<div className="flexbox--align-center--justify-end info-block">
					{!alleredeSendtTilOpenAm && (
						<Button
							className="flexbox--align-center"
							onClick={openOpenAmModal}
							kind="chevron-right"
						>
							SEND TIL OPENAM
						</Button>
					)}

					<Button
						onClick={openGjenopprettModal}
						className="flexbox--align-center"
						kind="synchronize"
					>
						GJENOPPRETT
					</Button>
				</div>
			)}

			{isOpenAmModalOpen && (
				<SendOpenAm bestillingId={bestilling.id} closeModal={closeOpenAmModal} />
			)}

			{isGjenopprettModalOpen && (
				<GjenopprettConnector closeModal={closeGjenoprettModal} bestilling={bestilling} />
			)}
		</div>
	)
}
