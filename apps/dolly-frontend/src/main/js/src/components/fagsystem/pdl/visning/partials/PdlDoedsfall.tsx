import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Formatters from '~/utils/DataFormatter'
import { Doedsfall } from '~/components/fagsystem/pdlf/PdlTypes'

type PdlDoedsfallTypes = {
	data: Array<Doedsfall>
}

type DoedsfallVisningTypes = {
	item: Doedsfall
	idx: number
}

const DoedsfallVisning = ({ item, idx }: DoedsfallVisningTypes) => {
	return (
		<div className="person-visning_content" key={idx}>
			<TitleValue title="Dødsdato" value={Formatters.formatDate(item.doedsdato)} />
		</div>
	)
}

export const PdlDoedsfall = ({ data }: PdlDoedsfallTypes) => {
	if (!data || data.length === 0) return null

	return (
		<div>
			<SubOverskrift label="Dødsfall" iconKind="grav" />
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={data} header="" nested>
						{(item: Doedsfall, idx: number) => <DoedsfallVisning item={item} idx={idx} />}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
