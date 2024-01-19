import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { showLabel } from '@/utils/DataFormatter'

type OppholdAnnetStedTypes = {
	oppholdAnnetSted?: string
}

export const OppholdAnnetSted = ({ oppholdAnnetSted }: OppholdAnnetStedTypes) => {
	if (!oppholdAnnetSted) {
		return null
	}

	return (
		<TitleValue
			title="Opphold annet sted"
			value={showLabel('oppholdAnnetSted', oppholdAnnetSted)}
		/>
	)
}
