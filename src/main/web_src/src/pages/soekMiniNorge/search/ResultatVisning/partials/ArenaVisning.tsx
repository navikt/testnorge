import React from 'react'

import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

type ArenaResponse = {
	arbeidsokerList: [{ servicebehov?: string }]
}

type VisningData = {
	brukertype: string
}

interface ArenaVisningProps {
	data: ArenaResponse
	loading: boolean
}

export const ArenaVisning = ({ data, loading }: ArenaVisningProps) => {
	if (loading) return <Loading label="Laster arena-data" />

	const sortedData = Array.isArray(data.arbeidsokerList)
		? data.arbeidsokerList.slice().reverse()
		: data.arbeidsokerList

	const visningData: VisningData[] = sortedData.map(info => ({
		brukertype: info.servicebehov ? 'Med servicebehov' : 'Uten servicebehov'
	}))

	return (
		<div>
			{
				//@ts-ignore
				<SubOverskrift label="Arena" />
			}
			<div className="person-visning_content">
				<ErrorBoundary>
					<DollyFieldArray data={visningData} nested>
						{(id: VisningData) => <TitleValue title="Brukertype" value={id.brukertype} />}
					</DollyFieldArray>
				</ErrorBoundary>
			</div>
		</div>
	)
}
