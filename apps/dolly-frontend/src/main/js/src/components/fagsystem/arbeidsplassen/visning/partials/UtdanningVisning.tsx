import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'

export const UtdanningVisning = ({ data }) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Utdanning" nested>
					{(utdanning) => (
						<>
							<TitleValue
								title="Utdanningsnivå"
								value={Formatters.showLabel('nusKoder', utdanning.nuskode)}
							/>
							<TitleValue title="Grad og utdanningsretning" value={utdanning.field} />
							<TitleValue title="Skole/studiested" value={utdanning.institution} />
							<TitleValue title="Beskrivelse" value={utdanning.description} size="medium" />
							<TitleValue title="Startdato" value={Formatters.formatDate(utdanning.startDate)} />
							<TitleValue title="Sluttdato" value={Formatters.formatDate(utdanning.endDate)} />
							<TitleValue
								title="Pågående utdanning"
								value={Formatters.oversettBoolean(utdanning.ongoing)}
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
