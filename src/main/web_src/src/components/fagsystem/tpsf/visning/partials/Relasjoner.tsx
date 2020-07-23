import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

import { Barn } from './Barn'
import { Partner } from './Partner'

type RelasjonerProps = {
	relasjoner: Array<Relasjon>
}

type Relasjon = {
	relasjonTypeNavn: RelasjonType
	personRelasjonMed: Person
}

type Person = {
	ident: string
	identtype: string
}

enum RelasjonType {
	EKTEFELLE = 'EKTEFELLE',
	PARTNER = 'PARTNER',
	MOR = 'MOR',
	FAR = 'FAR',
	BARN = 'BARN',
	FOEDSEL = 'FOEDSEL'
}

const getHeader = (navn: string) => {
	return (relasjon: Relasjon) => {
		if (
			navn.toUpperCase() === RelasjonType.BARN &&
			relasjon.personRelasjonMed.identtype === 'FDAT'
		) {
			return `${navn} - Dødfødt (${relasjon.personRelasjonMed.ident})`
		}
		return `${navn} (${relasjon.personRelasjonMed.ident})`
	}
}

export const Relasjoner = ({ relasjoner }: RelasjonerProps) => {
	if (!relasjoner || relasjoner.length < 1) return null

	const barn = relasjoner.filter(
		({ relasjonTypeNavn }) =>
			relasjonTypeNavn === RelasjonType.BARN || relasjonTypeNavn === RelasjonType.FOEDSEL
	)
	const partnere = relasjoner.filter(
		({ relasjonTypeNavn }) =>
			relasjonTypeNavn === RelasjonType.EKTEFELLE || relasjonTypeNavn === RelasjonType.PARTNER
	)

	return (
		<React.Fragment>
			<SubOverskrift label="Familierelasjoner" iconKind="relasjoner" />
			<DollyFieldArray
				data={partnere}
				getHeader={getHeader('Partner')}
				header="Partner"
				expandable={partnere.length > 1}
			>
				{(partner: Relasjon, idx: number) => <Partner key={idx} data={partner.personRelasjonMed} />}
			</DollyFieldArray>

			<DollyFieldArray
				data={barn}
				getHeader={getHeader('Barn')}
				header="Barn"
				expandable={barn.length > 1}
			>
				{(barnet: Relasjon, idx: number) => (
					<Barn key={idx} data={barnet.personRelasjonMed} type={barnet.relasjonTypeNavn} />
				)}
			</DollyFieldArray>
		</React.Fragment>
	)
}
