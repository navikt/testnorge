import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import { DoedfoedtBarn } from '~/components/fagsystem/pdlf/PdlTypes'

type DoedfoedtBarnData = {
	data: Array<DoedfoedtBarn>
}

type VisningData = {
	data: DoedfoedtBarn
}

export const Visning = ({ data }: VisningData) => (
	<>
		<ErrorBoundary>
			<div className="person-visning_content">
				<TitleValue title="Dødsdato" value={Formatters.formatDate(data.dato)} />
			</div>
		</ErrorBoundary>
	</>
)

export const DoedfoedtBarnVisning = ({ data }: DoedfoedtBarnData) => {
	if (!data || data.length < 1) return null

	return (
		<div>
			<SubOverskrift label="Dødfødt barn" iconKind="doedfoedt" />

			<DollyFieldArray data={data} nested>
				{(doedfoedtBarn: DoedfoedtBarn) => <Visning key={doedfoedtBarn.id} data={doedfoedtBarn} />}
			</DollyFieldArray>
		</div>
	)
}
