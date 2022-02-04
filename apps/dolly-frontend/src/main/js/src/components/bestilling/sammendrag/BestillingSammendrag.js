import React from 'react'
import Bestillingskriterier from './kriterier/Bestillingskriterier'
import MiljoeStatus from './miljoeStatus/MiljoeStatus'
import JiraLenker from '~/components/bestilling/jiraLenker/JiraLenker'

export default function BestillingSammendrag({ bestilling }) {
	if ((!bestilling.antallIdenter || !bestilling.gruppeId) && !bestilling.organisasjonNummer) {
		return (
			<div className="bestilling-detaljer">
				<MiljoeStatus bestilling={bestilling} />
			</div>
		)
	}
	return (
		<div className="bestilling-detaljer">
			<MiljoeStatus bestilling={bestilling} />
			<Bestillingskriterier
				bestilling={bestilling.bestilling}
				bestillingsinformasjon={{
					antallIdenter: bestilling.antallIdenter,
					sistOppdatert: bestilling.sistOppdatert,
					opprettetFraId: bestilling.opprettetFraId,
					opprettetFraGruppeId: bestilling.opprettetFraGruppeId,
					navSyntetiskIdent:
						bestilling.bestilling.pdldata?.opprettNyPerson?.syntetisk ||
						bestilling.bestilling.navSyntetiskIdent,
					beskrivelse: bestilling.bestilling.beskrivelse,
				}}
				header="Bestillingskriterier"
			/>
			<JiraLenker openAm={bestilling.openamSent && bestilling.openamSent.split(',')} />
		</div>
	)
}
