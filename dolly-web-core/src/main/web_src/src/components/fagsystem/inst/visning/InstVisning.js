import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'

const getSortedData = data => {
	return Array.isArray(data)
		? data.slice().sort(function(a, b) {
				const datoA = new Date(a.startdato)
				const datoB = new Date(b.startdato)

				return datoA < datoB ? 1 : datoA > datoB ? -1 : 0
		  })
		: data
}

export const InstVisning = ({ data, loading }) => {
	if (loading) return <Loading label="laster inst data" />
	if (!data) return false

	const sortedData = getSortedData(data)

	return (
		<div>
			<SubOverskrift label="Institusjonsopphold" iconKind="institusjon" />
			<DollyFieldArray data={sortedData} nested>
				{(opphold, idx) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue
							title="Institusjonstype"
							value={Formatters.showLabel('institusjonstype', opphold.institusjonstype)}
						/>
						<TitleValue title="Startdato" value={Formatters.formatStringDates(opphold.startdato)} />
						<TitleValue
							title="Sluttdato"
							value={Formatters.formatStringDates(opphold.faktiskSluttdato)}
						/>
					</div>
				)}
			</DollyFieldArray>
		</div>
	)
}
