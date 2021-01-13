import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Button from '~/components/ui/button/Button'
import BestillingSammendrag from '~/components/bestilling/sammendrag/Sammendrag'
import GjenopprettConnector from '~/components/bestilling/gjenopprett/GjenopprettBestillingConnector'
import SendOpenAmConnector from '~/components/bestilling/sendOpenAm/SendOpenAmConnector'

import './Detaljer.less'
import { MalModal } from '~/pages/minSide/maler/MalModal'

export default function BestillingDetaljer({ bestilling, iLaastGruppe }) {
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)
	const [isOpenAmModalOpen, openOpenAmModal, closeOpenAmModal] = useBoolean(false)
	const [isOpenMalModalOpen, openOpenMalModal, closeOpenMalModal] = useBoolean(false)

	const alleredeSendtTilOpenAm = Boolean(bestilling.openamSent)
	const alleredeMal = Boolean(bestilling.malBestillingNavn)
	const harIdenterOpprettet = bestilling.antallIdenterOpprettet > 0

	return (
		<div className="bestilling-detaljer">
			<BestillingSammendrag bestilling={bestilling} />

			{harIdenterOpprettet && (
				<div className="flexbox--align-center--justify-end info-block">
					{!alleredeSendtTilOpenAm && (
						<Button onClick={openOpenAmModal} kind="chevron-right">
							SEND TIL OPENAM
						</Button>
					)}

					{!iLaastGruppe && (
						<Button onClick={openGjenopprettModal} kind="synchronize">
							GJENOPPRETT
						</Button>
					)}
					{!alleredeMal && (
						<Button onClick={openOpenMalModal} kind={'maler'}>
							OPPRETT NY MAL
						</Button>
					)}
				</div>
			)}

			{isOpenAmModalOpen && (
				<SendOpenAmConnector bestilling={bestilling} closeModal={closeOpenAmModal} />
			)}

			{isGjenopprettModalOpen && (
				<GjenopprettConnector bestilling={bestilling} closeModal={closeGjenoprettModal} />
			)}

			{isOpenMalModalOpen && <MalModal id={bestilling.id} closeModal={closeOpenMalModal} />}
		</div>
	)
}
