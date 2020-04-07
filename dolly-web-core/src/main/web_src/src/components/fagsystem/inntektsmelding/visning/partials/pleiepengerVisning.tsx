import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

interface PleiepengerVisning {
	data?: Array<PleiepengePeriode>
}

type PleiepengePeriode = {
	fom?: string
	tom?: string
}

export default ({ data }: PleiepengerVisning) => {
	if (!data || data.length < 1) return null
	return (
		<DollyFieldArray data={data} header="Pleiepenger">
			{(id: PleiepengePeriode) => (
				<>
					<div className="person-visning_content">
						<TitleValue title="Fra og med dato" value={Formatters.formatDate(id.fom)} />
						<TitleValue title="Til og med dato" value={Formatters.formatDate(id.tom)} />
					</div>
				</>
			)}
		</DollyFieldArray>
	)
}
