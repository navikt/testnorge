import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'

export const AndreGodkjenningerVisning = ({ data }) => {
	if (data?.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Andre godkjenninger" nested>
					{(annenGodkjenning, idx: number) => (
						<>
							<TitleValue
								title="Annen godkjenning"
								value={Formatters.showLabel(
									'offentligGodkjenning',
									annenGodkjenning.certificateName
								)}
							/>
							<TitleValue title="Utsteder" value={annenGodkjenning.issuer} />
							<TitleValue
								title="Fullført"
								value={Formatters.formatDate(annenGodkjenning.fromDate)}
							/>
							<TitleValue title="Utløper" value={Formatters.formatDate(annenGodkjenning.toDate)} />
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
