import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { PdlPartner } from '~/components/fagsystem/pdl/visning/partials/PdlPartner'
import {
	ForelderBarnRelasjon,
	HentPerson,
	Rolle,
	Sivilstand,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { PdlBarn } from '~/components/fagsystem/pdl/visning/partials/PdlBarn'
import { PdlForeldre } from '~/components/fagsystem/pdl/visning/partials/PdlForeldre'

type PdlRelasjonerProps = {
	data: HentPerson
	visTittel?: boolean
}

export const PdlRelasjoner = ({ data, visTittel = true }: PdlRelasjonerProps) => {
	if (!data) {
		return null
	}

	const partnere = data.sivilstand?.filter((sivilstand: Sivilstand) => sivilstand.type !== 'UGIFT')
	const barn = data.forelderBarnRelasjon?.filter(
		(relasjon: ForelderBarnRelasjon) => relasjon.relatertPersonsRolle == Rolle.BARN
	)
	const foreldre = data.forelderBarnRelasjon?.filter(
		(relasjon: ForelderBarnRelasjon) =>
			relasjon.relatertPersonsRolle == Rolle.MOR ||
			relasjon.relatertPersonsRolle == Rolle.FAR ||
			relasjon.relatertPersonsRolle == Rolle.MEDMOR
	)

	if (hasNoValues(partnere) && hasNoValues(barn) && hasNoValues(foreldre)) return null

	return (
		<ErrorBoundary>
			<div>
				{visTittel && <SubOverskrift label="Familierelasjoner" iconKind="relasjoner" />}
				<PdlPartner data={partnere} />
				<PdlBarn data={barn} />
				<PdlForeldre data={foreldre} />
			</div>
		</ErrorBoundary>
	)
}

const hasNoValues = (liste: any[]) => {
	return !liste || liste.length === 0
}
