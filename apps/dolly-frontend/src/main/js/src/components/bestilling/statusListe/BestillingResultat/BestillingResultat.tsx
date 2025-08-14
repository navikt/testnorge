import Button from '@/components/ui/button/Button'
import { Feedback } from '@/components/feedback'
import ApiFeilmelding from '@/components/ui/apiFeilmelding/ApiFeilmelding'
import antallIdenterOpprettet from '@/components/bestilling/utils/antallIdenterOpprettet'
import { BestillingSammendragModal } from '@/components/bestilling/sammendrag/BestillingSammendragModal'

import './BestillingResultat.less'
import GjenopprettConnector from '@/components/bestilling/gjenopprett/GjenopprettBestillingConnector'
import useBoolean from '@/utils/hooks/useBoolean'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { Bestillingsstatus } from '@/utils/hooks/useDollyOrganisasjoner'
import { BestillingStatus } from '@/components/bestilling/statusListe/BestillingProgresjon/BestillingStatus'
import { TestComponentSelectors } from '#/mocks/Selectors'
import ConfettiExplosion from 'react-confetti-explosion'
import React, { useEffect, useState } from 'react'
import styled from 'styled-components'

const StyledConfettiExplosion = styled(ConfettiExplosion)`
	align-items: center;
	align-content: center;
	align-self: center;
	text-align: center;
`

const confettiDuration = 2800

type ResultatProps = {
	bestilling: Bestillingsstatus
	lukkBestilling: Function
	erOrganisasjon: boolean
}

const bestillingHarFeil = (bestilling: Bestillingsstatus) => {
	const statuser = bestilling?.status?.map((fagsystem) =>
		fagsystem.statuser?.some((status) => status?.melding !== 'OK'),
	)
	return statuser?.some((value) => value)
}

export default function BestillingResultat({
	bestilling,
	lukkBestilling,
	erOrganisasjon,
}: ResultatProps) {
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)
	const [showConfetti, setShowConfetti] = useState(false)
	const mutate = useMatchMutate()

	useEffect(() => {
		if (bestilling.ferdig && !bestilling.stoppet && !bestillingHarFeil(bestilling)) {
			setShowConfetti(true)
			const timer = setTimeout(() => {
				setShowConfetti(false)
			}, confettiDuration)
			return () => clearTimeout(timer)
		}
	}, [bestilling])

	const brukerId = bestilling?.bruker?.brukerId
	const antallOpprettet = antallIdenterOpprettet(bestilling)
	const harIdenterOpprettet = bestilling?.antallIdenterOpprettet > 0 || bestilling?.antallLevert > 0
	const disableGjenopprett =
		bestilling?.opprettetFraId ||
		bestilling?.opprettetFraGruppeId ||
		bestilling?.gjenopprettetFraIdent

	return (
		<div className="bestilling-status" key={`ferdig-bestilling-${bestilling.id}`}>
			<div className="bestilling-resultat">
				<div className="status-header">
					<p>Bestilling #{bestilling.id}</p>
					<h3>Bestillingsstatus</h3>
					<div className="status-header_button-wrap">
						<Button
							data-testid={TestComponentSelectors.BUTTON_LUKK_BESTILLING_RESULTAT}
							kind="remove-circle"
							onClick={() => {
								lukkBestilling(bestilling.id)
							}}
						/>
					</div>
				</div>
				<hr />
				<BestillingStatus bestilling={bestilling} erOrganisasjon={erOrganisasjon} />
				{antallOpprettet.harMangler && <span>{antallOpprettet.tekst}</span>}
				{bestilling.feil && <ApiFeilmelding feilmelding={bestilling.feil} container />}
				<Feedback
					label="Hvordan var din opplevelse med bruk av Dolly?"
					feedbackFor="Bruk av Dolly etter bestilling"
					etterBestilling={true}
				/>
				{showConfetti && (
					<div data-testid="confetti" style={{ display: 'flex', flexDirection: 'column' }}>
						<StyledConfettiExplosion particleCount={70} force={0.3} duration={confettiDuration} />
					</div>
				)}
				<div className="flexbox--all-center">
					<BestillingSammendragModal bestillinger={[bestilling]} />
					{harIdenterOpprettet && (
						<Button
							disabled={disableGjenopprett}
							title={
								disableGjenopprett
									? 'Kan ikke gjenopprette gjenoppretting av bestilling'
									: 'Gjenopprett'
							}
							onClick={openGjenopprettModal}
							kind="synchronize"
						>
							GJENOPPRETT
						</Button>
					)}
				</div>
				{isGjenopprettModalOpen && (
					<GjenopprettConnector
						bestilling={bestilling}
						brukerId={brukerId}
						closeModal={() => {
							closeGjenoprettModal()
							mutate(REGEX_BACKEND_GRUPPER)
						}}
					/>
				)}
			</div>
		</div>
	)
}
