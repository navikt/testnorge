import Button from '@/components/ui/button/Button'
import { Feedback } from '@/components/feedback'
import ApiFeilmelding from '@/components/ui/apiFeilmelding/ApiFeilmelding'
import antallIdenterOpprettet from '@/components/bestilling/utils/antallIdenterOpprettet'
import { BestillingSammendragModal } from '@/components/bestilling/sammendrag/BestillingSammendragModal'

import './BestillingResultat.less'
import GjenopprettConnector from '@/components/bestilling/gjenopprett/GjenopprettBestillingConnector'
import useBoolean from '@/utils/hooks/useBoolean'
import { REGEX_BACKEND_GRUPPER, useMatchMutate } from '@/utils/hooks/useMutate'
import { Bestillingsstatus } from '@/utils/hooks/useOrganisasjoner'
import { BestillingStatus } from '@/components/bestilling/statusListe/BestillingProgresjon/BestillingStatus'

type ResultatProps = {
	bestilling: Bestillingsstatus
	lukkBestilling: Function
	erOrganisasjon: boolean
}

export default function BestillingResultat({
	bestilling,
	lukkBestilling,
	erOrganisasjon,
}: ResultatProps) {
	const brukerId = bestilling?.bruker?.brukerId
	const [isGjenopprettModalOpen, openGjenopprettModal, closeGjenoprettModal] = useBoolean(false)

	const antallOpprettet = antallIdenterOpprettet(bestilling)
	const harIdenterOpprettet = bestilling?.antallIdenterOpprettet > 0 || bestilling?.antallLevert > 0
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
								lukkBestilling(bestilling.id)
							}}
						/>
					</div>
				</div>
				<hr />
				{/*// @ts-ignore*/}
				<BestillingStatus bestilling={bestilling} erOrganisasjon={erOrganisasjon} />
				{antallOpprettet.harMangler && <span>{antallOpprettet.tekst}</span>}
				{bestilling.feil && <ApiFeilmelding feilmelding={bestilling.feil} container />}
				<Feedback
					label="Hvordan var din opplevelse med bruk av Dolly?"
					feedbackFor="Bruk av Dolly etter bestilling"
				/>
				<div className="flexbox--all-center">
					<BestillingSammendragModal bestilling={bestilling} />
					{harIdenterOpprettet && (
						<Button onClick={openGjenopprettModal} kind="synchronize">
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
