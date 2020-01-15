import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'

import { Barn } from './Barn'
import { Partner } from './Partner'

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

export const Relasjoner = ({ relasjoner, bestilling }) => {
	if (!relasjoner) return false
	const barn = relasjoner.filter(
		relasjon => relasjonTranslator(relasjon.relasjonTypeNavn) === 'Barn'
	)
	const partnere = relasjoner.filter(
		relasjon => relasjonTranslator(relasjon.relasjonTypeNavn) === 'Partner'
	)

	return (
		<div>
			<SubOverskrift label="Familierelasjoner" iconKind="relasjoner" />
			<div>
				{partnere.map((partner, idx) => (
					<div key={idx}>
						<h3>
							Partner {idx + 1} {idx < 1 && '(nåværende/siste)'}
						</h3>
						<Partner data={partner.personRelasjonMed} />
					</div>
				))}
				{barn.map((barnet, jdx) => (
					<div key={jdx} className="title-multiple">
						<h3>Barn {jdx + 1}</h3>
						<Barn
							data={barnet.personRelasjonMed}
							type={barnet.relasjonTypeNavn}
							bestilling={bestilling.relasjoner.barn[jdx]}
						/>
					</div>
				))}
			</div>
		</div>
	)
}
