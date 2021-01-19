import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

export const GeografiskTilknytning = ({ data, visTittel = true }) => {
	if (!data) {
		return null
	}
	return (
		<ErrorBoundary>
			<div>
				{visTittel && <SubOverskrift label="Geografisk tilknyting" iconKind="nasjonalitet" />}
				<div className="person-visning_content">
					<TitleValue title="Bydel" value={data.gtBydel} />
					<TitleValue title="Kommunenummer" value={data.gtKommune} />
					<TitleValue title="Land" value={data.gtLand} />
					<TitleValue title="Type" value={data.gtType} />
					<TitleValue title="Regel" value={data.regel} />
				</div>
			</div>
		</ErrorBoundary>
	)
}
