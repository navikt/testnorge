import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'

import { Identhistorikk } from './Identhistorikk'
import { Personinfo } from './Personinfo'

// TODO: Flyttes et mer logisk sted hvis den trengs flere steder
const relasjonTranslator = relasjon => {
	switch (relasjon) {
		case 'EKTEFELLE':
			return 'Partner'
		case 'PARTNER':
			return 'Partner'
		case 'MOR':
			return 'Mor'
		case 'FAR':
			return 'Far'
		case 'BARN':
			return 'Barn'
		case 'FOEDSEL':
			return 'Barn'
		default:
			return 'Ukjent relasjon'
	}
}

export const Relasjoner = ({ relasjoner }) => {
	if (!relasjoner) return false

	return (
		<div>
			<SubOverskrift label="Familierelasjoner" />
			<div className="person-visning_content">
				{relasjoner.map((relasjon, idx) => (
					<div key={idx}>
						<div className="title-multiple">
							<h3>{relasjonTranslator(relasjon.relasjonTypeNavn)}</h3>
						</div>
						<div>
							<Personinfo data={relasjon.personRelasjonMed} visTittel={false} />
							<Identhistorikk
								identhistorikk={relasjon.personRelasjonMed.identHistorikk}
								visTittel={false}
							/>
						</div>
					</div>
				))}
			</div>
		</div>
	)
}
