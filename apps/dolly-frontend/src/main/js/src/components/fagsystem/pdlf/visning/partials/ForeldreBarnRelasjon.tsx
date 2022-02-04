import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { RelatertPerson } from '~/components/fagsystem/pdlf/visning/partials/RelatertPerson'
import { ForeldreBarnRelasjon, Relasjon } from '~/components/fagsystem/pdlf/PdlTypes'

type FamilieRelasjonerData = {
	data: Array<ForeldreBarnRelasjon>
	relasjoner: Array<Relasjon>
}

type VisningData = {
	data: ForeldreBarnRelasjon
	relasjoner: Array<Relasjon>
}

export const Visning = ({ data, relasjoner }: VisningData) => {
	const relatertPersonIdent = data.relatertPerson || data.relatertPersonsIdent
	const relasjon = relasjoner?.find((item) => item.relatertPerson?.ident === relatertPersonIdent)

	return (
		<>
			<ErrorBoundary>
				<div className="person-visning_content">
					{!relasjoner && <TitleValue title="Relatert person" value={relatertPersonIdent} />}
				</div>
				{relasjon && (
					<RelatertPerson
						data={relasjon.relatertPerson}
						tittel={Formatters.showLabel('pdlRelasjonTyper', data.relatertPersonsRolle)}
					/>
				)}
			</ErrorBoundary>
		</>
	)
}

export const ForelderBarnRelasjonVisning = ({ data, relasjoner }: FamilieRelasjonerData) => {
	if (!data || data.length < 1) return null

	return (
		<div>
			<SubOverskrift label="Barn/foreldre" iconKind="relasjoner" />

			<DollyFieldArray data={data} nested>
				{(foreldreBarnRelasjon: ForeldreBarnRelasjon) => (
					<Visning
						key={foreldreBarnRelasjon.id}
						data={foreldreBarnRelasjon}
						relasjoner={relasjoner}
					/>
				)}
			</DollyFieldArray>
		</div>
	)
}
