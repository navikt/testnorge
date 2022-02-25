import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { RelatertPerson } from '~/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { Relasjon } from '~/components/fagsystem/pdlf/PdlTypes'

type IdenthistorikkData = {
	relasjoner: Array<Relasjon>
}

export const IdenthistorikkVisning = ({ relasjoner }: IdenthistorikkData) => {
	const historiskeIdenter = relasjoner?.filter(
		(relasjon) => relasjon.relasjonType === 'GAMMEL_IDENTITET'
	)
	if (!historiskeIdenter || historiskeIdenter.length < 1) return null

	return (
		<div>
			<SubOverskrift label="Identhistorikk" iconKind="identhistorikk" />
			<DollyFieldArray data={historiskeIdenter} nested>
				{(ident: Relasjon) => (
					<RelatertPerson data={ident?.relatertPerson} tittel={'Gammel ident'} />
				)}
			</DollyFieldArray>
		</div>
	)
}
