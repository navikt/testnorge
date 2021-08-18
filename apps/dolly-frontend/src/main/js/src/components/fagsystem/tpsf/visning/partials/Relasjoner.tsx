import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

import { Barn } from './Barn'
import { Partner } from './Partner'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Foreldre } from './Foreldre'

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
	fornavn: string
	mellomnavn?: string
	etternavn: string
	kjonn: string
	alder: number
	doedsdato: Date
	foreldreType: string
	spesreg: string
	utenFastBopel: boolean
	boadresse: string
	postadresse: string
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
	const foreldre = relasjoner.filter(
		({ relasjonTypeNavn }) =>
			relasjonTypeNavn === RelasjonType.MOR || relasjonTypeNavn === RelasjonType.FAR
	)

	return (
		<React.Fragment>
			<SubOverskrift label="Familierelasjoner" iconKind="relasjoner" />
			<ErrorBoundary>
				<DollyFieldArray
					data={partnere}
					getHeader={getHeader('Partner')}
					header="Partner"
					expandable={partnere.length > 1}
				>
					{(partner: Relasjon, idx: number) => (
						<Partner key={idx} data={partner.personRelasjonMed} />
					)}
				</DollyFieldArray>
			</ErrorBoundary>

			<ErrorBoundary>
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
			</ErrorBoundary>

			<ErrorBoundary>
				<DollyFieldArray
					data={foreldre}
					getHeader={getHeader('Forelder')}
					header="Forelder"
					expandable={foreldre.length > 1}
				>
					{(forelder: Relasjon, idx: number) => (
						<Foreldre
							key={idx}
							person={forelder.personRelasjonMed}
							type={forelder.relasjonTypeNavn}
						/>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</React.Fragment>
	)
}
