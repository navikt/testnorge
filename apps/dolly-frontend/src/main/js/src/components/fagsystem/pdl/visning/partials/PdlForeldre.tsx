import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ForelderBarnRelasjon } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import React from 'react'

type VisningProps = {
	forelder: ForelderBarnRelasjon
	idx?: number
}

type PdlForeldreProps = {
	data: ForelderBarnRelasjon[]
}

const Visning = ({ forelder, idx }: VisningProps) => {
	return (
		<div key={idx} className="person-visning_content">
			<TitleValue title="Ident" value={forelder.relatertPersonsIdent} />
			<TitleValue title="Foreldretype" value={forelder.relatertPersonsRolle} size="medium" />
		</div>
	)
}

export const PdlForeldre = ({ data }: PdlForeldreProps) => {
	if (!data || data.length === 0) return null

	return (
		<div>
			{data.length > 1 ? (
				<ErrorBoundary>
					<DollyFieldArray header="Foreldre" data={data} nested>
						{(forelder: ForelderBarnRelasjon, idx: number) => (
							<Visning forelder={forelder} idx={idx} />
						)}
					</DollyFieldArray>
				</ErrorBoundary>
			) : (
				<Visning forelder={data[0]} />
			)}
		</div>
	)
}
