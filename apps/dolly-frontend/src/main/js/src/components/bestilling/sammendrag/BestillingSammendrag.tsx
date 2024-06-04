import MiljoeStatus from './miljoeStatus/MiljoeStatus'
import React, { Suspense } from 'react'
import Loading from '@/components/ui/loading/Loading'

const Bestillingskriterier = React.lazy(
	() => import('@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'),
)

export default function BestillingSammendrag({ bestilling }) {
	const findFirstIdent = (bestilling: any) => {
		for (const status of bestilling.status) {
			for (const statusDetail of status.statuser) {
				if (statusDetail.identer && statusDetail.identer.length > 0) {
					return statusDetail.identer[0]
				}
			}
		}
		return null
	}

	return (
		<div className="bestilling-detaljer">
			<MiljoeStatus bestilling={bestilling} />
			<Suspense fallback={<Loading label={'Laster bestillingskriterier...'} />}>
				<Bestillingskriterier
					firstIdent={findFirstIdent(bestilling)}
					bestilling={bestilling.bestilling}
					bestillingsinformasjon={{
						antallIdenter: bestilling.antallIdenter,
						antallLevert: bestilling.antallLevert,
						sistOppdatert: bestilling.sistOppdatert,
						opprettetFraId: bestilling.opprettetFraId,
						opprettetFraGruppeId: bestilling.opprettetFraGruppeId,
						navSyntetiskIdent: bestilling?.bestilling?.pdldata?.opprettNyPerson?.syntetisk,
						beskrivelse: bestilling?.bestilling?.beskrivelse,
					}}
					header="Bestillingskriterier"
				/>
			</Suspense>
		</div>
	)
}
