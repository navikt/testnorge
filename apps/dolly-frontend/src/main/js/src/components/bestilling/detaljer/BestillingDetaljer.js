import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Button from '~/components/ui/button/Button'
import BestillingSammendrag from '~/components/bestilling/sammendrag/BestillingSammendrag'
import GjenopprettConnector from '~/components/bestilling/gjenopprett/GjenopprettBestillingConnector'
import SendOpenAmConnector from '~/components/bestilling/sendOpenAm/SendOpenAmConnector'

import './BestillingDetaljer.less'
import { MalModal } from '~/pages/minSide/maler/MalModal'

export default function BestillingDetaljer({ bestilling, iLaastGruppe, brukerId }) {
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)
	const [isOpenAmModalOpen, openOpenAmModal, closeOpenAmModal] = useBoolean(false)
	const [isOpenMalModalOpen, openOpenMalModal, closeOpenMalModal] = useBoolean(false)

	const alleredeSendtTilOpenAm = Boolean(bestilling.openamSent)
	const alleredeMal = Boolean(bestilling.malBestillingNavn)
	const harIdenterOpprettet = bestilling.antallIdenterOpprettet > 0
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')

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

			{erOrganisasjon && (
				<div className="flexbox--align-center--justify-end info-block">
					<Button
						onClick={openGjenopprettModal}
						kind="synchronize"
						disabled={!bestilling.ferdig}
						title={
							!bestilling.ferdig ? 'Bestillingen kan ikke gjenopprettes før den er ferdig' : null
						}
					>
						GJENOPPRETT
					</Button>
				</div>
			)}

			{isOpenAmModalOpen && (
				<SendOpenAmConnector bestilling={bestilling} closeModal={closeOpenAmModal} />
			)}

			{isGjenopprettModalOpen && (
				<GjenopprettConnector
					bestilling={bestilling}
					brukerId={brukerId && brukerId}
					closeModal={closeGjenoprettModal}
				/>
			)}

			{isOpenMalModalOpen && <MalModal id={bestilling.id} closeModal={closeOpenMalModal} />}
		</div>
	)
}
