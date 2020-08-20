import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Bidiagnoser = ({ data }) => {
	if (!data || data.length === 0) return null

	return (
		<>
			<h4>Bidiagnoser</h4>
			<DollyFieldArray data={data} nested>
				{(bidiagnose, idx) => (
					<div key={idx} className="person-visning_content">
						<TitleValue title="Diagnose" value={bidiagnose.diagnose} />
						<TitleValue title="Diagnosekode" value={bidiagnose.diagnosekode} />
						{/* <TitleValue title="System" value={bidiagnose.system} /> */}
					</div>
				)}
			</DollyFieldArray>
		</>
	)
}
