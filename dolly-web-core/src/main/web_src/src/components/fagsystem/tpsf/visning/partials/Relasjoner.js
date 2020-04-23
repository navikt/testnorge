import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

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

export const Relasjoner = ({ relasjoner }) => {
	if (!relasjoner) return false
	const barn = relasjoner.filter(
		relasjon => relasjonTranslator(relasjon.relasjonTypeNavn) === 'Barn'
	)
	const partnere = relasjoner.filter(
		relasjon => relasjonTranslator(relasjon.relasjonTypeNavn) === 'Partner'
	)

	return (
		<React.Fragment>
			<SubOverskrift label="Familierelasjoner" iconKind="relasjoner" />
			<DollyFieldArray data={partnere} header="Partner" expandable={partnere.length > 1}>
				{(partner, idx) => <Partner key={idx} data={partner.personRelasjonMed} />}
			</DollyFieldArray>

			<DollyFieldArray data={barn} header="Barn" expandable={barn.length > 1}>
				{(barnet, idx) => (
					<Barn key={idx} data={barnet.personRelasjonMed} type={barnet.relasjonTypeNavn} />
				)}
			</DollyFieldArray>
		</React.Fragment>
	)
}
