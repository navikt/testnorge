import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'

import { Barn } from './Barn'
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
	let barnNr = 0,
		partnerNr = 0
	return (
		<div>
			<SubOverskrift label="Familierelasjoner" />
			<div className="person-visning_content">
				{relasjoner.map((relasjon, idx) => {
					const relasjonstype = relasjonTranslator(relasjon.relasjonTypeNavn)
					return (
						<div key={idx}>
							<div className="title-multiple"></div>
							{relasjonstype === 'Barn' && (
								<React.Fragment>
									<h3>
										{relasjonstype} {++barnNr}
									</h3>
									<Barn data={relasjon.personRelasjonMed} />
								</React.Fragment>
							)}
							{relasjonstype === 'Partner' && (
								<React.Fragment>
									<h3>
										{relasjonstype} {++partnerNr}
									</h3>
									{/* Legg til partneregenskaper */}
									<Personinfo data={relasjon.personRelasjonMed} />
								</React.Fragment>
							)}
						</div>
					)
				})}
			</div>
		</div>
	)
}
