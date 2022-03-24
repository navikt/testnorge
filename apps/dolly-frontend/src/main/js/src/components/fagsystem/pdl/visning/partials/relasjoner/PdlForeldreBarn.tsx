import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { ForeldreBarnRelasjon } from '~/components/fagsystem/pdlf/PdlTypes'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import Formatters from '~/utils/DataFormatter'

type VisningProps = {
	relasjon: ForeldreBarnRelasjon
	idx?: number
}

type PdlForeldreBarnProps = {
	data: ForeldreBarnRelasjon[]
}

const Visning = ({ relasjon, idx }: VisningProps) => {
	return (
		<>
			<h4 style={{ width: '100%', marginTop: '0' }}>
				{Formatters.showLabel('pdlRelasjonTyper', relasjon.relatertPersonsRolle)}
			</h4>
			<div key={idx} className="person-visning_content">
				<TitleValue title="Ident" value={relasjon.relatertPersonsIdent} />
				{relasjon.relatertPersonsRolle === 'BARN' && (
					<TitleValue title="Forhold til barn" value={relasjon.minRolleForPerson} size="medium" />
				)}
			</div>
		</>
	)
}

export const PdlForeldreBarn = ({ data }: PdlForeldreBarnProps) => {
	if (!data || data.length === 0) return null

	return (
		<div>
			<SubOverskrift label="Barn/foreldre" iconKind="relasjoner" />
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(relasjon: ForeldreBarnRelasjon, idx: number) => (
						<Visning relasjon={relasjon} idx={idx} />
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
