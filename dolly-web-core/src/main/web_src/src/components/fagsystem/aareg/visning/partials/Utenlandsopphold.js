import React from 'react'
import Loading from '~/components/ui/loading/Loading'
import Formatters from '~/utils/DataFormatter'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Utenlandsopphold = ({ data, loading }) => {
	if (loading) return <Loading label="laster Aareg-data" />
	if (!data) return false

	return (
		<div>
			<h4>Utenlandsopphold</h4>
			{data.map((id, idx) => (
				<div key={idx} className="person-visning_content">
					<TitleValue title="Land" value={id.landkode} kodeverk="LandkoderISO2" />
					{id.periode && (
						<TitleValue title="Startdato" value={Formatters.formatStringDates(id.periode.fom)} />
					)}
					{id.periode && (
						<TitleValue title="Sluttdato" value={Formatters.formatStringDates(id.periode.tom)} />
					)}
				</div>
			))}
		</div>
	)
}
