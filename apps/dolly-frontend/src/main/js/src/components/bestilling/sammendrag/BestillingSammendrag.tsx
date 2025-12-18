import MiljoeStatus from './miljoeStatus/MiljoeStatus'
import React, { Suspense } from 'react'
import Loading from '@/components/ui/loading/Loading'
import { BestillingKriterier } from '@/components/bestilling/sammendrag/BestillingKriterier'

const Bestillingskriterier = React.lazy(
	() => import('@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'),
)

export default function BestillingSammendrag({
	bestilling,
	closeModal,
}: {
	bestilling: any
	closeModal?: () => void
}) {
	// const findFirstIdent = (bestilling: any) => {
	// 	if (bestilling?.organisasjonNummer) {
	// 		return null
	// 	}
	// 	for (const status of bestilling.status) {
	// 		for (const statusDetail of status.statuser) {
	// 			if (statusDetail.identer && statusDetail.identer.length > 0) {
	// 				return statusDetail.identer[0]
	// 			}
	// 		}
	// 	}
	// 	return null
	// }
	// console.log('bestilling: ', bestilling) //TODO - SLETT MEG
	return (
		// <div className="bestilling-detaljer">
		<>
			<MiljoeStatus bestilling={bestilling} closeModal={closeModal} />
			<Suspense fallback={<Loading label={'Laster bestillingskriterier...'} />}>
				<BestillingKriterier bestilling={bestilling} />
				{/*<Bestillingsvisning bestilling={bestilling.bestilling} />*/}
				{/*<Bestillingskriterier*/}
				{/*	firstIdent={findFirstIdent(bestilling)}*/}
				{/*	bestilling={bestilling.bestilling}*/}
				{/*	bestillingsinformasjon={{*/}
				{/*		antallIdenter: bestilling.antallIdenter,*/}
				{/*		antallLevert: bestilling.antallLevert,*/}
				{/*		sistOppdatert: bestilling.sistOppdatert,*/}
				{/*		opprettetFraId: bestilling.opprettetFraId,*/}
				{/*		opprettetFraGruppeId: bestilling.opprettetFraGruppeId,*/}
				{/*		navSyntetiskIdent: bestilling?.bestilling?.pdldata?.opprettNyPerson?.syntetisk,*/}
				{/*		id2032: bestilling?.bestilling?.pdldata?.opprettNyPerson?.id2032,*/}
				{/*		beskrivelse: bestilling?.bestilling?.beskrivelse,*/}
				{/*	}}*/}
				{/*	header="Bestillingskriterier"*/}
				{/*/>*/}
			</Suspense>
		</>
		// </div>
		// )
	)
}
