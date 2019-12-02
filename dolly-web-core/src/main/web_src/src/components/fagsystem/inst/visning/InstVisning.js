import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

export const InstVisning = ({ data, loading }) => {
	if (loading) return <Loading label="laster inst data" />
	if (!data) return false

	return (
		<div>
			<SubOverskrift label="Institusjonsopphold" />
			{data.map((curr, idx) => (
				<div className="person-visning_content" key={idx}>
					<TitleValue title="Institusjonstype" value={data[idx].institusjonstype} />
					<TitleValue title="Start dato" value={Formatters.formatDate(data[idx].startdato)} />
					<TitleValue
						title="Slutt dato"
						value={Formatters.formatDate(data[idx].faktiskSluttdato)}
					/>
				</div>
			))}
		</div>
	)
}
