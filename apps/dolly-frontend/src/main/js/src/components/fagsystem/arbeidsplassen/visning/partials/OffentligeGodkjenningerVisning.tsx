import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'

export const OffentligeGodkjenningerVisning = ({ data }) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Offentlige godkjenninger" nested>
					{(offentligGodkjenning, idx: number) => (
						<>
							<TitleValue
								title="Offentlig godkjenning"
								value={Formatters.showLabel('offentligGodkjenning', offentligGodkjenning.title)}
							/>
							<TitleValue title="Utsteder" value={offentligGodkjenning.issuer} />
							<TitleValue
								title="Fullført"
								value={Formatters.formatDate(offentligGodkjenning.fromDate)}
							/>
							<TitleValue
								title="Utløper"
								value={Formatters.formatDate(offentligGodkjenning.toDate)}
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
