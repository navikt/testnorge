import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'

export const AndreGodkjenningerVisning = ({ data }) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Andre godkjenninger" nested>
					{(annenGodkjenning) => (
						<>
							<TitleValue
								title="Annen godkjenning"
								value={showLabel('offentligGodkjenning', annenGodkjenning.certificateName)}
							/>
							<TitleValue title="Utsteder" value={annenGodkjenning.issuer} />
							<TitleValue title="Fullført" value={formatDate(annenGodkjenning.fromDate)} />
							<TitleValue title="Utløper" value={formatDate(annenGodkjenning.toDate)} />
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
