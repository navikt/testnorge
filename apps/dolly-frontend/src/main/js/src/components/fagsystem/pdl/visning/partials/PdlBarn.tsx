import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ForelderBarnRelasjon } from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import React from 'react'

type VisningProps = {
	barn: ForelderBarnRelasjon
	idx?: number
}

type PdlBarnProps = {
	data: ForelderBarnRelasjon[]
}

const Visning = ({ barn, idx }: VisningProps) => {
	return (
		<div key={idx} className="person-visning_content">
			<TitleValue title="Ident" value={barn.relatertPersonsIdent} />
			<TitleValue title="Forhold til barn" value={barn.minRolleForPerson} size="medium" />
		</div>
	)
}

export const PdlBarn = ({ data }: PdlBarnProps) => {
	if (!data || data.length === 0) return null

	return (
		<div>
			{data.length > 1 ? (
				<ErrorBoundary>
					<DollyFieldArray header="Barn" data={data} nested>
						{(barn: ForelderBarnRelasjon, idx: number) => <Visning barn={barn} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			) : (
				<Visning barn={data[0]} />
			)}
		</div>
	)
}
