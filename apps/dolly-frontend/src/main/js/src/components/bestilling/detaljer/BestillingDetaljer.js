import React from 'react'
import useBoolean from '~/utils/hooks/useBoolean'
import Button from '~/components/ui/button/Button'
import BestillingSammendrag from '~/components/bestilling/sammendrag/BestillingSammendrag'
import GjenopprettConnector from '~/components/bestilling/gjenopprett/GjenopprettBestillingConnector'

import './BestillingDetaljer.less'
import { MalModal } from '~/pages/minSide/maler/MalModal'
import { NAVIGER_BESTILLING_ID } from '~/pages/gruppe/PersonVisning/TidligereBestillinger/TidligereBestillinger'

export default function BestillingDetaljer({ bestilling, iLaastGruppe, brukerId, brukertype }) {
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)
	const [isOpenMalModalOpen, openOpenMalModal, closeOpenMalModal] = useBoolean(false)

	const alleredeMal = Boolean(bestilling.malBestillingNavn)
	const harIdenterOpprettet = bestilling.antallIdenterOpprettet > 0
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')

	if (sessionStorage.getItem(NAVIGER_BESTILLING_ID)) {
		sessionStorage.removeItem(NAVIGER_BESTILLING_ID)
	}

	return (
		<div className="bestilling-detaljer">
			<BestillingSammendrag bestilling={bestilling} />

			{harIdenterOpprettet && (
				<div className="flexbox--align-center--justify-end info-block">
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

			{isGjenopprettModalOpen && (
				<GjenopprettConnector
					bestilling={bestilling}
					brukerId={brukerId && brukerId}
					closeModal={closeGjenoprettModal}
					brukertype={brukertype}
				/>
			)}

			{isOpenMalModalOpen && <MalModal id={bestilling.id} closeModal={closeOpenMalModal} />}
		</div>
	)
}
