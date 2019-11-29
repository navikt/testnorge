import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Arbeidsadgang = ({ arbeidsadgang }) => {
	if (!arbeidsadgang) return false

	const { harArbeidsAdgang, typeArbeidsadgang, arbeidsOmfang, periode } = arbeidsadgang

	return (
		<div>
			<h4>Arbeidsadgang</h4>
			<div className="person-visning_content">
				<TitleValue title="Har arbeidsadgang" value={harArbeidsAdgang} />
				<TitleValue
					title="Type arbeidsadgang"
					value={Formatters.showLabel('typeArbeidsadgang', typeArbeidsadgang)}
				/>
				<TitleValue
					title="Arbeidsomfang"
					value={Formatters.showLabel('arbeidsOmfang', arbeidsOmfang)}
				/>
				<TitleValue title="Fra dato" value={Formatters.formatStringDates(periode.fra)} />
				<TitleValue title="Til dato" value={Formatters.formatStringDates(periode.til)} />
			</div>
		</div>
	)
}
