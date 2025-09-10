import useBoolean from '@/utils/hooks/useBoolean'
import Button from '@/components/ui/button/Button'
import BestillingSammendrag from '@/components/bestilling/sammendrag/BestillingSammendrag'
import GjenopprettConnector from '@/components/bestilling/gjenopprett/GjenopprettBestillingConnector'

import './BestillingDetaljer.less'
import { MalModal, malTyper } from '@/pages/minSide/maler/MalModal'
import * as _ from 'lodash-es'
import { SlettButton } from '@/components/ui/button/SlettButton/SlettButton'
import React from 'react'
import { DollyApi } from '@/service/Api'
import { TestComponentSelectors } from '#/mocks/Selectors'

export default function BestillingDetaljer({ bestilling, iLaastGruppe, brukerId, brukertype }) {
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)
	const [isMalModalOpen, openMalModal, closeMalModal] = useBoolean(false)

	const alleredeMal = Boolean(bestilling.malBestillingNavn)
	const harIdenterOpprettet = bestilling.antallIdenterOpprettet > 0 || bestilling.antallLevert > 0
	const erOrganisasjon = bestilling.hasOwnProperty('organisasjonNummer')

	const sivilstand = _.get(bestilling, 'bestilling.pdldata.person.sivilstand')
	const harRelatertPersonVedSivilstand = sivilstand?.some((item) => item.relatertVedSivilstand)
	const harLevertPersoner = bestilling.antallLevert > 0

	const nyIdent = _.get(bestilling, 'bestilling.pdldata.person.nyident')
	const harEksisterendeNyIdent = nyIdent?.some((item) => item.eksisterendeIdent)

	const forelderBarnRelasjon = _.get(bestilling, 'bestilling.pdldata.person.forelderBarnRelasjon')
	const harRelatertPersonBarn = forelderBarnRelasjon?.some((item) => item.relatertPerson)

	const gjenopprettingsId =
		bestilling?.opprettetFraGruppeId ||
		bestilling?.opprettetFraId ||
		bestilling?.gjenopprettetFraIdent

	const gjenopprettTitle = gjenopprettingsId
		? 'Kan ikke gjenopprette gjenoppretting av bestilling'
		: harLevertPersoner
			? 'Gjenopprett bestilling'
			: 'Kan ikke gjenopprette bestilling fordi den har ingen leverte identer'

	return (
		<div className="bestilling-detaljer">
			<BestillingSammendrag bestilling={bestilling} />

			{harIdenterOpprettet && !erOrganisasjon && (
				<div className="flexbox--align-center--justify-end info-block">
					{!iLaastGruppe && (
						<Button
							data-testid={TestComponentSelectors.BUTTON_BESTILLINGDETALJER_GJENOPPRETT}
							onClick={openGjenopprettModal}
							kind="synchronize"
							disabled={!harLevertPersoner || gjenopprettingsId}
							title={gjenopprettTitle}
						>
							GJENOPPRETT
						</Button>
					)}
					{!alleredeMal &&
						!harRelatertPersonVedSivilstand &&
						!harEksisterendeNyIdent &&
						!harRelatertPersonBarn &&
						!gjenopprettingsId && (
							<Button
								data-testid={TestComponentSelectors.BUTTON_BESTILLINGDETALJER_OPPRETT_MAL}
								onClick={openMalModal}
								kind={'maler'}
								className="svg-icon-blue"
							>
								OPPRETT MAL
							</Button>
						)}
					<SlettButton
						bestillingId={bestilling.id}
						action={DollyApi.slettBestilling}
						loading={false}
						navigateHome={false}
					>
						Er du sikker på at du vil slette denne bestillingen?
					</SlettButton>
				</div>
			)}

			{erOrganisasjon && (
				<div className="flexbox--align-center--justify-end info-block">
					<Button
						onClick={openGjenopprettModal}
						kind="synchronize"
						disabled={!bestilling.ferdig}
						title={
							!bestilling.ferdig
								? 'Bestillingen kan ikke gjenopprettes før den er ferdig'
								: undefined
						}
					>
						GJENOPPRETT
					</Button>
					<Button onClick={openMalModal} kind={'maler'} className="svg-icon-blue">
						OPPRETT MAL
					</Button>
				</div>
			)}

			{isGjenopprettModalOpen && (
				<GjenopprettConnector
					bestilling={bestilling}
					brukerId={brukerId}
					closeModal={closeGjenoprettModal}
					brukertype={brukertype}
				/>
			)}

			{isMalModalOpen && (
				<MalModal
					id={bestilling.id}
					malType={erOrganisasjon ? malTyper.ORGANISASJON : malTyper.BESTILLING}
					closeModal={closeMalModal}
				/>
			)}
		</div>
	)
}
