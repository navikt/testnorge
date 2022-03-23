import React from 'react'
import { hasNoValues } from '~/components/fagsystem/pdl/visning/partials/relasjoner/PdlRelasjoner'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { DoedfoedtBarn } from '~/components/fagsystem/pdlf/PdlTypes'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

type DataProps = {
	data: DoedfoedtBarnProps[]
}

type DoedfoedtBarnProps = {
	doedfoedtBarn: DoedfoedtBarn
	idx?: number
}

const DoedfoedtBarnVisning = ({ doedfoedtBarn, idx }: DoedfoedtBarnProps) => {
	return (
		<div key={idx} className="person-visning_content">
			<TitleValue title="Dødsdato" value={Formatters.formatDate(doedfoedtBarn.dato)} />
		</div>
	)
}

export const PdlDoedfoedtBarn = ({ data }: DataProps) => {
	if (hasNoValues(data)) return null

	return (
		<div>
			<SubOverskrift label="Dødfødt barn" iconKind="doedfoedt" />
			<ErrorBoundary>
				<DollyFieldArray data={data} nested>
					{(data: DoedfoedtBarn, idx: number) => (
						<DoedfoedtBarnVisning doedfoedtBarn={data} idx={idx} />
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
