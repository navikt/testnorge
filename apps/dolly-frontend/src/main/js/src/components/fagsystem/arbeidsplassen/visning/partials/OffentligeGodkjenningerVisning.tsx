import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'

export const OffentligeGodkjenningerVisning = ({ data }) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Offentlige godkjenninger" nested>
					{(offentligGodkjenning) => (
						<>
							<TitleValue
								title="Offentlig godkjenning"
								value={showLabel('offentligGodkjenning', offentligGodkjenning.title)}
							/>
							<TitleValue title="Utsteder" value={offentligGodkjenning.issuer} />
							<TitleValue title="Fullført" value={formatDate(offentligGodkjenning.fromDate)} />
							<TitleValue title="Utløper" value={formatDate(offentligGodkjenning.toDate)} />
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
