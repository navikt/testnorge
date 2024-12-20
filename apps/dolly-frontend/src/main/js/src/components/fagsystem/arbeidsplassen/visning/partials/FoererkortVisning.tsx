import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { Foererkort } from '@/components/fagsystem/arbeidsplassen/ArbeidsplassenTypes'

type FoererkortVisningProps = {
	data?: Array<Foererkort>
}

export const FoererkortVisning = ({ data }: FoererkortVisningProps) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Førerkort" nested>
					{(foererkort: Foererkort) => (
						<>
							<TitleValue
								title="Type førerkort"
								value={showLabel('foererkortTyper', foererkort.type)}
							/>
							<TitleValue title="Gyldig fra" value={formatDate(foererkort.acquiredDate)} />
							<TitleValue title="Gyldig til" value={formatDate(foererkort.expiryDate)} />
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
