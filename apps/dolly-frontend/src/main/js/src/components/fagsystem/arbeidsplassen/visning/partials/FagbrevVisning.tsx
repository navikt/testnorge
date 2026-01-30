import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { showLabel } from '@/utils/DataFormatter'
import { Fagbrev } from '@/components/fagsystem/arbeidsplassen/ArbeidsplassenTypes'

type FagbrevVisningProps = {
	data?: Array<Fagbrev>
}

export const FagbrevVisning = ({ data }: FagbrevVisningProps) => {
	if (!data || data.length < 1) {
		return null
	}

	return (
		<div className="person-visning_content" style={{ marginTop: '-15px' }}>
			<ErrorBoundary>
				<DollyFieldArray data={data} header="Fagbrev" nested>
					{(fagbrev: Fagbrev) => (
						<>
							<TitleValue
								title="Fagdokumentasjon"
								value={showLabel('fagbrev', fagbrev.title)}
								size="medium"
							/>
						</>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
