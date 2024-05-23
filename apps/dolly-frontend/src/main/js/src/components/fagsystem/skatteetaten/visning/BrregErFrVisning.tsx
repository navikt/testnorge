import Panel from '@/components/ui/panel/Panel'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'

export const BrregErFrVisning = ({ harDagligLederRolle }: any) => {
	if (!harDagligLederRolle) {
		return null
	}

	return (
		<Panel heading="Enhetsregisteret og Foretaksregisteret">
			<div className="person-visning_content">
				<TitleValue title="Roller" value="Daglig leder" />
			</div>
		</Panel>
	)
}
