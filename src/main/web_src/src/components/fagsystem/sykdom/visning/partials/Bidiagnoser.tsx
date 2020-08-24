import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

type Bidiagnoser = {
	data: Array<Diagnose>
}

type Diagnose = {
	diagnose: string
	diagnosekode: string
}

export const Bidiagnoser = ({ data }: Bidiagnoser) => {
	if (!data || data.length === 0) return null
	return (
		<>
			<h4>Bidiagnoser</h4>
			<DollyFieldArray data={data} nested>
				{(bidiagnose: Diagnose, idx: string) => (
					<div key={idx} className="person-visning_content">
						<TitleValue title="Diagnose" value={bidiagnose.diagnose} />
						<TitleValue title="Diagnosekode" value={bidiagnose.diagnosekode} />
					</div>
				)}
			</DollyFieldArray>
		</>
	)
}
