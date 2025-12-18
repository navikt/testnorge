import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import { Utdanning } from '@/components/fagsystem/arbeidsplassen/ArbeidsplassenTypes'

type UtdanningVisningProps = {
	data?: Array<Utdanning>
}

export const UtdanningVisning = ({ data }: UtdanningVisningProps) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Utdanning" nested>
					{(utdanning: Utdanning) => (
						<>
							<TitleValue title="Utdanningsnivå" value={showLabel('nusKoder', utdanning.nuskode)} />
							<TitleValue title="Grad og utdanningsretning" value={utdanning.field} />
							<TitleValue title="Skole/studiested" value={utdanning.institution} />
							<TitleValue title="Beskrivelse" value={utdanning.description} size="medium" />
							<TitleValue title="Startdato" value={formatDate(utdanning.startDate)} />
							<TitleValue title="Sluttdato" value={formatDate(utdanning.endDate)} />
							<TitleValue title="Pågående utdanning" value={oversettBoolean(utdanning.ongoing)} />
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
