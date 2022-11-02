import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const Annet = ({ data }) => {
	const { flyktning, soeknadOmBeskyttelseUnderBehandling } = data
	if (!flyktning && !soeknadOmBeskyttelseUnderBehandling) return null

	return (
		<div>
			<h4>Annet</h4>
			<div className="person-visning_content">
				<TitleValue title="Har flyktningstatus" value={Formatters.oversettBoolean(flyktning)} />
				<TitleValue
					title="Er asylsøker"
					value={Formatters.showLabel('jaNeiUavklart', soeknadOmBeskyttelseUnderBehandling)}
				/>
			</div>
		</div>
	)
}
