import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Button from '~/components/ui/button/Button'
import BestillingSammendrag from '~/components/bestilling/sammendrag/Sammendrag'
import GjenopprettConnector from '~/components/bestilling/gjenopprett/GjenopprettConnector'
import SendOpenAmConnector from '~/components/bestilling/sendOpenAm/SendOpenAmConnector'

import './Detaljer.less'

export default function BestillingDetaljer({ bestilling }) {
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)
	const [isOpenAmModalOpen, openOpenAmModal, closeOpenAmModal] = useBoolean(false)

	const alleredeSendtTilOpenAm = Boolean(bestilling.openamSent)

	const harIdenterOpprettet = bestilling.antallIdenterOpprettet > 0
	const btnCss = 'flexbox--align-center'

	return (
		<div className="bestilling-detaljer">
			<BestillingSammendrag bestilling={bestilling} />

			{harIdenterOpprettet && (
				<div className="flexbox--align-center--justify-end info-block">
					{!alleredeSendtTilOpenAm && (
						<Button className={btnCss} onClick={openOpenAmModal} kind="chevron-right">
							SEND TIL OPENAM
						</Button>
					)}

					<Button onClick={openGjenopprettModal} className={btnCss} kind="synchronize">
						GJENOPPRETT
					</Button>
				</div>
			)}

			{isOpenAmModalOpen && (
				<SendOpenAmConnector bestilling={bestilling} closeModal={closeOpenAmModal} />
			)}

			{isGjenopprettModalOpen && (
				<GjenopprettConnector closeModal={closeGjenoprettModal} bestilling={bestilling} />
			)}
		</div>
	)
}
