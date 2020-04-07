import React from 'react'
import _isEmpty from 'lodash/isEmpty'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

interface RefusjonVisning {
	data?: Refusjon
}

type Refusjon = {
	refusjonsbeloepPrMnd?: number
	refusjonsopphoersdato?: string
	endringIRefusjonListe?: Array<EndringIRefusjon>
}

type EndringIRefusjon = {
	refusjonsbeloepPrMnd?: number
	endringsdato?: string
}

export default ({ data }: RefusjonVisning) => {
	if (!data || _isEmpty(data)) return null
	return (
		<>
			<h4>Refusjon</h4>
			<div className="person-visning_content">
				<TitleValue title="Refusjonsbeløp per måned" value={data.refusjonsbeloepPrMnd} />
				<TitleValue title="Opphørsdato" value={Formatters.formatDate(data.refusjonsopphoersdato)} />
				{data.endringIRefusjonListe && (
					<DollyFieldArray data={data.endringIRefusjonListe} header="Endring i refusjon">
						{(id: EndringIRefusjon) => (
							<>
								<div className="person-visning_content">
									<TitleValue title="Ednringsdato" value={Formatters.formatDate(id.endringsdato)} />
									<TitleValue title="Refusjonsbeløp per måned" value={id.refusjonsbeloepPrMnd} />
								</div>
							</>
						)}
					</DollyFieldArray>
				)}
			</div>
		</>
	)
}
