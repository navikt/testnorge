import React from 'react'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { HentPerson } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { PdlBarn } from '~/components/fagsystem/pdl/visning/partials/relasjoner/PdlBarn'
import { PdlForeldre } from '~/components/fagsystem/pdl/visning/partials/relasjoner/PdlForeldre'
import { PdlPartner } from '~/components/fagsystem/pdl/visning/partials/relasjoner/PdlPartner'
import { ForeldreBarnRelasjon, Rolle, Sivilstand } from '~/components/fagsystem/pdlf/PdlTypes'

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
		(relasjon: ForeldreBarnRelasjon) => relasjon.relatertPersonsRolle == Rolle.BARN
	)
	const doedfoedtBarn = data.doedfoedtBarn
	const foreldre = data.forelderBarnRelasjon?.filter(
		(relasjon: ForeldreBarnRelasjon) =>
			relasjon.relatertPersonsRolle == Rolle.MOR ||
			relasjon.relatertPersonsRolle == Rolle.FAR ||
			relasjon.relatertPersonsRolle == Rolle.MEDMOR
	)

	if (
		hasNoValues(partnere) &&
		hasNoValues(barn) &&
		hasNoValues(foreldre) &&
		hasNoValues(doedfoedtBarn)
	)
		return null

	return (
		<ErrorBoundary>
			<div>
				{visTittel && <SubOverskrift label="Familierelasjoner" iconKind="relasjoner" />}
				<PdlPartner data={partnere} />
				<PdlBarn barn={barn} doedfoedtBarn={doedfoedtBarn} />
				<PdlForeldre data={foreldre} />
			</div>
		</ErrorBoundary>
	)
}

export const hasNoValues = (liste: any[]) => {
	return !liste || liste.length === 0
}
