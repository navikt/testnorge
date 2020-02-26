import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const FradragVisning = ({ data }) => {
	if (!data || data.length === 0) return false

	return (
		<React.Fragment>
			<h4>Fradrag</h4>
			<DollyFieldArray data={data} nested>
				{(id, idx) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Beløp" value={id.beloep} />
						<TitleValue title="Beskrivelse" value={id.beskrivelse} kodeverk="Fradragbeskrivelse" />
					</div>
				)}
			</DollyFieldArray>
		</React.Fragment>
	)
}
