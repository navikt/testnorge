import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

interface NaturalytelseVisning {
	data?: Array<Naturalytelse>
	header: string
}

type Naturalytelse = {
	beloepPrMnd?: string
	fom?: string
	naturaytelseType: string
}

export default ({ data, header }: NaturalytelseVisning) => {
	if (!data || data.length < 1) return null
	return (
		<DollyFieldArray data={data} header={header} nested>
			{(id: Naturalytelse) => (
				<>
					<div className="person-visning_content">
						<TitleValue title="Beløp per måned" value={id.beloepPrMnd} />
						<TitleValue title="Naturalytelse type" value={id.naturaytelseType} />
						<TitleValue title="Fra og med dato" value={Formatters.formatDate(id.fom)} />
					</div>
				</>
			)}
		</DollyFieldArray>
	)
}
