import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'

export const PermisjonPermitteringer = ({ data }) => {
	if (!data) return false

	return (
		<div>
			<h4>Permisjon</h4>
			<div className="person-visning_content">
				{data.map((id, idx) => (
					<div key={idx}>
						{id.periode && (
							<TitleValue title="Startdato" value={Formatters.formatStringDates(id.periode.fom)} />
						)}
						{id.periode && (
							<TitleValue title="Sluttdato" value={Formatters.formatStringDates(id.periode.tom)} />
						)}
						<TitleValue title="Permisjonsprosent" value={id.prosent} />
					</div>
				))}
			</div>
		</div>
	)
}
