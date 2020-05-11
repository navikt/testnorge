import React from 'react'

import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'

type ArenaResponse = {
	arbeidsokerList: [{ servicebehov?: string }]
}

type VisningData = {
	brukertype?: string
}

interface ArenaVisningProps {
	data: ArenaResponse
	loading: boolean
}

export const ArenaVisning = ({ data, loading }: ArenaVisningProps) => {
	if (loading) return <Loading label="Laster arena-data" />
	if (!data) return false

	const sortedData = Array.isArray(data.arbeidsokerList)
		? data.arbeidsokerList.slice().reverse()
		: data.arbeidsokerList

	let visningData: VisningData[]
	visningData = []

	sortedData.forEach((info, idx) => {
		visningData.push({
			brukertype: info.servicebehov ? 'Med servicebehov' : 'Uten servicebehov'
		})
	})

	return (
		<div>{
			//@ts-ignore
			<SubOverskrift label="Arena" />}
			<div className="person-visning_content">
				<DollyFieldArray data={visningData} nested>
					{(id: VisningData ) => (
						<TitleValue title="Brukertype" value={id.brukertype} />
					)}
				</DollyFieldArray>
			</div>
		</div>
	)
}