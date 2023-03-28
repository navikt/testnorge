import Bestillingskriterier from './kriterier/Bestillingskriterier'
import MiljoeStatus from './miljoeStatus/MiljoeStatus'

export default function BestillingSammendrag({ bestilling }) {
	return (
		<div className="bestilling-detaljer">
			<MiljoeStatus bestilling={bestilling} />
			<Bestillingskriterier
				bestilling={bestilling.bestilling}
				bestillingsinformasjon={{
					antallIdenter: bestilling.antallIdenter,
					antallLevert: bestilling.antallLevert,
					sistOppdatert: bestilling.sistOppdatert,
					opprettetFraId: bestilling.opprettetFraId,
					opprettetFraGruppeId: bestilling.opprettetFraGruppeId,
					navSyntetiskIdent: bestilling.bestilling.pdldata?.opprettNyPerson?.syntetisk,
					beskrivelse: bestilling.bestilling.beskrivelse,
				}}
				header="Bestillingskriterier"
			/>
		</div>
	)
}
