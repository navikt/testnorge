import React from 'react'
import Kodeverk from '~/utils/kodeverkMapper'
import Formatters from '~/utils/DataFormatter'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Utenlandsopphold = ({ data }) => {
	if (!data) return false

	return (
		<React.Fragment>
			<h4>Utenlandsopphold</h4>
			<DollyFieldArray data={data} nested>
				{(id, idx) => (
					<div key={idx} className="person-visning_content">
						<TitleValue title="Land" value={id.landkode} kodeverk={Kodeverk.utenlandsoppholdLand} />
						{id.periode && (
							<TitleValue title="Startdato" value={Formatters.formatStringDates(id.periode.fom)} />
						)}
						{id.periode && (
							<TitleValue title="Sluttdato" value={Formatters.formatStringDates(id.periode.tom)} />
						)}
					</div>
				)}
			</DollyFieldArray>
		</React.Fragment>
	)
}
