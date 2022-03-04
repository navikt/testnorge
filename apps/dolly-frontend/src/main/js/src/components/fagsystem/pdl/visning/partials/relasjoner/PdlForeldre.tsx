import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { ForeldreBarnRelasjon } from '~/components/fagsystem/pdlf/PdlTypes'

type VisningProps = {
	forelder: ForeldreBarnRelasjon
	idx?: number
}

type PdlForeldreProps = {
	data: ForeldreBarnRelasjon[]
}

const Visning = ({ forelder, idx }: VisningProps) => {
	return (
		<div key={idx} className="person-visning_content">
			<TitleValue title="Foreldretype" value={forelder.relatertPersonsRolle} />
			<TitleValue title="Ident" value={forelder.relatertPersonsIdent} />
		</div>
	)
}

export const PdlForeldre = ({ data }: PdlForeldreProps) => {
	if (!data || data.length === 0) return null

	return (
		<div>
			<ErrorBoundary>
				<DollyFieldArray header="Foreldre" data={data} nested>
					{(forelder: ForeldreBarnRelasjon, idx: number) => (
						<Visning forelder={forelder} idx={idx} />
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
