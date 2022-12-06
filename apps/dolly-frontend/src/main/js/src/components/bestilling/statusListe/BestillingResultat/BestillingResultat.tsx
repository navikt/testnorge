import Button from '@/components/ui/button/Button'
import { Feedback } from '@/components/feedback'
import ApiFeilmelding from '@/components/ui/apiFeilmelding/ApiFeilmelding'
import FagsystemStatus from './FagsystemStatus/FagsystemStatus'
import antallIdenterOpprettet from '@/components/bestilling/utils/antallIdenterOpprettet'
import { BestillingSammendragModal } from '@/components/bestilling/sammendrag/BestillingSammendragModal'

import './BestillingResultat.less'
import GjenopprettConnector from '@/components/bestilling/gjenopprett/GjenopprettBestillingConnector'
import useBoolean from '@/utils/hooks/useBoolean'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { Bestillingsstatus } from '@/utils/hooks/useOrganisasjoner'

type ResultatProps = {
	bestilling: Bestillingsstatus
	setNyeBestillinger: Function
	lukkBestilling: Function
}

export default function BestillingResultat({
	bestilling,
	setNyeBestillinger,
	lukkBestilling,
}: ResultatProps) {
	const brukerId = bestilling?.bruker?.brukerId
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)

	const antallOpprettet = antallIdenterOpprettet(bestilling)
	const mutate = useMatchMutate()

	return (
		<div className="bestilling-status" key={`ferdig-bestilling-${bestilling.id}`}>
			<div className="bestilling-resultat">
				<div className="status-header">
					<p>Bestilling #{bestilling.id}</p>
					<h3>Bestillingsstatus</h3>
					<div className="status-header_button-wrap">
						<Button
							kind="remove-circle"
							onClick={() => {
								setNyeBestillinger((nyeBestillinger: Bestillingsstatus[]) =>
									nyeBestillinger.filter((best) => best.id !== bestilling.id)
								)
								lukkBestilling && lukkBestilling(bestilling.id)
							}}
						/>
					</div>
				</div>
				<hr />
				{/*// @ts-ignore*/}
				<FagsystemStatus bestilling={bestilling} />
				{antallOpprettet.harMangler && <span>{antallOpprettet.tekst}</span>}
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
							mutate(REGEX_BACKEND_GRUPPER)
						}}
					/>
				)}
			</div>
		</div>
	)
}
