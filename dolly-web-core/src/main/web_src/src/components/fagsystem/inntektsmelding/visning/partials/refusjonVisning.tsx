import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

interface RefusjonVisning {
	data: Refusjon
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
	if (!data) return null

	return (
		<>
			<h4>Refusjon</h4>
			<TitleValue title="Refusjonsbeløp per måned" value={data.refusjonsbeloepPrMnd} />
			<TitleValue title="Opphørsdato" value={data.refusjonsopphoersdato} />
			<DollyFieldArray data={data.endringIRefusjonListe} header="Endring i refusjon">
				{(id: EndringIRefusjon) => (
					<>
						<div className="person-visning_content">
							<TitleValue title="Ednringsdato" value={id.endringsdato} />
							<TitleValue title="Refusjonsbeløp per måned" value={id.refusjonsbeloepPrMnd} />
						</div>
					</>
				)}
			</DollyFieldArray>
		</>
	)
}
