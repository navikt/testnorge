import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import {
	DoedfoedtBarn,
	ForelderBarnRelasjon,
} from '~/pages/gruppe/PersonVisning/PersonMiljoeinfo/PdlDataTyper'
import React from 'react'
import { hasNoValues } from '~/components/fagsystem/pdl/visning/partials/PdlRelasjoner'
import Formatters from '~/utils/DataFormatter'

type BarnProps = {
	barn: ForelderBarnRelasjon
	idx?: number
}

type DoedfoedtBarnProps = {
	doedfoedtBarn: DoedfoedtBarn
	idx?: number
}

type PdlBarnProps = {
	barn: ForelderBarnRelasjon[]
	doedfoedtBarn: DoedfoedtBarn[]
}

const BarnVisning = ({ barn, idx }: BarnProps) => {
	return (
		<div key={idx} className="person-visning_content">
			<TitleValue title="Ident" value={barn.relatertPersonsIdent} />
			<TitleValue title="Forhold til barn" value={barn.minRolleForPerson} size="medium" />
		</div>
	)
}

const DoedfoedtBarnVisning = ({ doedfoedtBarn, idx }: DoedfoedtBarnProps) => {
	return (
		<div key={idx} className="person-visning_content">
			<TitleValue title="Dato" value={Formatters.formatDate(doedfoedtBarn.dato)} />
		</div>
	)
}

export const PdlBarn = ({ barn, doedfoedtBarn }: PdlBarnProps) => {
	if (hasNoValues(barn) && hasNoValues(doedfoedtBarn)) return null

	return (
		<div>
			<ErrorBoundary>
				{!hasNoValues(barn) && (
					<DollyFieldArray header="Barn" data={barn} nested>
						{(data: ForelderBarnRelasjon, idx: number) => <BarnVisning barn={data} idx={idx} />}
					</DollyFieldArray>
				)}
				{!hasNoValues(doedfoedtBarn) && (
					<DollyFieldArray header="Dødfødt barn" data={doedfoedtBarn} nested>
						{(data: DoedfoedtBarn, idx: number) => (
							<DoedfoedtBarnVisning doedfoedtBarn={data} idx={idx} />
						)}
					</DollyFieldArray>
				)}
			</ErrorBoundary>
		</div>
	)
}
