import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Formatters from '@/utils/DataFormatter'

export const FoererkortVisning = ({ data }) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Førerkort" nested>
					{(foererkort) => (
						<>
							<TitleValue
								title="Type førerkort"
								value={Formatters.showLabel('foererkortTyper', foererkort.type)}
							/>
							<TitleValue
								title="Gyldig fra"
								value={Formatters.formatDate(foererkort.acquiredDate)}
							/>
							<TitleValue title="Gyldig til" value={Formatters.formatDate(foererkort.expiryDate)} />
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
