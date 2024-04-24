import MiljoeStatus from './miljoeStatus/MiljoeStatus'
import React, { Suspense } from 'react'
import Loading from '@/components/ui/loading/Loading'

const Bestillingskriterier = React.lazy(
	() => import('@/components/bestilling/sammendrag/kriterier/Bestillingskriterier'),
)

export default function BestillingSammendrag({ bestilling }) {
	return (
		<div className="bestilling-detaljer">
			<MiljoeStatus bestilling={bestilling} />
			<Suspense fallback={<Loading label={'Laster bestillingskriterier...'} />}>
				<Bestillingskriterier
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
